package uk.co.dekoorb.delay;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DelayedQueue<E> {

    private final DelayQueue<DelayedEntry<E>> queue;
    private final long delay;

    public DelayedQueue(int queueRunnerCount, long delay, Consumer<E> callback) {
        this.delay = delay;
        this.queue = new DelayQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(queueRunnerCount);
        for (int i = 0; i < queueRunnerCount; i++) {
            executor.execute(new DelayQueueProcessor<>(queue, callback));
        }
        executor.shutdown();
    }

    public void add(E entry) {
        long targetDispatchTime = System.currentTimeMillis() + delay;
        DelayedEntry<E> delayedEntry = new DelayedEntry<>(targetDispatchTime, entry);
        queue.offer(delayedEntry);
    }
}
