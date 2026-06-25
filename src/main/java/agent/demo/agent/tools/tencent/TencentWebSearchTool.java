package agent.demo.agent.tools.tencent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;

/**
 * 腾讯联网搜索工具
 * 封装腾讯云联网搜索API（SearchPro），为智能体提供实时网络搜索能力
 *
 * <p>使用标准云API 3.0的TC3-HMAC-SHA256签名方式鉴权
 *
 * <p>环境变量配置：
 * <ul>
 *   <li>TENCENT_API_SECRET_ID - 腾讯云API SecretId</li>
 *   <li>TENCENT_API_SECRET_KEY - 腾讯云API SecretKey</li>
 * </ul>
 *
 * @author Diego
 * @since 2024
 * @see <a href="https://cloud.tencent.com/document/product/1806/121811">腾讯云联网搜索API文档</a>
 */
@Component
public class TencentWebSearchTool implements Function<TencentWebSearchTool.SearchRequest, String> {

    private static final Logger log = LoggerFactory.getLogger(TencentWebSearchTool.class);
    private static final String API_ACTION = "SearchPro";
    private static final String API_VERSION = "2025-05-08";
    private static final String HOST = "wsa.tencentcloudapi.com";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private TencentCloudSigner signer;
    private final String endpoint;

    public TencentWebSearchTool(
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper,
            @Value("${tencent.search.endpoint:https://wsa.tencentcloudapi.com}") String endpoint,
            @Value("${tencent.search.timeout:5000}") int timeout,
            @Value("${tencent.search.secret-id:}") String secretId,
            @Value("${tencent.search.secret-key:}") String secretKey) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofMillis(timeout))
                .readTimeout(Duration.ofMillis(timeout))
                .build();
        this.objectMapper = objectMapper;
        this.endpoint = endpoint;

        // 优先从配置文件读取，其次从环境变量读取
        if (secretId != null && !secretId.isEmpty() && secretKey != null && !secretKey.isEmpty()) {
            this.signer = new TencentCloudSigner(secretId, secretKey);
            log.info("TencentWebSearchTool initialized with config file credentials");
        } else {
            try {
                this.signer = TencentCloudSigner.fromEnvironment();
                log.info("TencentWebSearchTool initialized with environment variables");
            } catch (IllegalArgumentException e) {
                log.warn("TencentWebSearchTool: {}", e.getMessage());
                this.signer = null;
            }
        }

        log.info("TencentWebSearchTool initialized with endpoint: {}", endpoint);
    }

    @Override
    public String apply(SearchRequest request) {
        // 参数验证
        if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            return "错误：搜索查询不能为空";
        }

        // 验证签名器
        if (signer == null) {
            return "错误：TENCENT_API_SECRET_ID 或 TENCENT_API_SECRET_KEY 未配置";
        }

        try {
            // 构建请求体
            String payload = buildPayload(request);
            log.info("===== 腾讯搜索API请求 =====");
            log.info("请求URL: {}", endpoint);
            log.info("请求体: {}", payload);

            // 生成签名
            long timestamp = TencentCloudSigner.currentTimestamp();
            String authorization = signer.sign(API_ACTION, payload, timestamp);
            log.info("时间戳: {}", timestamp);
            log.info("Authorization: {}", authorization);

            // 构建HTTP请求
            HttpHeaders headers = buildHeaders(timestamp, authorization);
            log.info("请求头: {}", headers);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            // 发送请求
            log.info("发送请求...");
            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint, HttpMethod.POST, entity, String.class);
            log.info("响应状态码: {}", response.getStatusCode());
            log.info("响应内容: {}", response.getBody());
            log.info("===== 请求完成 =====");

            // 解析响应
            return parseResponse(response.getBody(), request.getQuery());

        } catch (IllegalArgumentException e) {
            log.error("Invalid argument: {}", e.getMessage());
            return "错误：" + e.getMessage();
        } catch (RestClientException e) {
            log.error("API request failed: {}", e.getMessage());
            return "错误：API请求失败 - " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return "错误：发生未知错误 - " + e.getMessage();
        }
    }

    /**
     * 构建请求体JSON
     */
    private String buildPayload(SearchRequest request) throws JsonProcessingException {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("Query", request.getQuery());

        // 可选参数 - 只在有效时添加
        // Mode: 0-自然搜索，1-多模态VR，2-混合
        if (request.getMode() != null && (request.getMode() == 0 || request.getMode() == 1 || request.getMode() == 2)) {
            payload.put("Mode", request.getMode());
        }
        // Cnt: 只能是10/20/30/40/50
        if (request.getCnt() != null && (request.getCnt() == 10 || request.getCnt() == 20 ||
            request.getCnt() == 30 || request.getCnt() == 40 || request.getCnt() == 50)) {
            payload.put("Cnt", request.getCnt());
        }
        // FromTime/ToTime: Unix时间戳，大于0才添加
        if (request.getFromTime() != null && request.getFromTime() > 0) {
            payload.put("FromTime", request.getFromTime());
        }
        if (request.getToTime() != null && request.getToTime() > 0) {
            payload.put("ToTime", request.getToTime());
        }
        // Site: 站点过滤
        if (request.getSite() != null && !request.getSite().isEmpty()) {
            payload.put("Site", request.getSite());
        }
        // Industry: gov/news/acad/finance
        if (request.getIndustry() != null && !request.getIndustry().isEmpty()) {
            payload.put("Industry", request.getIndustry());
        }

        return objectMapper.writeValueAsString(payload);
    }

    /**
     * 构建HTTP请求头 - 使用标准云API 3.0方式
     */
    private HttpHeaders buildHeaders(long timestamp, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Host", HOST);
        headers.set("X-TC-Action", API_ACTION);
        headers.set("X-TC-Version", API_VERSION);
        headers.set("X-TC-Timestamp", String.valueOf(timestamp));
        headers.set("Authorization", authorization);
        return headers;
    }

    /**
     * 解析API响应
     */
    private String parseResponse(String responseBody, String query) throws JsonProcessingException {
        if (responseBody == null || responseBody.isEmpty()) {
            return "错误：API返回空响应";
        }

        // 解析外层响应
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        Map<String, Object> response = (Map<String, Object>) responseMap.get("Response");

        if (response == null) {
            return "错误：无效的API响应格式";
        }

        // 检查错误
        Object errorObj = response.get("Error");
        if (errorObj != null) {
            if (errorObj instanceof Map) {
                Map<String, Object> errorMap = (Map<String, Object>) errorObj;
                String code = (String) errorMap.get("Code");
                String message = (String) errorMap.get("Message");
                return String.format("API错误：%s - %s", code, message);
            } else {
                return "API错误：" + errorObj.toString();
            }
        }

        // 获取结果
        Object pagesObj = response.get("Pages");
        String requestId = (String) response.get("RequestId");

        if (pagesObj == null) {
            return String.format("未找到与\"%s\"相关的搜索结果\n请求ID: %s", query, requestId);
        }

        // 处理Pages - 可能是List<String>或List<Map>
        List<String> pages = new ArrayList<>();
        if (pagesObj instanceof List) {
            List<?> pagesList = (List<?>) pagesObj;
            for (Object page : pagesList) {
                if (page instanceof String) {
                    pages.add((String) page);
                } else if (page instanceof Map) {
                    // 将Map转换为JSON字符串
                    pages.add(objectMapper.writeValueAsString(page));
                }
            }
        }

        if (pages.isEmpty()) {
            return String.format("未找到与\"%s\"相关的搜索结果\n请求ID: %s", query, requestId);
        }

        // 解析并格式化结果
        return formatResults(pages, query, requestId);
    }

    /**
     * 格式化搜索结果
     */
    private String formatResults(List<String> pages, String query, String requestId) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("搜索\"%s\"的结果：\n\n", query));

        int count = 0;
        for (String pageJson : pages) {
            try {
                SearchResult result = objectMapper.readValue(pageJson, SearchResult.class);
                count++;
                sb.append(String.format("%d. %s\n", count, result.getTitle()));
                sb.append(String.format("   链接: %s\n", result.getUrl()));
                sb.append(String.format("   摘要: %s\n", result.getPassage()));
                sb.append(String.format("   来源: %s | 日期: %s\n", result.getSite(), result.getDate()));
                sb.append(String.format("   相关度: %.2f\n\n", result.getScore()));
            } catch (JsonProcessingException e) {
                log.warn("Failed to parse search result: {}", pageJson, e);
            }
        }

        sb.append(String.format("共 %d 条结果\n请求ID: %s", count, requestId));
        return sb.toString();
    }

    /**
     * 搜索请求参数
     */
    public static class SearchRequest {
        private String query;
        private Integer mode;
        private Integer cnt;
        private Long fromTime;
        private Long toTime;
        private String site;
        private String industry;

        public SearchRequest() {}

        public SearchRequest(String query) {
            this.query = query;
        }

        // Getters and Setters
        public String getQuery() { return query; }
        public void setQuery(String query) { this.query = query; }

        public Integer getMode() { return mode; }
        public void setMode(Integer mode) { this.mode = mode; }

        public Integer getCnt() { return cnt; }
        public void setCnt(Integer cnt) { this.cnt = cnt; }

        public Long getFromTime() { return fromTime; }
        public void setFromTime(Long fromTime) { this.fromTime = fromTime; }

        public Long getToTime() { return toTime; }
        public void setToTime(Long toTime) { this.toTime = toTime; }

        public String getSite() { return site; }
        public void setSite(String site) { this.site = site; }

        public String getIndustry() { return industry; }
        public void setIndustry(String industry) { this.industry = industry; }
    }
}
