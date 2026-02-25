package ru.optimist.coreboost;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Выполняется до старта Minecraft-классов.
 * Здесь безопасно настраивать system properties, которые читаются на ранних этапах игры.
 */
public final class CoreBoostPreLaunch implements PreLaunchEntrypoint {
    private static final String PROFILE_PROPERTY = "coreboost.profile";
    private static final String PROFILE_ENV = "COREBOOST_PROFILE";

    @Override
    public void onPreLaunch() {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPlan plan = ThreadPlan.forMachine(cores, resolveProfile());

        // Читается Minecraft для пула фоновых задач (чанки, I/O и часть worldgen задач).
        setIfAbsent("max.bg.threads", Integer.toString(plan.bgThreads()));

        // Читается Netty при инициализации сетевых event-loop потоков.
        setIfAbsent("io.netty.eventLoopThreads", Integer.toString(plan.nettyThreads()));

        // Общий пул Java, который используют некоторые async-задачи модов/игры.
        setIfAbsent("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(plan.forkJoinParallelism()));
    }

    private static Profile resolveProfile() {
        String raw = System.getProperty(PROFILE_PROPERTY);
        if (raw == null || raw.isBlank()) {
            raw = System.getenv(PROFILE_ENV);
        }
        return Profile.parse(raw);
    }

    private static void setIfAbsent(String key, String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    private record ThreadPlan(int bgThreads, int nettyThreads, int forkJoinParallelism) {
        private static ThreadPlan forMachine(int cores, Profile profile) {
            int clampedCores = clamp(cores, 2, 64);
            return switch (profile) {
                case AGGRESSIVE -> aggressive(clampedCores);
                case BALANCED -> balanced(clampedCores);
                case CONSERVATIVE -> conservative(clampedCores);
            };
        }

        private static ThreadPlan aggressive(int cores) {
            int bg = clamp(cores, 2, 32);
            int netty = clamp(cores, 2, 16);
            int fj = clamp(cores - 1, 1, 32);
            return new ThreadPlan(bg, netty, fj);
        }

        private static ThreadPlan balanced(int cores) {
            int bg = clamp(cores <= 6 ? cores - 1 : cores - 2, 2, 24);
            int netty = clamp((int) Math.ceil(cores * 0.75), 2, 12);
            int fj = clamp((int) Math.floor(cores * 0.8), 1, 24);
            return new ThreadPlan(bg, netty, fj);
        }

        private static ThreadPlan conservative(int cores) {
            int bg = clamp((int) Math.ceil(cores * 0.6), 2, 16);
            int netty = clamp((int) Math.ceil(cores * 0.5), 2, 8);
            int fj = clamp((int) Math.floor(cores * 0.6), 1, 16);
            return new ThreadPlan(bg, netty, fj);
        }

        private static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    enum Profile {
        AGGRESSIVE,
        BALANCED,
        CONSERVATIVE;

        static Profile parse(String value) {
            if (value == null || value.isBlank()) {
                return BALANCED;
            }

            return switch (value.trim().toLowerCase()) {
                case "aggressive", "max", "ultra" -> AGGRESSIVE;
                case "conservative", "safe", "low" -> CONSERVATIVE;
                default -> BALANCED;
            };
        }
    }
}
