package uk.co.dekoorb.delay;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class DelayedEntry<E> implements Delayed {
    private final long targetDispatchTime;
    private final E entry;

    DelayedEntry(long targetDispatchTime, E entry) {
        this.targetDispatchTime = targetDispatchTime;
        this.entry = entry;
    }

    E getEntry() {
        return entry;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long now = System.currentTimeMillis();
        return unit.convert(targetDispatchTime - now, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        int result;
        if (delayed instanceof DelayedEntry) {
            DelayedEntry delayedEntry = (DelayedEntry) delayed;
            result = (int) ((this.targetDispatchTime) - (delayedEntry.targetDispatchTime));
        } else {
            result = 0;
        }
        return result;
    }
}
