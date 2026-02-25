package ru.optimist.coreboost;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CoreBoostClientMod implements ClientModInitializer {
    public static final String MOD_ID = "coreboost";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        int cores = Runtime.getRuntime().availableProcessors();
        String profile = System.getProperty("coreboost.profile");
        if (profile == null || profile.isBlank()) {
            profile = System.getenv("COREBOOST_PROFILE");
        }
        if (profile == null || profile.isBlank()) {
            profile = "balanced (default)";
        }

        LOGGER.info("[CoreBoost] CPU cores detected: {}", cores);
        LOGGER.info("[CoreBoost] Profile: {}", profile);
        LOGGER.info("[CoreBoost] max.bg.threads={}", System.getProperty("max.bg.threads"));
        LOGGER.info("[CoreBoost] io.netty.eventLoopThreads={}", System.getProperty("io.netty.eventLoopThreads"));
        LOGGER.info("[CoreBoost] ForkJoin.common.parallelism={}", System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
        LOGGER.info("[CoreBoost] Совет: если у тебя фризы из-за перегруза CPU, попробуй COREBOOST_PROFILE=conservative.");
        LOGGER.info("[CoreBoost] Важно: мод ускоряет многопоточные части игры, но рендер Minecraft всё ещё в основном зависит от одного главного потока/GPU.");
    }
}
