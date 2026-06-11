package agent.demo.userinfo.service;

import agent.demo.userinfo.model.UserInfo;

import java.util.List;

/**
 * 用户信息服务接口
 */
public interface UserInfoService {

    /**
     * 获取所有客户信息
     */
    List<UserInfo> getAllCustomers();

    /**
     * 根据ID获取客户信息
     */
    UserInfo getCustomerById(Long id);

    /**
     * 根据姓名精确查询
     */
    List<UserInfo> getCustomersByName(String name);

    /**
     * 根据拼音查询
     */
    List<UserInfo> getCustomersByPinyin(String pinyin);

    /**
     * 根据拼音首字母查询
     */
    List<UserInfo> getCustomersByPinyinInitial(String pinyinInitial);

    /**
     * 模糊拼音查询
     */
    List<UserInfo> searchByPinyinFuzzy(String keyword);

    /**
     * 模糊姓名查询
     */
    List<UserInfo> searchByNameFuzzy(String keyword);

    /**
     * 智能查询（支持中文、拼音、首字母）
     */
    List<UserInfo> smartSearch(String query);

    /**
     * 获取客户总数
     */
    int getCustomerCount();
}
