package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadManager {
    private static ExecutorService generalExecutorService;
    private static ExecutorService sqlExecutorService;

    private ThreadManager() {}

    public static void execute(Runnable task) {
        if (generalExecutorService == null || generalExecutorService.isShutdown()) {
            generalExecutorService = Executors.newFixedThreadPool(5);
        }
        generalExecutorService.execute(task);
    }

    public static Future<?> submitSqlTask(Runnable task) {
        if (sqlExecutorService == null || sqlExecutorService.isShutdown()) {
            sqlExecutorService = Executors.newSingleThreadExecutor();
        }
        return sqlExecutorService.submit(task);
    }

    public static void shutdown() {
        if (generalExecutorService != null) {
            generalExecutorService.shutdown();
        }
        if (sqlExecutorService != null) {
            sqlExecutorService.shutdown();
        }
    }
}