package agent.demo.insurance.loader;

import agent.demo.insurance.model.InsurancePolicy;
import agent.demo.userinfo.model.UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 保单数据生成器
 * 基于customers.json中的用户生成mock保单数据
 */
public class InsurancePolicyDataGenerator {

    private static final String[] INSURANCE_TYPES = {
        "人寿保险", "健康保险", "意外伤害保险", "养老保险", "医疗保险",
        "车险", "财产保险", "旅行保险", "教育保险", "投资连结保险"
    };

    private static final String[] STATUSES = {"有效", "已过期", "待生效", "已理赔"};

    private static final Random RANDOM = new Random(42); // 固定种子，保证数据可重现

    /**
     * 生成保单数据
     */
    public static List<InsurancePolicy> generatePolicies(List<UserInfo> customers) {
        List<InsurancePolicy> policies = new ArrayList<>();
        int policyCount = 1;

        for (UserInfo customer : customers) {
            // 每个用户生成1-3个保单
            int policyNum = 1 + RANDOM.nextInt(3);

            for (int i = 0; i < policyNum; i++) {
                InsurancePolicy policy = new InsurancePolicy();

                // 保单号
                policy.setPolicyId(String.format("P%06d", policyCount++));

                // 投保人信息
                policy.setPolicyholderId(customer.getId());
                policy.setPolicyholderName(customer.getName());

                // 被保险人（可能是投保人本人或家属）
                if (RANDOM.nextInt(3) == 0) {
                    // 30%概率为家属
                    policy.setInsuredName(customer.getName() + "的家属");
                } else {
                    policy.setInsuredName(customer.getName());
                }

                // 险种
                policy.setInsuranceType(INSURANCE_TYPES[RANDOM.nextInt(INSURANCE_TYPES.length)]);

                // 保费（1000-50000元）
                policy.setPremium(1000.0 + RANDOM.nextInt(49000));

                // 保额（保费的10-50倍）
                policy.setSumInsured(policy.getPremium() * (10 + RANDOM.nextInt(41)));

                // 保险期间（2023-2025年）
                int startYear = 2023 + RANDOM.nextInt(3);
                int startMonth = 1 + RANDOM.nextInt(12);
                int startDay = 1 + RANDOM.nextInt(28);
                policy.setStartDate(String.format("%d-%02d-%02d", startYear, startMonth, startDay));

                int endYear = startYear + 1 + RANDOM.nextInt(2);
                policy.setEndDate(String.format("%d-%02d-%02d", endYear, startMonth, startDay));

                // 状态
                if (startYear < 2024) {
                    policy.setStatus(STATUSES[RANDOM.nextInt(2)]); // 有效或已过期
                } else {
                    policy.setStatus(STATUSES[0]); // 有效
                }

                policies.add(policy);
            }
        }

        return policies;
    }

    /**
     * 主方法，用于测试数据生成
     */
    public static void main(String[] args) throws IOException {
        // 加载客户数据
        ObjectMapper objectMapper = new ObjectMapper();
        List<UserInfo> customers = objectMapper.readValue(
                new File("data/customers.json"),
                new TypeReference<List<UserInfo>>() {}
        );

        // 生成保单数据
        List<InsurancePolicy> policies = generatePolicies(customers);

        System.out.println("生成了 " + policies.size() + " 个保单");
        System.out.println("前5个保单：");
        for (int i = 0; i < 5 && i < policies.size(); i++) {
            System.out.println(policies.get(i));
        }
    }
}
