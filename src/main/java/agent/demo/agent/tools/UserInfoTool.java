package agent.demo.agent.tools;

import agent.demo.userinfo.model.UserInfo;
import agent.demo.userinfo.service.UserInfoService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * 用户信息查询工具
 * 封装UserInfoService，供智能体调用
 */
@Component
public class UserInfoTool implements Function<UserInfoTool.SearchRequest, String> {

    private final UserInfoService userInfoService;

    public UserInfoTool(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    /**
     * 搜索请求
     */
    public static class SearchRequest {
        private String query;

        public SearchRequest() {}

        public SearchRequest(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    @Override
    public String apply(SearchRequest request) {
        String query = request.getQuery();
        List<UserInfo> results = userInfoService.smartSearch(query);

        if (results.isEmpty()) {
            return "未找到匹配的用户信息：" + query;
        }

        if (results.size() == 1) {
            return "找到 1 个匹配的用户：\n\n" + formatUser(results.get(0));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("找到 ").append(results.size()).append(" 个匹配的用户：\n\n");
        for (int i = 0; i < Math.min(results.size(), 20); i++) {
            UserInfo user = results.get(i);
            sb.append(i + 1).append(". ").append(user.getName())
              .append(" - ").append(user.getCompany())
              .append(" - ").append(user.getPosition())
              .append("\n");
        }

        if (results.size() > 20) {
            sb.append("\n... 还有 ").append(results.size() - 20).append(" 个结果");
        }

        return sb.toString();
    }

    /**
     * 格式化用户信息
     */
    private String formatUser(UserInfo user) {
        return String.format(
            "姓名：%s\n年龄：%d\n性别：%s\n电话：%s\n邮箱：%s\n地址：%s\n公司：%s\n职位：%s",
            user.getName(),
            user.getAge(),
            user.getGender(),
            user.getPhone(),
            user.getEmail(),
            user.getAddress(),
            user.getCompany(),
            user.getPosition()
        );
    }
}
