package agent.demo.userinfo.controller;

import agent.demo.userinfo.model.UserInfo;
import agent.demo.userinfo.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户查询控制器
 * 暴露REST API接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserInfoService userInfoService;

    public UserController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 根据用户ID查询用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public UserInfo getUserById(@PathVariable Long id) {
        return userInfoService.getCustomerById(id);
    }

    /**
     * 根据用户姓名查询用户
     * GET /api/users/search?name={name}
     */
    @GetMapping("/search")
    public List<UserInfo> searchUsers(@RequestParam String name) {
        return userInfoService.getCustomersByName(name);
    }

    /**
     * 智能查询用户
     * GET /api/users/smart-search?query={query}
     */
    @GetMapping("/smart-search")
    public List<UserInfo> smartSearchUsers(@RequestParam String query) {
        return userInfoService.smartSearch(query);
    }

    /**
     * 获取用户总数
     * GET /api/users/count
     */
    @GetMapping("/count")
    public int getUserCount() {
        return userInfoService.getCustomerCount();
    }
}
