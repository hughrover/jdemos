package agent.demo.insurance.loader;

import agent.demo.insurance.model.InsurancePolicy;
import agent.demo.userinfo.model.UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 保单数据文件生成器
 * 将生成的保单数据保存为JSON文件
 */
public class InsurancePolicyFileGenerator {

    private static final String DATA_FILE_PATH = "data/insurance_policies.json";

    /**
     * 生成保单数据文件
     */
    public static void generateDataFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 加载客户数据
        List<UserInfo> customers = objectMapper.readValue(
                new File("data/customers.json"),
                new TypeReference<List<UserInfo>>() {}
        );

        // 生成保单数据
        List<InsurancePolicy> policies = InsurancePolicyDataGenerator.generatePolicies(customers);

        // 写入JSON文件
        File outputFile = new File(DATA_FILE_PATH);
        objectMapper.writeValue(outputFile, policies);

        System.out.println("成功生成 " + policies.size() + " 个保单数据到文件: " + outputFile.getAbsolutePath());
    }

    /**
     * 主方法，用于测试文件生成
     */
    public static void main(String[] args) {
        try {
            generateDataFile();
        } catch (IOException e) {
            System.err.println("生成保单数据文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
