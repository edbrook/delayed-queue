package uk.co.dekoorb.delay;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.DelayQueue;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class DelayQueueProcessorTest {

    private final Long MOCK_ENTRY = 1234567890L;

    private DelayedEntry<Long> mockDelayEntry;
    private DelayQueue<DelayedEntry<Long>> mockDelayQueue;
    private Consumer<Long> mockConsumer;

    private DelayQueueProcessor<Long> delayQueueProcessor;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        mockDelayEntry = mock(DelayedEntry.class);
        mockDelayQueue = mock(DelayQueue.class);
        mockConsumer = mock(Consumer.class);

        delayQueueProcessor = new DelayQueueProcessor(mockDelayQueue, mockConsumer);
    }

    @Test
    public void processEntry() throws InterruptedException {
        when(mockDelayQueue.take()).thenReturn(mockDelayEntry);
        when(mockDelayEntry.getEntry()).thenReturn(MOCK_ENTRY);

        delayQueueProcessor.processNextEntry();

        verify(mockDelayQueue).take();
        verify(mockDelayEntry).getEntry();
        verify(mockConsumer).accept(MOCK_ENTRY);
    }
}