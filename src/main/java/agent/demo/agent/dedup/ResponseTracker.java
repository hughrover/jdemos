package agent.demo.agent.dedup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Tracks responses to detect and prevent duplicate output.
 * Uses SHA-256 hashing for efficient comparison.
 */
public class ResponseTracker {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTracker.class);

    private String lastResponseHash;
    private String lastResponseContent;
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
     * Check if a response is a duplicate of the last response.
     *
     * @param content the response content to check
     * @return true if this is a duplicate response
     */
    public boolean isDuplicate(String content) {
        if (content == null) {
            return lastResponseHash == null;
        }

        String hash = calculateHash(content);
        boolean isDuplicate = hash.equals(lastResponseHash);

        if (isDuplicate) {
            duplicateCount++;
            logger.warn("[DEDUP] Duplicate response detected (count: {}). Hash: {}, content length: {}",
                    duplicateCount, hash, content.length());
            logger.debug("[DEDUP] Duplicate content preview: {}",
                    content.substring(0, Math.min(200, content.length())));
        }

        return isDuplicate;
    }

    /**
     * Record a response as the latest response.
     *
     * @param content the response content to record
     */
    public void recordResponse(String content) {
        if (content == null) {
            lastResponseHash = "null";
            lastResponseContent = null;
        } else {
            lastResponseHash = calculateHash(content);
            lastResponseContent = content;
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
        duplicateCount = 0;
        logger.debug("[DEDUP] Response tracker reset");
    }

    @Override
    public String toString() {
        return String.format("ResponseTracker{hash=%s, duplicates=%d}", lastResponseHash, duplicateCount);
    }
}
