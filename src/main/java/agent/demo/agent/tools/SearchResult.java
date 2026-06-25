package agent.demo.agent.tools;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * 搜索结果类
 * 对应SearchPro API响应中Pages数组的每个元素
 *
 * @author Diego
 * @since 2024
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {

    @JsonProperty("title")
    private String title;

    @JsonProperty("date")
    private String date;

    @JsonProperty("url")
    private String url;

    @JsonProperty("passage")
    private String passage;

    @JsonProperty("content")
    private String content;

    @JsonProperty("site")
    private String site;

    @JsonProperty("score")
    private Double score;

    @JsonProperty("images")
    private List<String> images;

    @JsonProperty("favicon")
    private String favicon;

    public SearchResult() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getPassage() { return passage; }
    public void setPassage(String passage) { this.passage = passage; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getFavicon() { return favicon; }
    public void setFavicon(String favicon) { this.favicon = favicon; }

    @Override
    public String toString() {
        return String.format("标题: %s\n链接: %s\n摘要: %s\n日期: %s\n来源: %s\n相关度: %.2f",
                title, url, passage, date, site, score);
    }
}
