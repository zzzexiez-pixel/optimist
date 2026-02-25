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

        LOGGER.info("[CoreBoost] CPU cores detected: {}", cores);
        LOGGER.info("[CoreBoost] max.bg.threads={}", System.getProperty("max.bg.threads"));
        LOGGER.info("[CoreBoost] io.netty.eventLoopThreads={}", System.getProperty("io.netty.eventLoopThreads"));
        LOGGER.info("[CoreBoost] ForkJoin.common.parallelism={}", System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
        LOGGER.info("[CoreBoost] Важно: мод ускоряет многопоточные части игры, но рендер Minecraft всё ещё в основном зависит от одного главного потока/GPU.");
    }
}
