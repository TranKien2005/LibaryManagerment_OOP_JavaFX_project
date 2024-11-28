package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Utility class to manage thread pools for different types of tasks.
 * Provides methods to execute tasks on different executors: general tasks and SQL-related tasks.
 */
public class ThreadManager {
    private static ExecutorService generalExecutorService;
    private static ExecutorService sqlExecutorService;

    // Private constructor to prevent instantiation
    private ThreadManager() {}

    /**
     * Executes a general task asynchronously using a fixed thread pool with 5 threads.
     * If the pool is shut down, a new pool is created and the task is submitted for execution.
     *
     * @param task the task to be executed
     */
    public static void execute(Runnable task) {
        if (generalExecutorService == null || generalExecutorService.isShutdown()) {
            generalExecutorService = Executors.newFixedThreadPool(5); // Create a fixed thread pool with 5 threads
        }
        generalExecutorService.execute(task); // Submit the task to the pool
    }

    /**
     * Submits an SQL-related task for asynchronous execution using a single-thread executor.
     * If the executor is shut down, a new single-thread executor is created and the task is submitted.
     *
     * @param task the task to be executed
     * @return a Future representing the result of the asynchronous computation
     */
    public static Future<?> submitSqlTask(Runnable task) {
        if (sqlExecutorService == null || sqlExecutorService.isShutdown()) {
            sqlExecutorService = Executors.newSingleThreadExecutor(); // Create a single-thread executor for SQL tasks
        }
        return sqlExecutorService.submit(task); // Submit the task and return a Future
    }

    /**
     * Shuts down both the general and SQL executors, stopping any further tasks from being executed.
     * Gracefully shuts down each executor.
     */
    public static void shutdown() {
        if (generalExecutorService != null) {
            generalExecutorService.shutdown(); // Gracefully shut down the general executor service
        }
        if (sqlExecutorService != null) {
            sqlExecutorService.shutdown(); // Gracefully shut down the SQL executor service
        }
    }
}
