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
 * Uses ResponseTracker to track response hashes and suppress duplicates.
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

            if (tracker.isDuplicate(content)) {
                logger.warn("[DEDUP] Suppressing duplicate response. Hash: {}",
                        tracker.calculateHash(content));

                // Return an empty response to suppress the duplicate
                AssistantMessage emptyMessage = new AssistantMessage("");
                return ModelResponse.of(emptyMessage);
            } else {
                // Record this response as the latest
                tracker.recordResponse(content);
                logger.debug("[DEDUP] Recorded new response. Hash: {}",
                        tracker.calculateHash(content));
            }
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
