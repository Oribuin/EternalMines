package xyz.oribuin.eternalmines.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public final class SchedulerUtil {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            4,
            (new ThreadFactoryBuilder().setNameFormat("EternalMines-%d").build())
    );

    public static CompletableFuture<Void> async(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, scheduler);
    }

    public static ScheduledFuture<?> delayedTask(Runnable runnable, long delay) {
        return scheduler.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> repeatingTask(Runnable runnable, long delay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(runnable, delay, period, unit);
    }

}
