# CoreBoost (Fabric, Minecraft 1.21.1)

Мод для Fabric, который на раннем этапе запуска настраивает ключевые пулы потоков под число ядер CPU и выбранный профиль производительности:

- `max.bg.threads` — фоновые потоки Minecraft (чанки/часть worldgen/I-O).
- `io.netty.eventLoopThreads` — сетевые event-loop потоки Netty.
- `java.util.concurrent.ForkJoinPool.common.parallelism` — общий Java-пул async-задач.

> ⚠️ Важно: Minecraft нельзя «полностью распараллелить» модом. Рендер и часть game loop остаются в основном однопоточными. Этот мод ускоряет только многопоточные участки и обычно уменьшает просадки/микрофризы, а не делает FPS буквально «x10» на любой сборке.

## Профили CoreBoost

По умолчанию используется `balanced`.

- `aggressive` — максимум фонового параллелизма для мощных CPU.
- `balanced` — дефолтный режим, более ровный баланс FPS и фоновых задач.
- `conservative` — меньше фоновых потоков, чтобы освободить CPU под рендер.

Как включить профиль:

```bash
# JVM property
-Dcoreboost.profile=aggressive

# или переменная окружения
COREBOOST_PROFILE=conservative
```

## Сборка

```bash
./gradlew build
```

Готовый jar: `build/libs/coreboost-<version>.jar`

## Установка

1. Установи Fabric Loader для Minecraft 1.21.1.
2. Положи jar мода в папку `mods`.
3. Запусти игру и проверь лог — мод печатает применённые значения потоков и активный профиль.

## Рекомендуемые JVM параметры (дополнительно)

```bash
-Xms4G -Xmx4G -XX:+UseZGC -XX:+ZGenerational
```

(Подбирай под свою систему.)
