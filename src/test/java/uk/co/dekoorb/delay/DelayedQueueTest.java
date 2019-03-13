package uk.co.dekoorb.delay;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DelayedQueueTest {

    private static final long MOCK_ZERO_DELAY = 0;
    private static final long MOCK_SHORT_DELAY = 1000;
    private static final long AVERAGE_DELAY_THRESHOLD = 10;

    private long delayDifference;
    private CountDownLatch lock;

    @Test
    public void zeroDelayAdd() throws InterruptedException {
        delayCheck(MOCK_ZERO_DELAY);
    }

    @Test
    public void shortDelayAdd() throws InterruptedException {
        delayCheck(MOCK_SHORT_DELAY);
    }

    @Test
    public void highSpeedShortDelayManyThreads() throws InterruptedException {
        multiMessageTest(100, 36000, 2, 10);
    }

    @Test
    public void highSpeedShortDelayOneThread() throws InterruptedException {
        multiMessageTest(100, 36000, 2, 1);
    }

    @Test
    public void highSpeedLongDelayManyThreads() throws InterruptedException {
        multiMessageTest(5000, 36000, 2, 10);
    }

    @Test
    public void highSpeedLongDelayOneThread() throws InterruptedException {
        multiMessageTest(5000, 36000, 2, 1);
    }

    @Test
    public void lowSpeedShortDelayManyThreads() throws InterruptedException {
        multiMessageTest(100, 120, 4, 10);
    }

    @Test
    public void lowSpeedShortDelayOneThread() throws InterruptedException {
        multiMessageTest(100, 120, 4, 1);
    }

    @Test
    public void lowSpeedLongDelayManyThreads() throws InterruptedException {
        multiMessageTest(5000, 120, 4, 10);
    }

    @Test
    public void lowSpeedLongDelayOneThread() throws InterruptedException {
        multiMessageTest(5000, 120, 4, 1);
    }

    private void multiMessageTest(long testDelay, int messagesPerMinute,
                                  int testDurationSeconds, int queueRunnerCount) throws InterruptedException {
        final int messageCount = (int) (messagesPerMinute / 60.0 * testDurationSeconds);

        final List<Long> delays = Collections.synchronizedList(new ArrayList<>());
        final List<Long> entriesIn = Collections.synchronizedList(new ArrayList<>());
        final List<Long> entriesOut = Collections.synchronizedList(new ArrayList<>());

        lock = new CountDownLatch(messageCount);

        DelayedQueue<Long> delayedQueue = new DelayedQueue<>(queueRunnerCount, testDelay, startTime -> {
            long now = System.currentTimeMillis();
            long delay = now - startTime;
            delays.add(delay);
            entriesOut.add(startTime);
            lock.countDown();
        });

        long entry;
        for (int i = 0; i < messageCount; i++) {
            entry = System.currentTimeMillis();
            entriesIn.add(entry);
            delayedQueue.add(entry);
            TimeUnit.MILLISECONDS.sleep(60000 / messagesPerMinute);
        }

        lock.await((testDelay * 2) + (testDurationSeconds * 1000), TimeUnit.MILLISECONDS);

        // Check messages were delayed
        assertEquals(messageCount, delays.size());
        for (Long delay : delays) {
            assertTrue("Delay too short:" + delay, delay >= testDelay);
        }

        // Check the average delay is within tolerance
        double delayAverage = delays.stream().mapToLong(Long::longValue).average().orElseThrow(RuntimeException::new);
        System.out.printf("Average Delay (%d msgs): %.3fms%n", messageCount, delayAverage);
        assertTrue(delayAverage - testDelay <= AVERAGE_DELAY_THRESHOLD);

        assertEquals("Messages in count != Messages out count:", entriesIn.size(), entriesOut.size());
        System.out.printf("Messages are in order? %s%n", entriesIn.equals(entriesOut) ? "Yes" : "No");
    }

    private void delayCheck(long delay) throws InterruptedException {
        lock = new CountDownLatch(1);
        delayDifference = Integer.MAX_VALUE;
        DelayedQueue<Long> delayedQueue = new DelayedQueue<>(1, delay, startTime -> {
            long now = System.currentTimeMillis();
            long actualDelay = now - startTime;
            DelayedQueueTest.this.delayDifference = Math.abs(actualDelay - delay);
            lock.countDown();
        });
        delayedQueue.add(System.currentTimeMillis());
        lock.await(5, TimeUnit.SECONDS);
        assertTrue("Delay difference was:" + delayDifference, delayDifference <= AVERAGE_DELAY_THRESHOLD);
    }
}