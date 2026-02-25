package ru.optimist.coreboost;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Выполняется до старта Minecraft-классов.
 * Здесь безопасно настраивать system properties, которые читаются на ранних этапах игры.
 */
public final class CoreBoostPreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        int cores = Runtime.getRuntime().availableProcessors();
        int bgThreads = Math.max(2, cores);
        int nettyThreads = Math.max(4, cores * 2);

        // Читается Minecraft для пула фоновых задач (чанки, I/O и часть worldgen задач).
        setIfAbsent("max.bg.threads", Integer.toString(bgThreads));

        // Читается Netty при инициализации сетевых event-loop потоков.
        setIfAbsent("io.netty.eventLoopThreads", Integer.toString(nettyThreads));

        // Общий пул Java, который используют некоторые async-задачи модов/игры.
        setIfAbsent("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(Math.max(1, cores - 1)));
    }

    private static void setIfAbsent(String key, String value) {
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }
}
