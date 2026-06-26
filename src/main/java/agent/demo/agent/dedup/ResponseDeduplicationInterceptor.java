package agent.demo.agent.dedup;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;

/**
 * Interceptor that detects and prevents duplicate responses.
 * Uses ResponseTracker to track response content and suppress redundancies.
 *
 * Deduplication strategy:
 * 1. Exact hash match → suppress (empty response)
 * 2. New content is subset of old → suppress (empty response)
 * 3. New content is a progressive extension of old → ALLOW through,
 *    AND update tracker to the longer version
 * 4. High LCS similarity → suppress (empty response)
 * 5. Otherwise → allow through and record
 */
public class ResponseDeduplicationInterceptor extends ModelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ResponseDeduplicationInterceptor.class);

    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        // Get the tracker for the current thread
        ResponseTracker tracker = ResponseTrackerHolder.getTracker();

        // Call the actual model
        ModelResponse response;
        try {
            response = handler.call(request);
        } catch (Exception e) {
            logger.error("[DEDUP] Model call failed", e);
            throw new RuntimeException("Model call failed", e);
        }

        // Check if the response is a duplicate
        Object message = response.getMessage();
        if (message instanceof AssistantMessage assistantMessage) {
            String content = assistantMessage.getText();

            // Skip empty / whitespace-only responses
            if (content == null || content.isBlank()) {
                return response;
            }

            if (tracker.isDuplicate(content)) {
                logger.warn("[DEDUP] Suppressing duplicate response. Hash: {}",
                        tracker.calculateHash(content));

                // Return an empty response to suppress the duplicate
                AssistantMessage emptyMessage = new AssistantMessage("");
                return ModelResponse.of(emptyMessage);
            }

            // Handle progressive extension: new response is a superset of the old one.
            // Allow it through since it contains more complete information,
            // but update the tracker so the old shorter version is replaced.
            if (tracker.isProgressiveExtension(content)) {
                logger.info("[DEDUP] Progressive extension detected: replacing old record with extended version. " +
                                "Old hash: {}, new hash: {}",
                        tracker.getLastResponseHash(), tracker.calculateHash(content));
                tracker.recordResponse(content);
                return response;
            }

            // Normal case: record this response as the latest
            tracker.recordResponse(content);
            logger.debug("[DEDUP] Recorded new response. Hash: {}",
                    tracker.calculateHash(content));
        }

        return response;
    }

    @Override
    public String getName() {
        return "ResponseDeduplicationInterceptor";
    }

    @Override
    public List<ToolCallback> getTools() {
        return List.of();
    }
}
