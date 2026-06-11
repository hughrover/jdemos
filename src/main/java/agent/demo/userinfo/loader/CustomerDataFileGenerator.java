package agent.demo.userinfo.loader;

import agent.demo.userinfo.model.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 客户数据文件生成器
 * 将生成的客户数据保存为JSON文件
 */
public class CustomerDataFileGenerator {

    private static final String DATA_FILE_PATH = "data/customers.json";

    /**
     * 生成客户数据文件
     */
    public static void generateDataFile(int count) throws IOException {
        // 生成客户数据
        List<UserInfo> customers = UserInfoDataGenerator.generateCustomers(count);

        // 创建ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // 确保data目录存在
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // 写入JSON文件
        File outputFile = new File(DATA_FILE_PATH);
        objectMapper.writeValue(outputFile, customers);

        System.out.println("成功生成 " + count + " 个客户数据到文件: " + outputFile.getAbsolutePath());
    }

    /**
     * 主方法，用于测试文件生成
     */
    public static void main(String[] args) {
        try {
            generateDataFile(1000);
        } catch (IOException e) {
            System.err.println("生成数据文件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
