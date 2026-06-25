package agent.demo.agent.tools.tencent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 腾讯搜索API响应类
 * 对应SearchPro API的Response结构
 *
 * @author Diego
 * @since 2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResponse {

    @JsonProperty("Query")
    private String query;

    @JsonProperty("Pages")
    private List<String> pages;

    @JsonProperty("Version")
    private String version;

    @JsonProperty("RequestId")
    private String requestId;

    @JsonProperty("Msg")
    private String msg;

    public SearchResponse() {}

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<String> getPages() { return pages; }
    public void setPages(List<String> pages) { this.pages = pages; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
}
