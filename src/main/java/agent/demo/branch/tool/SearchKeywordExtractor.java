package agent.demo.branch.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 智能搜索关键词提取器
 * 从用户问题中提取银行名称、城市、网点名称，并生成搜索关键词
 *
 * <p>支持的业务场景：
 * <ul>
 *   <li>同业业绩 - 搜索周边银行的业绩情况</li>
 *   <li>潜客画像 - 搜索潜在客户的画像和需求</li>
 *   <li>渠道动态 - 搜索银行渠道的动态信息</li>
 *   <li>周边新闻 - 搜索网点周边的新闻</li>
 * </ul>
 *
 * @author Diego
 * @since 2024
 */
@Component
public class SearchKeywordExtractor {

    private static final Logger log = LoggerFactory.getLogger(SearchKeywordExtractor.class);

    // 银行名称列表
    private static final List<String> BANK_NAMES = Arrays.asList(
            "浦发银行", "工商银行", "建设银行", "中国银行", "农业银行", "招商银行",
            "交通银行", "邮储银行", "兴业银行", "中信银行", "光大银行", "民生银行",
            "平安银行", "华夏银行", "广发银行", "浙商银行", "渤海银行", "恒丰银行"
    );

    // 城市列表
    private static final List<String> CITIES = Arrays.asList(
            "上海", "北京", "深圳", "广州", "成都", "杭州", "武汉", "南京",
            "重庆", "西安", "苏州", "天津", "郑州", "长沙", "青岛", "大连",
            "宁波", "厦门", "昆明", "合肥", "福州", "济南", "哈尔滨", "沈阳"
    );

    // 网点类型后缀
    private static final List<String> BRANCH_SUFFIXES = Arrays.asList(
            "支行", "分行", "分理处", "营业部", "营业厅", "网点"
    );

    // 业务场景关键词映射
    private static final Map<String, List<String>> SCENARIO_KEYWORDS = new HashMap<>();

    static {
        SCENARIO_KEYWORDS.put("peer_performance", Arrays.asList("同业", "周边", "业绩", "对比", "排名"));
        SCENARIO_KEYWORDS.put("customer_profile", Arrays.asList("潜客", "画像", "需求", "客群", "客户"));
        SCENARIO_KEYWORDS.put("channel_dynamics", Arrays.asList("渠道", "动态", "银行", "政策", "变化"));
        SCENARIO_KEYWORDS.put("local_news", Arrays.asList("新闻", "周边", "地区", "社区", "事件"));
    }

    /**
     * 提取搜索关键词
     *
     * @param question 用户问题
     * @return 提取的关键词结果
     */
    public ExtractionResult extract(String question) {
        if (question == null || question.isEmpty()) {
            return new ExtractionResult();
        }

        log.info("提取搜索关键词：{}", question);

        ExtractionResult result = new ExtractionResult();

        // 提取银行名称
        String bankName = extractBankName(question);
        result.setBankName(bankName);

        // 提取城市
        String city = extractCity(question);
        result.setCity(city);

        // 提取网点名称
        String branchName = extractBranchName(question);
        result.setBranchName(branchName);

        // 识别业务场景
        String scenario = identifyScenario(question);
        result.setScenario(scenario);

        // 生成搜索关键词
        String keywords = generateKeywords(result);
        result.setKeywords(keywords);

        log.info("提取结果：银行={}, 城市={}, 网点={}, 场景={}, 关键词={}",
                bankName, city, branchName, scenario, keywords);

        return result;
    }

    /**
     * 提取银行名称
     */
    private String extractBankName(String question) {
        for (String bank : BANK_NAMES) {
            if (question.contains(bank)) {
                return bank;
            }
        }
        return null;
    }

    /**
     * 提取城市
     */
    private String extractCity(String question) {
        for (String city : CITIES) {
            if (question.contains(city)) {
                return city;
            }
        }
        return null;
    }

    /**
     * 提取网点名称
     */
    private String extractBranchName(String question) {
        // 使用正则表达式匹配网点名称
        // 匹配模式：XX支行、XX分行、XX分理处等
        for (String suffix : BRANCH_SUFFIXES) {
            Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+" + suffix);
            Matcher matcher = pattern.matcher(question);
            if (matcher.find()) {
                String branch = matcher.group();
                // 排除银行名称中的网点类型
                if (!branch.endsWith("银行" + suffix)) {
                    return branch;
                }
            }
        }
        return null;
    }

    /**
     * 识别业务场景
     */
    private String identifyScenario(String question) {
        Map<String, Integer> scenarioScores = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : SCENARIO_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (question.contains(keyword)) {
                    score++;
                }
            }
            if (score > 0) {
                scenarioScores.put(entry.getKey(), score);
            }
        }

        // 返回得分最高的场景
        return scenarioScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("general");
    }

    /**
     * 生成搜索关键词
     */
    private String generateKeywords(ExtractionResult result) {
        StringBuilder keywords = new StringBuilder();

        // 根据场景生成不同的关键词
        switch (result.getScenario()) {
            case "peer_performance":
                if (result.getBankName() != null) keywords.append(result.getBankName()).append(" ");
                if (result.getCity() != null) keywords.append(result.getCity()).append(" ");
                if (result.getBranchName() != null) keywords.append(result.getBranchName()).append(" ");
                keywords.append("周边 银行 业绩");
                break;

            case "customer_profile":
                if (result.getCity() != null) keywords.append(result.getCity()).append(" ");
                if (result.getBranchName() != null) keywords.append(result.getBranchName()).append(" ");
                keywords.append("居民 理财需求 客户画像");
                break;

            case "channel_dynamics":
                if (result.getCity() != null) keywords.append(result.getCity()).append(" ");
                keywords.append("银行 网点 渠道 动态");
                break;

            case "local_news":
                if (result.getCity() != null) keywords.append(result.getCity()).append(" ");
                if (result.getBranchName() != null) keywords.append(result.getBranchName()).append(" ");
                keywords.append("新闻");
                break;

            default:
                // 通用场景，使用原始问题
                if (result.getBankName() != null) keywords.append(result.getBankName()).append(" ");
                if (result.getCity() != null) keywords.append(result.getCity()).append(" ");
                if (result.getBranchName() != null) keywords.append(result.getBranchName()).append(" ");
                break;
        }

        return keywords.toString().trim();
    }

    /**
     * 提取结果
     */
    public static class ExtractionResult {
        private String bankName;
        private String city;
        private String branchName;
        private String scenario;
        private String keywords;

        public ExtractionResult() {}

        // Getters and Setters
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getBranchName() { return branchName; }
        public void setBranchName(String branchName) { this.branchName = branchName; }

        public String getScenario() { return scenario; }
        public void setScenario(String scenario) { this.scenario = scenario; }

        public String getKeywords() { return keywords; }
        public void setKeywords(String keywords) { this.keywords = keywords; }

        @Override
        public String toString() {
            return String.format("ExtractionResult{bankName='%s', city='%s', branchName='%s', scenario='%s', keywords='%s'}",
                    bankName, city, branchName, scenario, keywords);
        }
    }
}
