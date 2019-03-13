package uk.co.dekoorb.delay;

import java.util.concurrent.DelayQueue;
import java.util.function.Consumer;

class DelayQueueProcessor<E> implements Runnable {

    private final DelayQueue<DelayedEntry<E>> queue;
    private final Consumer<E> callback;

    DelayQueueProcessor(DelayQueue<DelayedEntry<E>> queue, Consumer<E> callback) {
        this.queue = queue;
        this.callback = callback;
    }

    void processNextEntry() {
        try {
            DelayedEntry<E> delayedEntry = queue.take();
            E entry = delayedEntry.getEntry();
            callback.accept(entry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        for (;;) {
            processNextEntry();
        }
    }
}
