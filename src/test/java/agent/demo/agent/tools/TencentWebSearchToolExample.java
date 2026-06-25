package agent.demo.agent.tools;

/**
 * TencentWebSearchTool使用示例
 *
 * <p>使用前请确保设置环境变量：
 * <pre>
 * export TENCENT_WSA_API_KEY="YourSecretId#YourSecretKey"
 * </pre>
 *
 * @author Diego
 * @since 2024
 */
public class TencentWebSearchToolExample {

    public static void main(String[] args) {
        // 注意：此示例需要设置环境变量才能运行
        // 以下代码仅展示使用方式

        System.out.println("=== TencentWebSearchTool 使用示例 ===\n");

        // 示例1：基础搜索
        System.out.println("示例1：基础搜索");
        System.out.println("SearchRequest request = new SearchRequest(\"今天北京的天气\");");
        System.out.println("String result = tool.apply(request);");
        System.out.println();

        // 示例2：带参数的搜索
        System.out.println("示例2：带参数的搜索");
        System.out.println("SearchRequest request = new SearchRequest();");
        System.out.println("request.setQuery(\"三星堆的由来\");");
        System.out.println("request.setMode(0);  // 自然搜索模式");
        System.out.println("request.setCnt(10);  // 返回10条结果");
        System.out.println("request.setSite(\"zhihu.com\");  // 仅搜索知乎");
        System.out.println("String result = tool.apply(request);");
        System.out.println();

        // 示例3：时间范围搜索
        System.out.println("示例3：时间范围搜索");
        System.out.println("SearchRequest request = new SearchRequest();");
        System.out.println("request.setQuery(\"人工智能最新进展\");");
        System.out.println("request.setFromTime(1745498501L);  // 开始时间");
        System.out.println("request.setToTime(1745584901L);    // 结束时间");
        System.out.println("String result = tool.apply(request);");
        System.out.println();

        // 示例4：行业过滤搜索
        System.out.println("示例4：行业过滤搜索");
        System.out.println("SearchRequest request = new SearchRequest();");
        System.out.println("request.setQuery(\"量子计算研究\");");
        System.out.println("request.setIndustry(\"acad\");  // 学术搜索");
        System.out.println("String result = tool.apply(request);");
        System.out.println();

        // 示例5：在智能体中使用
        System.out.println("示例5：在Spring AI智能体中使用");
        System.out.println("@Component");
        System.out.println("public class MyAgent {");
        System.out.println("    private final TencentWebSearchTool searchTool;");
        System.out.println();
        System.out.println("    public MyAgent(TencentWebSearchTool searchTool) {");
        System.out.println("        this.searchTool = searchTool;");
        System.out.println("    }");
        System.out.println();
        System.out.println("    public String answerQuestion(String question) {");
        System.out.println("        // 使用搜索工具获取实时信息");
        System.out.println("        String searchResult = searchTool.apply(new SearchRequest(question));");
        System.out.println("        // 基于搜索结果生成回答...");
        System.out.println("        return searchResult;");
        System.out.println("    }");
        System.out.println("}");
    }
}
