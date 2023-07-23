package mvtproductionback.kafka;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Auther wyjq
 * @Date 2023/7/22
 **/

@Component
public class CustomThreadPool {
    private ThreadPoolExecutor executor;

    private int numThreads=4;

    private int maxNumTasks=6;

    @Resource
    private KafkaListenerEndpointRegistry registry;

    public CustomThreadPool() {
        executor = new ThreadPoolExecutor(
                numThreads,    // Core pool size
                numThreads,    // Maximum pool size
                0L,            // Keep-alive time for idle threads
                TimeUnit.MILLISECONDS, // Time unit for the keep-alive time
                new LinkedBlockingQueue<>(maxNumTasks) // Task queue
        );

        // Set a custom RejectedExecutionHandler to handle rejected tasks
        executor.setRejectedExecutionHandler((r, executor) -> {
            System.out.println("Task rejected: Thread pool is full. Waiting for an available thread...");
            registry.getListenerContainer("consumer0").pause();
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
    }

    public void submitTask(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}