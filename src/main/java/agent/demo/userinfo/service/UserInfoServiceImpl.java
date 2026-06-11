package agent.demo.userinfo.service;

import agent.demo.userinfo.loader.CustomerDataCache;
import agent.demo.userinfo.matcher.PinyinMatcher;
import agent.demo.userinfo.model.UserInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息服务实现
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final CustomerDataCache dataCache;

    public UserInfoServiceImpl() {
        this.dataCache = CustomerDataCache.getInstance();
        try {
            this.dataCache.initialize();
        } catch (Exception e) {
            System.err.println("初始化客户数据缓存失败: " + e.getMessage());
        }
    }

    @Override
    public List<UserInfo> getAllCustomers() {
        return dataCache.getAllCustomers();
    }

    @Override
    public UserInfo getCustomerById(Long id) {
        return dataCache.getCustomerById(id);
    }

    @Override
    public List<UserInfo> getCustomersByName(String name) {
        return dataCache.getCustomersByName(name);
    }

    @Override
    public List<UserInfo> getCustomersByPinyin(String pinyin) {
        return dataCache.getCustomersByPinyin(pinyin);
    }

    @Override
    public List<UserInfo> getCustomersByPinyinInitial(String pinyinInitial) {
        return dataCache.getCustomersByPinyinInitial(pinyinInitial);
    }

    @Override
    public List<UserInfo> searchByPinyinFuzzy(String keyword) {
        return dataCache.searchByPinyinFuzzy(keyword);
    }

    @Override
    public List<UserInfo> searchByNameFuzzy(String keyword) {
        return dataCache.searchByNameFuzzy(keyword);
    }

    @Override
    public List<UserInfo> smartSearch(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }

        List<UserInfo> results = new ArrayList<>();

        // 1. 尝试精确姓名匹配
        List<UserInfo> exactMatches = getCustomersByName(query);
        if (!exactMatches.isEmpty()) {
            results.addAll(exactMatches);
        }

        // 2. 尝试拼音全拼匹配
        List<UserInfo> pinyinMatches = getCustomersByPinyin(query);
        if (!pinyinMatches.isEmpty()) {
            results.addAll(pinyinMatches);
        }

        // 3. 尝试拼音首字母匹配
        List<UserInfo> initialMatches = getCustomersByPinyinInitial(query);
        if (!initialMatches.isEmpty()) {
            results.addAll(initialMatches);
        }

        // 4. 尝试模糊拼音查询
        List<UserInfo> fuzzyPinyinMatches = searchByPinyinFuzzy(query);
        if (!fuzzyPinyinMatches.isEmpty()) {
            results.addAll(fuzzyPinyinMatches);
        }

        // 5. 尝试模糊姓名查询
        List<UserInfo> fuzzyNameMatches = searchByNameFuzzy(query);
        if (!fuzzyNameMatches.isEmpty()) {
            results.addAll(fuzzyNameMatches);
        }

        // 去重
        return results.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public int getCustomerCount() {
        return dataCache.getCustomerCount();
    }
}
