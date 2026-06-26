package agent.demo.agent.dedup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Tracks responses to detect and prevent duplicate output.
 * Uses SHA-256 hashing for exact-match comparison, plus content-inclusion
 * and edit-distance similarity for detecting near-duplicate responses
 * that are progressively refined across ReactAgent thinking loops.
 */
public class ResponseTracker {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTracker.class);

    /** Minimum similarity ratio (0.0-1.0) above which two responses are considered duplicates. */
    private static final double SIMILARITY_THRESHOLD = 0.85;

    /** Responses shorter than this many chars will use exact-match only. */
    private static final int MIN_LENGTH_FOR_SIMILARITY = 50;

    private String lastResponseHash;
    private String lastResponseContent;
    private String lastResponseNormalized;
    private int duplicateCount;

    /**
     * Calculate SHA-256 hash of response content.
     *
     * @param content the response content to hash
     * @return hex string of the hash (first 16 chars for brevity)
     */
    public String calculateHash(String content) {
        if (content == null) {
            return "null";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not available", e);
            return "error-" + content.hashCode();
        }
    }

    /**
     * Normalize content for comparison: trim, collapse whitespace, lowercase.
     */
    private String normalize(String content) {
        if (content == null) return "";
        return content.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    /**
     * Check if a response should be suppressed (truly redundant).
     * Returns true only when the new content adds no new information:
     *   - Exact hash match (byte-for-byte identical)
     *   - New content is fully contained within the last response (subset)
     *   - High similarity ratio with no progressive extension (fuzzy near-duplicate)
     *
     * IMPORTANT: Does NOT consider "progressive refinement" (new content
     * extends old content) as a duplicate, because the longer version is
     * more complete and should reach the user.
     *
     * @param content the response content to check
     * @return true if this response should be suppressed
     */
    public boolean isDuplicate(String content) {
        if (content == null) {
            return lastResponseHash == null;
        }

        // 1. Exact hash match (fast path, byte-for-byte identical)
        String hash = calculateHash(content);
        if (hash.equals(lastResponseHash)) {
            duplicateCount++;
            logger.warn("[DEDUP] Exact duplicate detected (#{}). Hash: {}, length: {}",
                    duplicateCount, hash, content.length());
            return true;
        }

        if (lastResponseContent == null) {
            return false;
        }

        String newNorm = normalize(content);
        String lastNorm = lastResponseNormalized;

        if (lastNorm.isEmpty() || newNorm.isEmpty()) {
            return false;
        }

        // 2. New content is entirely contained within the last response (subset/regression)
        if (lastNorm.contains(newNorm)) {
            duplicateCount++;
            logger.warn("[DEDUP] Subset duplicate detected (#{}): new content is contained within last response. " +
                            "Last length: {}, new length: {}",
                    duplicateCount, lastResponseContent.length(), content.length());
            logger.debug("[DEDUP] New content preview: {}",
                    content.substring(0, Math.min(200, content.length())));
            return true;
        }

        // 3. Fuzzy similarity check (LCS ratio ≥ threshold) for longer texts.
        //    Skip if this is a progressive extension (new extends the old one).
        if (content.length() >= MIN_LENGTH_FOR_SIMILARITY
                && lastResponseContent.length() >= MIN_LENGTH_FOR_SIMILARITY
                && !(newNorm.length() > lastNorm.length() && newNorm.startsWith(lastNorm))) {
            double similarity = calculateSimilarity(newNorm, lastNorm);
            if (similarity >= SIMILARITY_THRESHOLD) {
                duplicateCount++;
                logger.warn("[DEDUP] Similar duplicate detected (#{}): similarity ratio {:.2f}. " +
                                "Last length: {}, new length: {}",
                        duplicateCount, similarity, lastResponseContent.length(), content.length());
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the new content is a progressive refinement of the last response
     * (i.e., contains all of the last response's text and adds more).
     * Used by the interceptor to decide whether to update the tracked record
     * without suppressing the new content.
     *
     * @param content the response content to check
     * @return true if new content extends (is a superset of) the previous response
     */
    public boolean isProgressiveExtension(String content) {
        if (lastResponseContent == null || content == null) {
            return false;
        }
        String newNorm = normalize(content);
        String lastNorm = lastResponseNormalized;
        return newNorm.length() > lastNorm.length() && newNorm.startsWith(lastNorm);
    }

    /**
     * Calculate similarity ratio between two normalized strings using
     * longest common subsequence (LCS) divided by the longer length.
     * Returns a value between 0.0 (completely different) and 1.0 (identical).
     */
    private double calculateSimilarity(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;

        // Use shorter-first LCS for efficiency
        String shorter = a.length() <= b.length() ? a : b;
        String longer = a.length() > b.length() ? a : b;

        int lcsLen = longestCommonSubsequence(shorter, longer);
        return (double) lcsLen / longer.length();
    }

    /**
     * Compute the length of the longest common subsequence between two strings
     * using space-optimized DP (O(min(n,m)) memory).
     */
    private int longestCommonSubsequence(String s1, String s2) {
        // Ensure s1 is the shorter one
        if (s1.length() > s2.length()) {
            String tmp = s1;
            s1 = s2;
            s2 = tmp;
        }
        int m = s1.length();
        int n = s2.length();
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s2.charAt(i - 1) == s1.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }
        return prev[m];
    }

    /**
     * Record a response as the latest response.
     * The caller (interceptor) is responsible for deciding whether to call this
     * method or to suppress the response.
     *
     * @param content the response content to record
     */
    public void recordResponse(String content) {
        if (content == null) {
            lastResponseHash = "null";
            lastResponseContent = null;
            lastResponseNormalized = null;
        } else {
            lastResponseHash = calculateHash(content);
            lastResponseContent = content;
            lastResponseNormalized = normalize(content);
        }
        logger.debug("[DEDUP] Recorded response. Hash: {}, content length: {}",
                lastResponseHash, content != null ? content.length() : 0);
    }

    /**
     * Check if a response is a duplicate and record it if not.
     * This is a convenience method combining isDuplicate and recordResponse.
     *
     * @param content the response content to check and record
     * @return true if this was a duplicate response
     */
    public boolean checkAndRecord(String content) {
        boolean duplicate = isDuplicate(content);
        if (!duplicate) {
            recordResponse(content);
        }
        return duplicate;
    }

    /**
     * Get the hash of the last recorded response.
     *
     * @return the last response hash, or null if no response recorded
     */
    public String getLastResponseHash() {
        return lastResponseHash;
    }

    /**
     * Get the number of duplicate responses detected.
     *
     * @return the duplicate count
     */
    public int getDuplicateCount() {
        return duplicateCount;
    }

    /**
     * Reset the tracker state.
     */
    public void reset() {
        lastResponseHash = null;
        lastResponseContent = null;
        lastResponseNormalized = null;
        duplicateCount = 0;
        logger.debug("[DEDUP] Response tracker reset");
    }

    @Override
    public String toString() {
        return String.format("ResponseTracker{hash=%s, duplicates=%d}", lastResponseHash, duplicateCount);
    }
}
