package agent.demo.branch.tool;

import agent.demo.branch.model.ActivityItem;
import agent.demo.branch.model.BranchActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

/**
 * 网点活动数据工具
 * 提供各大银行网点的活动数据查询能力
 *
 * <p>支持的银行：浦发银行、工商银行、建设银行、中国银行、农业银行、招商银行等
 *
 * <p>使用示例：
 * <pre>{@code
 * SearchRequest request = new SearchRequest("浦发银行", "上海", "制造局路支行");
 * String result = branchActivityTool.apply(request);
 * }</pre>
 *
 * @author Diego
 * @since 2024
 */
@Component
public class BranchActivityTool implements Function<BranchActivityTool.SearchRequest, String> {

    private static final Logger log = LoggerFactory.getLogger(BranchActivityTool.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 模拟数据存储
    private static final Map<String, BranchActivity> MOCK_DATA = new HashMap<>();

    static {
        // 初始化模拟数据
        MOCK_DATA.put("浦发银行_上海_制造局路支行", createPudongBankData());
        MOCK_DATA.put("工商银行_北京_朝阳支行", createICBCData());
        MOCK_DATA.put("建设银行_深圳_福田支行", createCCBData());
        MOCK_DATA.put("中国银行_广州_天河支行", createBOCData());
        MOCK_DATA.put("农业银行_成都_武侯支行", createABCData());
        MOCK_DATA.put("招商银行_杭州_西湖支行", createCMBData());
    }

    @Override
    public String apply(SearchRequest request) {
        log.info("查询网点活动：银行={}, 城市={}, 网点={}",
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
            BranchActivity activity = MOCK_DATA.get(key);

            if (activity == null) {
                // 如果没有精确匹配，尝试模糊匹配
                activity = MOCK_DATA.values().stream()
                        .filter(a -> a.getBankName().equals(request.getBankName())
                                && a.getBranchName().contains(request.getBranchName()))
                        .findFirst()
                        .orElse(null);
            }

            if (activity == null) {
                // 生成随机模拟数据
                activity = generateMockData(request.getBankName(), request.getCity(), request.getBranchName());
            }

            return objectMapper.writeValueAsString(activity);

        } catch (Exception e) {
            log.error("查询网点活动失败", e);
            return "错误：查询网点活动失败 - " + e.getMessage();
        }
    }

    /**
     * 生成模拟数据
     */
    private BranchActivity generateMockData(String bankName, String city, String branchName) {
        int hash = (bankName + branchName).hashCode();
        int activityCount = 5 + Math.abs(hash % 10);

        List<ActivityItem> activities = new ArrayList<>();
        String[] activityNames = {"理财讲座", "客户答谢会", "社区金融知识普及", "新产品推介会", "VIP客户专享活动"};
        String[] activityTypes = {"讲座", "答谢会", "社区活动", "推介会", "专享活动"};

        for (int i = 0; i < activityCount; i++) {
            int nameIndex = Math.abs((hash + i) % activityNames.length);
            activities.add(new ActivityItem(
                    activityNames[nameIndex],
                    String.format("2024-06-%02d", 1 + Math.abs((hash + i) % 28)),
                    activityTypes[nameIndex],
                    20 + Math.abs((hash + i) % 100)
            ));
        }

        return new BranchActivity(bankName, branchName, city, activityCount, activities, "2024年6月");
    }

    /**
     * 创建浦发银行模拟数据
     */
    private static BranchActivity createPudongBankData() {
        List<ActivityItem> activities = Arrays.asList(
                new ActivityItem("理财讲座", "2024-06-05", "讲座", 45),
                new ActivityItem("客户答谢会", "2024-06-12", "答谢会", 120),
                new ActivityItem("社区金融知识普及", "2024-06-15", "社区活动", 80),
                new ActivityItem("新产品推介会", "2024-06-18", "推介会", 65),
                new ActivityItem("VIP客户专享活动", "2024-06-22", "专享活动", 30),
                new ActivityItem("信用卡推广活动", "2024-06-25", "推广活动", 150),
                new ActivityItem("老年人防诈骗讲座", "2024-06-28", "讲座", 90),
                new ActivityItem("企业客户座谈会", "2024-06-30", "座谈会", 25)
        );
        return new BranchActivity("浦发银行", "制造局路支行", "上海", 8, activities, "2024年6月");
    }

    /**
     * 创建工商银行模拟数据
     */
    private static BranchActivity createICBCData() {
        List<ActivityItem> activities = Arrays.asList(
                new ActivityItem("理财规划讲座", "2024-06-03", "讲座", 55),
                new ActivityItem("高端客户品酒会", "2024-06-10", "答谢会", 40),
                new ActivityItem("社区义诊活动", "2024-06-14", "社区活动", 200),
                new ActivityItem("手机银行推广", "2024-06-17", "推广活动", 180),
                new ActivityItem("亲子财商教育", "2024-06-21", "讲座", 60),
                new ActivityItem("企业年金宣讲会", "2024-06-24", "推介会", 35),
                new ActivityItem("信用卡优惠活动", "2024-06-27", "推广活动", 250),
                new ActivityItem("退休规划讲座", "2024-06-30", "讲座", 75),
                new ActivityItem("小微企业融资对接会", "2024-06-08", "座谈会", 20),
                new ActivityItem("金融知识竞赛", "2024-06-16", "社区活动", 120),
                new ActivityItem("外汇投资讲座", "2024-06-23", "讲座", 45),
                new ActivityItem("贵宾客户生日会", "2024-06-29", "答谢会", 30)
        );
        return new BranchActivity("工商银行", "朝阳支行", "北京", 12, activities, "2024年6月");
    }

    /**
     * 创建建设银行模拟数据
     */
    private static BranchActivity createCCBData() {
        List<ActivityItem> activities = Arrays.asList(
                new ActivityItem("住房贷款政策解读", "2024-06-04", "讲座", 80),
                new ActivityItem("社区健步走活动", "2024-06-09", "社区活动", 150),
                new ActivityItem("信用卡权益说明会", "2024-06-13", "推介会", 60),
                new ActivityItem("少儿绘画比赛", "2024-06-17", "社区活动", 90),
                new ActivityItem("财富管理讲座", "2024-06-20", "讲座", 50),
                new ActivityItem("数字人民币体验", "2024-06-24", "推广活动", 200),
                new ActivityItem("老年人智能手机教学", "2024-06-27", "社区活动", 70),
                new ActivityItem("VIP客户观影会", "2024-06-30", "答谢会", 40),
                new ActivityItem("创业融资指导", "2024-06-11", "座谈会", 25),
                new ActivityItem("保险产品说明会", "2024-06-19", "推介会", 55)
        );
        return new BranchActivity("建设银行", "福田支行", "深圳", 10, activities, "2024年6月");
    }

    /**
     * 创建中国银行模拟数据
     */
    private static BranchActivity createBOCData() {
        List<ActivityItem> activities = Arrays.asList(
                new ActivityItem("外汇业务讲座", "2024-06-06", "讲座", 45),
                new ActivityItem("留学金融说明会", "2024-06-12", "推介会", 60),
                new ActivityItem("社区端午节活动", "2024-06-18", "社区活动", 180),
                new ActivityItem("跨境支付体验", "2024-06-22", "推广活动", 90),
                new ActivityItem("高端客户高尔夫", "2024-06-28", "答谢会", 30)
        );
        return new BranchActivity("中国银行", "天河支行", "广州", 5, activities, "2024年6月");
    }

    /**
     * 创建农业银行模拟数据
     */
    private static BranchActivity createABCData() {
        List<ActivityItem> activities = Arrays.asList(
                new ActivityItem("惠农政策宣讲", "2024-06-05", "讲座", 100),
                new ActivityItem("社区健康讲座", "2024-06-11", "社区活动", 120),
                new ActivityItem("社保卡办理指导", "2024-06-16", "推广活动", 200),
                new ActivityItem("农户贷款对接会", "2024-06-21", "座谈会", 30),
                new ActivityItem("防电信诈骗宣传", "2024-06-26", "社区活动", 150)
        );
        return new BranchActivity("农业银行", "武侯支行", "成都", 5, activities, "2024年6月");
    }

    /**
     * 创建招商银行模拟数据
     */
    private static BranchActivity createCMBData() {
        List<ActivityItem> activities = Arrays.asList(
                new ActivityItem("财富管理沙龙", "2024-06-04", "讲座", 35),
                new ActivityItem("亲子烘焙活动", "2024-06-09", "社区活动", 50),
                new ActivityItem("信用卡权益日", "2024-06-14", "推广活动", 300),
                new ActivityItem("高端客户红酒品鉴", "2024-06-19", "答谢会", 25),
                new ActivityItem("数字金融服务体验", "2024-06-24", "推广活动", 150),
                new ActivityItem("退休规划讲座", "2024-06-29", "讲座", 40)
        );
        return new BranchActivity("招商银行", "西湖支行", "杭州", 6, activities, "2024年6月");
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
