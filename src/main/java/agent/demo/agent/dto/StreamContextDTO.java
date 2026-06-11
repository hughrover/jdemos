package agent.demo.agent.dto;

import lombok.Data;
import java.util.LinkedList;

/**
 * 提示词执行返回参数
 */
@Data
public class StreamContextDTO {

    // 是否在图表模式内
    private boolean inChart = false;

    // 图表内容拼接缓冲区
    private StringBuilder chartContentSb = new StringBuilder();

    // 滑动窗口
    private LinkedList<String> slidingWindow = new LinkedList<>();

    // 答案缓冲区
    private StringBuilder answerSb = new StringBuilder();

    // 实时答案
    private String realtimeAnswer;

    // 原始内容累积（用于检测标签）
    private String rawContent;

    // 缓冲可能成为标签前缀的字符
    private String buffer;

    // 是否已经处理过标签
    private boolean hasProcessed = false;

    // 上一次输出的内容
    private String lastOutput;

    private StringBuilder chartBuffer;
    private boolean inTag;

}