package agent.demo.userinfo.loader;

import agent.demo.userinfo.model.UserInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户数据加载器
 * 负责从文件中读取客户信息并加载到内存
 */
public class CustomerDataLoader {

    private static final String DATA_FILE_PATH = "data/customers.json";

    private List<UserInfo> customers;

    /**
     * 加载客户数据
     */
    public void load() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 尝试从文件系统加载
        File file = new File(DATA_FILE_PATH);
        if (file.exists()) {
            customers = objectMapper.readValue(file, new TypeReference<List<UserInfo>>() {});
            return;
        }

        // 尝试从classpath加载
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DATA_FILE_PATH);
        if (inputStream != null) {
            customers = objectMapper.readValue(inputStream, new TypeReference<List<UserInfo>>() {});
            inputStream.close();
            return;
        }

        // 如果文件不存在，生成数据
        System.out.println("数据文件不存在，正在生成...");
        CustomerDataFileGenerator.generateDataFile(1000);
        customers = objectMapper.readValue(new File(DATA_FILE_PATH), new TypeReference<List<UserInfo>>() {});
    }

    /**
     * 获取所有客户信息
     */
    public List<UserInfo> getAllCustomers() {
        if (customers == null) {
            try {
                load();
            } catch (IOException e) {
                System.err.println("加载客户数据失败: " + e.getMessage());
                return new ArrayList<>();
            }
        }
        return customers;
    }

    /**
     * 根据ID获取客户信息
     */
    public UserInfo getCustomerById(Long id) {
        return getAllCustomers().stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据姓名获取客户信息（精确匹配）
     */
    public List<UserInfo> getCustomersByName(String name) {
        return getAllCustomers().stream()
                .filter(customer -> customer.getName().equals(name))
                .toList();
    }

    /**
     * 获取客户总数
     */
    public int getCustomerCount() {
        return getAllCustomers().size();
    }

    /**
     * 重新加载数据（支持热更新）
     */
    public void reload() throws IOException {
        customers = null;
        load();
    }
}
