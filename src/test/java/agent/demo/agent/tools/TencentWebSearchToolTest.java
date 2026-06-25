package agent.demo.agent.tools;

import agent.demo.agent.tools.tencent.SearchResponse;
import agent.demo.agent.tools.tencent.SearchResult;
import agent.demo.agent.tools.tencent.TencentCloudSigner;
import agent.demo.agent.tools.tencent.TencentWebSearchTool;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TencentWebSearchTool单元测试
 *
 * @author Diego
 * @since 2024
 */
@DisplayName("TencentWebSearchTool Tests")
class TencentWebSearchToolTest {

    @Nested
    @DisplayName("SearchRequest Tests")
    class SearchRequestTests {

        @Test
        @DisplayName("Should create request with query only")
        void shouldCreateRequestWithQueryOnly() {
            TencentWebSearchTool.SearchRequest request = new TencentWebSearchTool.SearchRequest("test query");
            assertEquals("test query", request.getQuery());
            assertNull(request.getMode());
            assertNull(request.getCnt());
            assertNull(request.getFromTime());
            assertNull(request.getToTime());
            assertNull(request.getSite());
            assertNull(request.getIndustry());
        }

        @Test
        @DisplayName("Should set all parameters correctly")
        void shouldSetAllParametersCorrectly() {
            TencentWebSearchTool.SearchRequest request = new TencentWebSearchTool.SearchRequest();
            request.setQuery("test query");
            request.setMode(2);
            request.setCnt(20);
            request.setFromTime(1745498501L);
            request.setToTime(1745584901L);
            request.setSite("zhihu.com");
            request.setIndustry("acad");

            assertEquals("test query", request.getQuery());
            assertEquals(2, request.getMode());
            assertEquals(20, request.getCnt());
            assertEquals(1745498501L, request.getFromTime());
            assertEquals(1745584901L, request.getToTime());
            assertEquals("zhihu.com", request.getSite());
            assertEquals("acad", request.getIndustry());
        }
    }

    @Nested
    @DisplayName("SearchResult Tests")
    class SearchResultTests {

        @Test
        @DisplayName("Should create SearchResult with all fields")
        void shouldCreateSearchResultWithAllFields() {
            SearchResult result = new SearchResult();
            result.setTitle("Test Title");
            result.setUrl("https://example.com");
            result.setPassage("Test passage");
            result.setDate("2024-06-07 19:00:51");
            result.setSite("Example");
            result.setScore(0.89);

            assertEquals("Test Title", result.getTitle());
            assertEquals("https://example.com", result.getUrl());
            assertEquals("Test passage", result.getPassage());
            assertEquals("2024-06-07 19:00:51", result.getDate());
            assertEquals("Example", result.getSite());
            assertEquals(0.89, result.getScore());
        }

        @Test
        @DisplayName("Should format toString correctly")
        void shouldFormatToStringCorrectly() {
            SearchResult result = new SearchResult();
            result.setTitle("Test Title");
            result.setUrl("https://example.com");
            result.setPassage("Test passage");
            result.setDate("2024-06-07");
            result.setSite("Example");
            result.setScore(0.89);

            String expected = "标题: Test Title\n链接: https://example.com\n摘要: Test passage\n日期: 2024-06-07\n来源: Example\n相关度: 0.89";
            assertEquals(expected, result.toString());
        }
    }

    @Nested
    @DisplayName("SearchResponse Tests")
    class SearchResponseTests {

        @Test
        @DisplayName("Should create SearchResponse with all fields")
        void shouldCreateSearchResponseWithAllFields() {
            SearchResponse response = new SearchResponse();
            response.setQuery("test query");
            response.setVersion("standard");
            response.setRequestId("test-request-id");

            assertEquals("test query", response.getQuery());
            assertEquals("standard", response.getVersion());
            assertEquals("test-request-id", response.getRequestId());
        }
    }

    @Nested
    @DisplayName("TencentCloudSigner Tests")
    class TencentCloudSignerTests {

        @Test
        @DisplayName("Should throw exception when environment variable not set")
        void shouldThrowExceptionWhenEnvironmentVariableNotSet() {
            // Note: This test assumes TENCENT_WSA_API_KEY is not set
            // In CI/CD, you may need to mock System.getenv()
            assertThrows(IllegalArgumentException.class, () -> {
                TencentCloudSigner.fromEnvironment();
            });
        }

        @Test
        @DisplayName("Should generate consistent signature for same input")
        void shouldGenerateConsistentSignatureForSameInput() {
            TencentCloudSigner signer = new TencentCloudSigner("testId", "testKey");
            String payload = "{\"Query\":\"test\"}";
            long timestamp = 1745498501L;

            String signature1 = signer.sign("SearchPro", payload, timestamp);
            String signature2 = signer.sign("SearchPro", payload, timestamp);

            assertEquals(signature1, signature2);
        }

        @Test
        @DisplayName("Should generate different signature for different payloads")
        void shouldGenerateDifferentSignatureForDifferentPayloads() {
            TencentCloudSigner signer = new TencentCloudSigner("testId", "testKey");

            // 使用不同的时间戳来确保签名不同
            String signature1 = signer.sign("SearchPro", "{\"Query\":\"test1\"}", 1745498501L);
            String signature2 = signer.sign("SearchPro", "{\"Query\":\"test2\"}", 1745498502L);

            assertNotEquals(signature1, signature2);
        }

        @Test
        @DisplayName("Should return current timestamp")
        void shouldReturnCurrentTimestamp() {
            long before = System.currentTimeMillis() / 1000;
            long timestamp = TencentCloudSigner.currentTimestamp();
            long after = System.currentTimeMillis() / 1000;

            assertTrue(timestamp >= before && timestamp <= after);
        }
    }
}
