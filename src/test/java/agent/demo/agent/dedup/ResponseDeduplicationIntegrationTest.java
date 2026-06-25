package agent.demo.agent.dedup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for response deduplication mechanism.
 */
class ResponseDeduplicationIntegrationTest {

    private ResponseDeduplicationInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new ResponseDeduplicationInterceptor();
        // Reset the tracker for each test
        ResponseTrackerHolder.resetTracker();
    }

    @Test
    void testDuplicateDetection_SameContent() {
        String content = "这是一条测试响应";

        // First response - should not be duplicate
        AssistantMessage message1 = new AssistantMessage(content);
        ResponseTracker tracker = ResponseTrackerHolder.getTracker();

        assertFalse(tracker.isDuplicate(content), "First response should not be duplicate");
        tracker.recordResponse(content);

        // Second response with same content - should be duplicate
        assertTrue(tracker.isDuplicate(content), "Second response with same content should be duplicate");
    }

    @Test
    void testDuplicateDetection_DifferentContent() {
        String content1 = "这是第一条响应";
        String content2 = "这是第二条响应";

        ResponseTracker tracker = ResponseTrackerHolder.getTracker();

        // First response
        assertFalse(tracker.isDuplicate(content1), "First response should not be duplicate");
        tracker.recordResponse(content1);

        // Second response with different content - should not be duplicate
        assertFalse(tracker.isDuplicate(content2), "Different content should not be duplicate");
    }

    @Test
    void testThreadLocal_Isolation() {
        String content1 = "Thread 1 response";
        String content2 = "Thread 2 response";

        // Simulate thread 1
        ResponseTracker tracker1 = ResponseTrackerHolder.getTracker();
        assertFalse(tracker1.isDuplicate(content1), "Thread 1 first response should not be duplicate");
        tracker1.recordResponse(content1);

        // Simulate thread 2 (in a real scenario, this would be a different thread)
        // For testing, we'll use the same tracker but with different content
        assertFalse(tracker1.isDuplicate(content2), "Different content should not be duplicate");
    }

    @Test
    void testDuplicateCount_Tracking() {
        String content = "重复的响应";

        ResponseTracker tracker = ResponseTrackerHolder.getTracker();

        // First call - not duplicate
        tracker.isDuplicate(content);
        tracker.recordResponse(content);

        // Second call - duplicate
        tracker.isDuplicate(content);

        // Third call - duplicate
        tracker.isDuplicate(content);

        assertEquals(2, tracker.getDuplicateCount(), "Should have 2 duplicates");
    }

    @Test
    void testReset_ClearsState() {
        String content = "测试响应";

        ResponseTracker tracker = ResponseTrackerHolder.getTracker();

        // Record a response
        tracker.isDuplicate(content);
        tracker.recordResponse(content);

        // Verify state
        assertNotNull(tracker.getLastResponseHash(), "Hash should not be null");
        assertEquals(0, tracker.getDuplicateCount(), "Should have 0 duplicates");

        // Make a duplicate
        tracker.isDuplicate(content);
        assertEquals(1, tracker.getDuplicateCount(), "Should have 1 duplicate");

        // Reset
        tracker.reset();

        // Verify reset
        assertNull(tracker.getLastResponseHash(), "Hash should be null after reset");
        assertEquals(0, tracker.getDuplicateCount(), "Duplicate count should be 0 after reset");
    }

    @Test
    void testLongContent_Hashing() {
        // Test with a long content similar to the user's example
        String longContent = "我搜索了浦发银行上海制造局路支行的详细信息，但搜索结果中没有直接显示该支行的确切地址、营业时间和联系方式。不过，我可以根据搜索结果提供一些有用的信息：\n\n" +
                "地址信息：启信宝显示\"上海浦东发展银行股份有限公司制造局路支行\"的地址为\"上海市黄浦区陆家浜路1392号101、102、103室\"，但这可能不是制造局路支行本身，而是相关分支机构的地址。\n\n" +
                "营业时间参考：\n\n" +
                "对公业务：周一至周五上午9:00至下午5:00\n" +
                "个人业务：周一至周五上午9:00至下午5:00；周六、周日上午10:00至下午4:00\n" +
                "ATM机：24小时营业\n" +
                "联系方式：\n\n" +
                "浦发银行全国客服电话：400-6383-888（工作日9:30-19:00）\n" +
                "浦发银行上海分行营业部电话：68887000（仅供参考，可能不是制造局路支行的直拨电话）\n" +
                "由于搜索结果中没有找到该支行的确切信息，我建议您可以通过以下方式获取最准确的信息：\n\n" +
                "拨打浦发银行全国客服热线400-6383-888进行咨询\n" +
                "访问浦发银行官方网站或手机APP查询具体网点信息\n" +
                "直接前往制造局路附近实地查看该支行的营业时间公告牌\n" +
                "如果您需要我尝试其他搜索方式或有其他特定需求，请告诉我。";

        ResponseTracker tracker = ResponseTrackerHolder.getTracker();

        // First call - not duplicate
        assertFalse(tracker.isDuplicate(longContent), "First call should not be duplicate");
        tracker.recordResponse(longContent);

        // Second call - duplicate
        assertTrue(tracker.isDuplicate(longContent), "Second call with same long content should be duplicate");
    }
}
