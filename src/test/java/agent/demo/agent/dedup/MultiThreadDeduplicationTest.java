package agent.demo.agent.dedup;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for multi-threaded response deduplication scenarios.
 */
class MultiThreadDeduplicationTest {

    @Test
    void testMultiThread_IndependentTrackers() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger duplicateCount = new AtomicInteger(0);
        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to start

                    // Each thread gets its own tracker
                    ResponseTracker tracker = ResponseTrackerHolder.getTracker();

                    // Each thread records its own response
                    String content = "Thread " + threadId + " response";
                    tracker.recordResponse(content);

                    // Verify isolation - other threads' content should not be duplicate
                    String otherContent = "Thread " + ((threadId + 1) % threadCount) + " response";
                    if (tracker.isDuplicate(otherContent)) {
                        duplicateCount.incrementAndGet();
                    }

                    // Verify same content is duplicate
                    if (!tracker.isDuplicate(content)) {
                        error.set(new AssertionError("Same content should be duplicate in same thread"));
                    }

                } catch (Exception e) {
                    error.set(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads
        doneLatch.await(); // Wait for all threads to finish
        executor.shutdown();

        assertNull(error.get(), "No errors should occur");
        assertEquals(0, duplicateCount.get(), "No cross-thread duplicates should be detected");
    }

    @Test
    void testMultiThread_ConcurrentDuplicateDetection() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger totalDuplicates = new AtomicInteger(0);
        AtomicReference<Throwable> error = new AtomicReference<>();

        String sharedContent = "Shared response content";

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    ResponseTracker tracker = ResponseTrackerHolder.getTracker();
                    int localDuplicates = 0;

                    for (int j = 0; j < iterationsPerThread; j++) {
                        if (tracker.isDuplicate(sharedContent)) {
                            localDuplicates++;
                        } else {
                            tracker.recordResponse(sharedContent);
                        }
                    }

                    totalDuplicates.addAndGet(localDuplicates);

                } catch (Exception e) {
                    error.set(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertNull(error.get(), "No errors should occur");

        // Each thread should have (iterationsPerThread - 1) duplicates
        // because the first call is not a duplicate
        int expectedDuplicates = threadCount * (iterationsPerThread - 1);
        assertEquals(expectedDuplicates, totalDuplicates.get(),
                "Each thread should detect duplicates for repeated content");
    }

    @Test
    void testMultiThread_ThreadLocalCleanup() throws InterruptedException {
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    // Get tracker
                    ResponseTracker tracker = ResponseTrackerHolder.getTracker();

                    // Record a response
                    String content = "Thread " + threadId + " response";
                    tracker.recordResponse(content);

                    // Verify state
                    assertNotNull(tracker.getLastResponseHash(), "Hash should not be null");
                    assertEquals(0, tracker.getDuplicateCount(), "Should have 0 duplicates");

                    // Remove tracker
                    ResponseTrackerHolder.removeTracker();

                    // Get new tracker (should be fresh)
                    ResponseTracker newTracker = ResponseTrackerHolder.getTracker();
                    assertNull(newTracker.getLastResponseHash(), "New tracker should have no hash");

                } catch (Exception e) {
                    error.set(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertNull(error.get(), "No errors should occur");
    }

    @Test
    void testMultiThread_ResetTracker() throws InterruptedException {
        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicReference<Throwable> error = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    ResponseTracker tracker = ResponseTrackerHolder.getTracker();

                    // Record a response
                    String content = "Thread " + threadId + " response";
                    tracker.recordResponse(content);

                    // Make it duplicate
                    tracker.isDuplicate(content);
                    assertEquals(1, tracker.getDuplicateCount(), "Should have 1 duplicate");

                    // Reset tracker
                    ResponseTrackerHolder.resetTracker();

                    // Verify reset
                    assertNull(tracker.getLastResponseHash(), "Hash should be null after reset");
                    assertEquals(0, tracker.getDuplicateCount(), "Duplicate count should be 0 after reset");

                } catch (Exception e) {
                    error.set(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertNull(error.get(), "No errors should occur");
    }
}
