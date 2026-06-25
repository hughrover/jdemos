package agent.demo.agent.dedup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ResponseTracker hash calculation and duplicate detection.
 */
class ResponseTrackerTest {

    private ResponseTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new ResponseTracker();
    }

    @Test
    void testCalculateHash_SameContent_SameHash() {
        String content = "Hello, World!";
        String hash1 = tracker.calculateHash(content);
        String hash2 = tracker.calculateHash(content);

        assertEquals(hash1, hash2, "Same content should produce same hash");
    }

    @Test
    void testCalculateHash_DifferentContent_DifferentHash() {
        String content1 = "Hello, World!";
        String content2 = "Hello, World?";
        String hash1 = tracker.calculateHash(content1);
        String hash2 = tracker.calculateHash(content2);

        assertNotEquals(hash1, hash2, "Different content should produce different hash");
    }

    @Test
    void testCalculateHash_NullContent_ReturnsNull() {
        String hash = tracker.calculateHash(null);

        assertEquals("null", hash, "Null content should return 'null'");
    }

    @Test
    void testCalculateHash_EmptyContent_ReturnsHash() {
        String hash = tracker.calculateHash("");

        assertNotNull(hash, "Empty content should return a hash");
        assertNotEquals("null", hash, "Empty content hash should not be 'null'");
    }

    @Test
    void testIsDuplicate_FirstCall_ReturnsFalse() {
        String content = "Hello, World!";
        boolean isDuplicate = tracker.isDuplicate(content);

        assertFalse(isDuplicate, "First call should not be duplicate");
    }

    @Test
    void testIsDuplicate_SecondCall_ReturnsTrue() {
        String content = "Hello, World!";
        tracker.isDuplicate(content); // First call
        boolean isDuplicate = tracker.isDuplicate(content); // Second call

        assertTrue(isDuplicate, "Second call with same content should be duplicate");
    }

    @Test
    void testIsDuplicate_DifferentContent_ReturnsFalse() {
        String content1 = "Hello, World!";
        String content2 = "Hello, World?";
        tracker.isDuplicate(content1);
        boolean isDuplicate = tracker.isDuplicate(content2);

        assertFalse(isDuplicate, "Different content should not be duplicate");
    }

    @Test
    void testRecordResponse_RecordsHash() {
        String content = "Hello, World!";
        tracker.recordResponse(content);

        String lastHash = tracker.getLastResponseHash();
        String expectedHash = tracker.calculateHash(content);

        assertEquals(expectedHash, lastHash, "Recorded hash should match calculated hash");
    }

    @Test
    void testCheckAndRecord_FirstCall_ReturnsFalse() {
        String content = "Hello, World!";
        boolean isDuplicate = tracker.checkAndRecord(content);

        assertFalse(isDuplicate, "First call should not be duplicate");
    }

    @Test
    void testCheckAndRecord_SecondCall_ReturnsTrue() {
        String content = "Hello, World!";
        tracker.checkAndRecord(content); // First call
        boolean isDuplicate = tracker.checkAndRecord(content); // Second call

        assertTrue(isDuplicate, "Second call with same content should be duplicate");
    }

    @Test
    void testDuplicateCount_IncrementsOnDuplicate() {
        String content = "Hello, World!";
        tracker.isDuplicate(content); // First call, not duplicate
        tracker.isDuplicate(content); // Second call, duplicate
        tracker.isDuplicate(content); // Third call, duplicate

        assertEquals(2, tracker.getDuplicateCount(), "Duplicate count should be 2");
    }

    @Test
    void testReset_ClearsState() {
        String content = "Hello, World!";
        tracker.isDuplicate(content);
        tracker.isDuplicate(content); // Duplicate

        tracker.reset();

        assertNull(tracker.getLastResponseHash(), "Last hash should be null after reset");
        assertEquals(0, tracker.getDuplicateCount(), "Duplicate count should be 0 after reset");
    }

    @Test
    void testToString_ContainsHashAndCount() {
        String content = "Hello, World!";
        tracker.isDuplicate(content);
        tracker.isDuplicate(content); // Duplicate

        String toString = tracker.toString();

        assertTrue(toString.contains("hash="), "toString should contain hash");
        assertTrue(toString.contains("duplicates=1"), "toString should contain duplicate count");
    }
}
