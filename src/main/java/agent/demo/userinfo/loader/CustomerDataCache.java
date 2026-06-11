package agent.demo.userinfo.loader;

import agent.demo.userinfo.model.UserInfo;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 客户数据缓存管理器
 * 提供内存缓存和快速查询功能
 */
public class CustomerDataCache {

    private static CustomerDataCache instance;

    private final CustomerDataLoader dataLoader;
    private List<UserInfo> customerList;
    private Map<Long, UserInfo> customerByIdMap;
    private Map<String, List<UserInfo>> customerByNameMap;

    private CustomerDataCache() {
        this.dataLoader = new CustomerDataLoader();
        this.customerByIdMap = new ConcurrentHashMap<>();
        this.customerByNameMap = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     */
    public static synchronized CustomerDataCache getInstance() {
        if (instance == null) {
            instance = new CustomerDataCache();
        }
        return instance;
    }

    /**
     * 初始化缓存
     */
    public void initialize() throws IOException {
        dataLoader.load();
        customerList = dataLoader.getAllCustomers();
        buildIndex();
    }

    /**
     * 构建索引
     */
    private void buildIndex() {
        customerByIdMap.clear();
        customerByNameMap.clear();

        for (UserInfo customer : customerList) {
            // ID索引
            customerByIdMap.put(customer.getId(), customer);

            // 姓名索引
            customerByNameMap.computeIfAbsent(customer.getName(), k -> new ArrayList<>()).add(customer);
        }
    }

    /**
     * 获取所有客户
     */
    public List<UserInfo> getAllCustomers() {
        return Collections.unmodifiableList(customerList);
    }

    /**
     * 根据ID获取客户
     */
    public UserInfo getCustomerById(Long id) {
        return customerByIdMap.get(id);
    }

    /**
     * 根据姓名精确查询
     */
    public List<UserInfo> getCustomersByName(String name) {
        return customerByNameMap.getOrDefault(name, Collections.emptyList());
    }

    /**
     * 模糊姓名查询（包含匹配）
     */
    public List<UserInfo> searchByNameFuzzy(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Collections.emptyList();
        }

        return customerList.stream()
                .filter(customer -> customer.getName().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 获取客户总数
     */
    public int getCustomerCount() {
        return customerList.size();
    }

    /**
     * 重新加载数据
     */
    public void reload() throws IOException {
        dataLoader.reload();
        customerList = dataLoader.getAllCustomers();
        buildIndex();
    }
}
