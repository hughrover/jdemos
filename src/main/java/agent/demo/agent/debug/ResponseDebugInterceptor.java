package agent.demo.agent.debug;

import com.alibaba.cloud.ai.graph.agent.interceptor.ModelInterceptor;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ModelCallHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallback;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Debug interceptor to track response duplication issues.
 * Logs response content hash and count to identify where duplicates occur.
 */
public class ResponseDebugInterceptor extends ModelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ResponseDebugInterceptor.class);

    private final AtomicInteger responseCount = new AtomicInteger(0);

    @Override
    public ModelResponse interceptModel(ModelRequest request, ModelCallHandler handler) {
        // Log before model call
        int callNumber = responseCount.incrementAndGet();
        logger.debug("[DEBUG] Model call #{} starting", callNumber);

        // Call the actual model
        ModelResponse response;
        try {
            response = handler.call(request);
        } catch (Exception e) {
            logger.error("[DEBUG] Model call #{} failed", callNumber, e);
            throw new RuntimeException("Model call failed", e);
        }

        // Log after model call
        Object message = response.getMessage();
        if (message instanceof AssistantMessage assistantMessage) {
            String content = assistantMessage.getText();
            String hash = calculateHash(content);
            logger.debug("[DEBUG] Model call #{} completed. Response hash: {}, content length: {}",
                    callNumber, hash, content != null ? content.length() : 0);
            logger.debug("[DEBUG] Response content preview: {}",
                    content != null ? content.substring(0, Math.min(100, content.length())) + "..." : "null");
        }

        return response;
    }

    @Override
    public String getName() {
        return "ResponseDebugInterceptor";
    }

    @Override
    public List<ToolCallback> getTools() {
        return List.of();
    }

    private String calculateHash(String content) {
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
            return hexString.toString().substring(0, 16); // Return first 16 chars for brevity
        } catch (NoSuchAlgorithmException e) {
            return "error";
        }
    }
}
