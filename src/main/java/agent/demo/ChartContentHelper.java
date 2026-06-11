package agent.demo;


import org.springframework.stereotype.Component;

@Component
public class ChartContentHelper {


    //标签起始位
    public static final String CHAT_START_TAG = "<chart>";
    //标签结束位
    public static final String CHAT_END_TAG = "</chart>";

    /**
     * 固定标签 <chat>@cppc_@</chat>
     * 未遇到完整标签前：模型吐什么就输出什么（普通文本）
     * 遇到 < 开始后：缓冲不输出，直到确认不是标签
     * 确认是 <chat> 后：进入图表模式，标签内所有内容不输出
     * 遇到完整的 </chat> 后：一次性输出 @cppc_@，设置 displayType 和 componentsType
     * 之后恢复正常流式输出（模型吐什么就输出什么）
     *
     * @cppc_@ 只输出一次
     */
    public void processChartContent(StreamContextDTO context, String contentString,
                                    SubmitQuestionResponseVO resVo,
                                    String displayType, String componentsType, String output) {

        if (contentString == null || contentString.isEmpty()) {
            resVo.setAnswer("");
            return;
        }

        if (context.isHasProcessed()) {
            resVo.setAnswer(contentString);
            return;
        }

        StringBuilder buf = context.getChartBuffer();
        if (buf == null) {
            buf = new StringBuilder();
            context.setChartBuffer(buf);
        }

        buf.append(contentString);
        String current = buf.toString();

        // 1. 检查是否包含完整的结束标签
        if (current.contains(CHAT_END_TAG)) {
            int endIdx = current.indexOf(CHAT_END_TAG);
            int startIdx = current.lastIndexOf(CHAT_START_TAG, endIdx);

            if (startIdx >= 0) {
                String after = current.substring(endIdx + CHAT_END_TAG.length());

                String result = output;
                if (!after.isEmpty()) {
                    result = result + after;
                }
                resVo.setAnswer(result);
                resVo.setDisplayType(displayType);
                resVo.setComponentsType(componentsType);

                context.setHasProcessed(true);
                context.setChartBuffer(null);
                return;
            }
        }

        // 2. 检查是否包含完整的开始标签
        if (current.contains(CHAT_START_TAG)) {
            int startIdx = current.indexOf(CHAT_START_TAG);
            String before = current.substring(0, startIdx);

            if (!before.isEmpty()) {
                resVo.setAnswer(before);
            } else {
                resVo.setAnswer("");  // 清空，因为 <chart> 本身不应该输出
            }
            context.setInTag(true);
            context.setChartBuffer(new StringBuilder(current.substring(startIdx)));
            return;
        }

        // 3. 检查是否是标签开始的前缀
        boolean isTagStart = false;
        String tagStartPrefix = null;
        String beforeOutput = null;

        if (current.endsWith("。\n\n<")) {
            beforeOutput = current.substring(0, current.length() - 4);
            tagStartPrefix = "\n\n<";
            isTagStart = true;
        } else if (current.endsWith("\n\n<")) {
            tagStartPrefix = "\n\n<";
            isTagStart = true;
        } else if (current.endsWith("\n\n<c")) {
            tagStartPrefix = "\n\n<c";
            isTagStart = true;
        } else if (current.endsWith("\n\n<ch")) {
            tagStartPrefix = "\n\n<ch";
            isTagStart = true;
        } else if (current.endsWith("\n\n<cha")) {
            tagStartPrefix = "\n\n<cha";
            isTagStart = true;
        } else if (current.endsWith("\n\n<char")) {
            tagStartPrefix = "\n\n<char";
            isTagStart = true;
        } else if (current.endsWith("\n\n<chart")) {
            tagStartPrefix = "\n\n<chart";
            isTagStart = true;
        } else if (current.endsWith("<")) {
            tagStartPrefix = "<";
            isTagStart = true;
        } else if (current.endsWith("<c")) {
            tagStartPrefix = "<c";
            isTagStart = true;
        } else if (current.endsWith("<ch")) {
            tagStartPrefix = "<ch";
            isTagStart = true;
        } else if (current.endsWith("<cha")) {
            tagStartPrefix = "<cha";
            isTagStart = true;
        } else if (current.endsWith("<char")) {
            tagStartPrefix = "<char";
            isTagStart = true;
        } else if (current.endsWith("<chart")) {
            tagStartPrefix = "<chart";
            isTagStart = true;
        }

        if (isTagStart) {
            if (beforeOutput != null && !beforeOutput.isEmpty()) {
                resVo.setAnswer(beforeOutput);
            } else if (tagStartPrefix != null && current.length() > tagStartPrefix.length()) {
                String before = current.substring(0, current.length() - tagStartPrefix.length());
                if (!before.isEmpty()) {
                    resVo.setAnswer(before);
                } else {
                    resVo.setAnswer("");
                }
            } else {
                resVo.setAnswer("");  // 清空，因为当前内容全是标签前缀
            }
            context.setChartBuffer(new StringBuilder(tagStartPrefix));
            return;
        }

        // 4. 如果已经在标签模式内，清空 answer
        if (context.isInTag()) {
            resVo.setAnswer("");
            return;
        }

        // 5. 不是标签，输出全部
        resVo.setAnswer(current);
        context.setChartBuffer(null);
    }
}