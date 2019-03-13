package uk.co.dekoorb.delay;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DelayedEntryTest {

    private final long MOCK_TIMESTAMP = 9999999999999L;
    private final Long MOCK_ENTRY = Long.valueOf("42");

    private DelayedEntry<Long> delayedEntry;

    @Before
    public void setUp() {
        delayedEntry = new DelayedEntry<>(MOCK_TIMESTAMP, MOCK_ENTRY);
    }

    @Test
    public void getEntry() {
        Long entry = delayedEntry.getEntry();
        assertEquals(MOCK_ENTRY, entry);
    }

    @Test
    public void getDelay() {
        long now = System.currentTimeMillis();
        long delay = delayedEntry.getDelay(TimeUnit.MILLISECONDS);
        assertTrue(Math.abs(now - MOCK_TIMESTAMP + delay) < 2);
    }

    @Test
    public void compareTo() {
        final long MOCK_DELAY = 5000;
        DelayedEntry<Long> other = new DelayedEntry<>(MOCK_TIMESTAMP - MOCK_DELAY, MOCK_ENTRY);
        int result = delayedEntry.compareTo(other);
        assertEquals(MOCK_DELAY, result);
    }
}