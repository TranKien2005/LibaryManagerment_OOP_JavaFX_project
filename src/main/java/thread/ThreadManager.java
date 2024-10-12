package thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadManager {

    
    private static ExecutorService executorService;

    private ThreadManager() {}

    public static void execute(Runnable task) {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(1);
        }
        executorService.execute(task);
    }

    public static void shutdown() {
        executorService.shutdown();
    }
}