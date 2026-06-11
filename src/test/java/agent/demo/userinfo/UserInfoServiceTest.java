package agent.demo.userinfo;

import agent.demo.userinfo.model.UserInfo;
import agent.demo.userinfo.service.UserInfoService;
import agent.demo.userinfo.service.UserInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserInfoService单元测试
 */
class UserInfoServiceTest {

    private UserInfoService userInfoService;

    @BeforeEach
    void setUp() {
        userInfoService = new UserInfoServiceImpl();
    }

    @Test
    void testGetAllCustomers() {
        List<UserInfo> customers = userInfoService.getAllCustomers();
        assertNotNull(customers);
        assertEquals(1000, customers.size());
    }

    @Test
    void testGetCustomerById() {
        UserInfo customer = userInfoService.getCustomerById(1L);
        assertNotNull(customer);
        assertEquals(1L, customer.getId());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        UserInfo customer = userInfoService.getCustomerById(9999L);
        assertNull(customer);
    }

    @Test
    void testSmartSearch() {
        // 测试智能搜索
        List<UserInfo> results = userInfoService.smartSearch("陶国军");
        assertFalse(results.isEmpty());
        assertEquals("陶国军", results.get(0).getName());
    }

    @Test
    void testSmartSearchByPinyin() {
        // 测试拼音搜索
        List<UserInfo> results = userInfoService.smartSearch("taoguojun");
        assertFalse(results.isEmpty());
    }

    @Test
    void testSmartSearchByInitial() {
        // 测试首字母搜索
        List<UserInfo> results = userInfoService.smartSearch("tgj");
        assertFalse(results.isEmpty());
    }

    @Test
    void testGetCustomerCount() {
        int count = userInfoService.getCustomerCount();
        assertEquals(1000, count);
    }

    @Test
    void testSearchByNameFuzzy() {
        // 测试模糊姓名搜索
        List<UserInfo> results = userInfoService.searchByNameFuzzy("陶");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(u -> u.getName().contains("陶")));
    }

    @Test
    void testGetCustomersByName() {
        // 测试精确姓名搜索
        List<UserInfo> customers = userInfoService.getAllCustomers();
        if (!customers.isEmpty()) {
            String testName = customers.get(0).getName();
            List<UserInfo> results = userInfoService.getCustomersByName(testName);
            assertFalse(results.isEmpty());
            assertTrue(results.stream().allMatch(u -> u.getName().equals(testName)));
        }
    }
}
