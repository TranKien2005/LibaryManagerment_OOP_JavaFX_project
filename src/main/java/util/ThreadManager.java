package util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Utility class to manage thread pools for different types of tasks.
 * Provides separate thread pools for general-purpose tasks and SQL-related operations.
 * 
 * <p>The class maintains two executor services:
 * <ul>
 *   <li>A fixed thread pool with 5 threads for general tasks
 *   <li>A single-thread executor for SQL operations to ensure sequential database access
 * </ul>
 * 
 * <p>All methods are thread-safe and executors are created lazily when needed.
 * 
 * @since 1.0
 */
public class ThreadManager {
    private static ExecutorService generalExecutorService;
    private static ExecutorService sqlExecutorService;

    // Private constructor to prevent instantiation
    private ThreadManager() {}

    /**
     * Executes a general task asynchronously using a fixed thread pool with 5 threads.
     * If the pool is shut down or not yet created, a new pool will be initialized.
     * 
     * <p>Tasks submitted through this method will be executed concurrently with
     * up to 5 threads running simultaneously.
     *
     * @param task the task to be executed
     * @throws NullPointerException if task is null
     * @since 1.0
     */
    public static void execute(Runnable task) {
        if (generalExecutorService == null || generalExecutorService.isShutdown()) {
            generalExecutorService = Executors.newFixedThreadPool(5); // Create a fixed thread pool with 5 threads
        }
        generalExecutorService.execute(task); // Submit the task to the pool
    }

    /**
     * Submits an SQL-related task for asynchronous execution using a single-thread executor.
     * If the executor is shut down or not yet created, a new executor will be initialized.
     * 
     * <p>Tasks submitted through this method will be executed sequentially to prevent
     * concurrent database access issues.
     *
     * @param task the task to be executed
     * @param <T> the type of the task's result
     * @return a Future representing pending completion of the task
     * @throws NullPointerException if task is null
     * @since 1.0
     */
    public static <T> Future<T> submitSqlTask(Callable<T> task) {
        if (sqlExecutorService == null || sqlExecutorService.isShutdown()) {
            sqlExecutorService = Executors.newSingleThreadExecutor(); // Create a single-thread executor for SQL tasks
        }
        return sqlExecutorService.submit(task); // Submit the task and return a Future
    }

    /**
     * Submits an SQL-related task for asynchronous execution using a single-thread executor.
     * If the executor is shut down or not yet created, a new executor will be initialized.
     * 
     * <p>Tasks submitted through this method will be executed sequentially to prevent
     * concurrent database access issues.
     *
     * @param task the task to be executed
     * @return a Future representing pending completion of the task
     * @throws NullPointerException if task is null
     * @since 1.0
     */
    public static Future<?> submitSqlTask(Runnable task) {
        if (sqlExecutorService == null || sqlExecutorService.isShutdown()) {
            sqlExecutorService = Executors.newSingleThreadExecutor(); // Create a single-thread executor for SQL tasks
        }
        return sqlExecutorService.submit(task); // Submit the task and return a Future
    }

    /**
     * Shuts down both the general and SQL executors, stopping any further tasks from being executed.
     * Already submitted tasks will be executed before the shutdown completes.
     * 
     * <p>This method initiates an orderly shutdown where previously submitted tasks are
     * executed, but no new tasks will be accepted.
     * 
     * @since 1.0
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
