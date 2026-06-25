package agent.demo.branch.tool;

import agent.demo.branch.model.BranchPerformance;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 网点业绩数据工具
 * 提供各大银行网点的业绩数据查询能力
 *
 * <p>支持的银行：浦发银行、工商银行、建设银行、中国银行、农业银行、招商银行等
 *
 * <p>使用示例：
 * <pre>{@code
 * SearchRequest request = new SearchRequest("浦发银行", "上海", "制造局路支行");
 * String result = branchPerformanceTool.apply(request);
 * }</pre>
 *
 * @author Diego
 * @since 2024
 */
@Component
public class BranchPerformanceTool implements Function<BranchPerformanceTool.SearchRequest, String> {

    private static final Logger log = LoggerFactory.getLogger(BranchPerformanceTool.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 模拟数据存储
    private static final Map<String, BranchPerformance> MOCK_DATA = new HashMap<>();

    static {
        // 初始化模拟数据
        MOCK_DATA.put("浦发银行_上海_制造局路支行",
                new BranchPerformance("浦发银行", "制造局路支行", "上海", 156, 2850.0, "2024年6月"));
        MOCK_DATA.put("工商银行_北京_朝阳支行",
                new BranchPerformance("工商银行", "朝阳支行", "北京", 203, 3420.0, "2024年6月"));
        MOCK_DATA.put("建设银行_深圳_福田支行",
                new BranchPerformance("建设银行", "福田支行", "深圳", 178, 2980.0, "2024年6月"));
        MOCK_DATA.put("中国银行_广州_天河支行",
                new BranchPerformance("中国银行", "天河支行", "广州", 165, 2750.0, "2024年6月"));
        MOCK_DATA.put("农业银行_成都_武侯支行",
                new BranchPerformance("农业银行", "武侯支行", "成都", 142, 2350.0, "2024年6月"));
        MOCK_DATA.put("招商银行_杭州_西湖支行",
                new BranchPerformance("招商银行", "西湖支行", "杭州", 189, 3150.0, "2024年6月"));
    }

    @Override
    public String apply(SearchRequest request) {
        log.info("查询网点业绩：银行={}, 城市={}, 网点={}",
                request.getBankName(), request.getCity(), request.getBranchName());

        try {
            // 参数验证
            if (request.getBankName() == null || request.getBankName().isEmpty()) {
                return "错误：银行名称不能为空";
            }
            if (request.getBranchName() == null || request.getBranchName().isEmpty()) {
                return "错误：网点名称不能为空";
            }

            // 构建查询键
            String key = String.format("%s_%s_%s",
                    request.getBankName(),
                    request.getCity() != null ? request.getCity() : "未知",
                    request.getBranchName());

            // 查询模拟数据
            BranchPerformance performance = MOCK_DATA.get(key);

            if (performance == null) {
                // 如果没有精确匹配，尝试模糊匹配
                performance = MOCK_DATA.values().stream()
                        .filter(p -> p.getBankName().equals(request.getBankName())
                                && p.getBranchName().contains(request.getBranchName()))
                        .findFirst()
                        .orElse(null);
            }

            if (performance == null) {
                // 生成随机模拟数据
                performance = generateMockData(request.getBankName(), request.getCity(), request.getBranchName());
            }

            return objectMapper.writeValueAsString(performance);

        } catch (Exception e) {
            log.error("查询网点业绩失败", e);
            return "错误：查询网点业绩失败 - " + e.getMessage();
        }
    }

    /**
     * 生成模拟数据
     */
    private BranchPerformance generateMockData(String bankName, String city, String branchName) {
        // 使用名称的hashCode生成一致的随机数据
        int hash = (bankName + branchName).hashCode();
        int policyCount = 100 + Math.abs(hash % 150);
        double premiumAmount = 1500.0 + Math.abs(hash % 2000);

        return new BranchPerformance(bankName, branchName, city, policyCount, premiumAmount, "2024年6月");
    }

    /**
     * 搜索请求
     */
    public static class SearchRequest {
        private String bankName;
        private String city;
        private String branchName;

        public SearchRequest() {}

        public SearchRequest(String bankName, String city, String branchName) {
            this.bankName = bankName;
            this.city = city;
            this.branchName = branchName;
        }

        // Getters and Setters
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getBranchName() { return branchName; }
        public void setBranchName(String branchName) { this.branchName = branchName; }
    }
}
