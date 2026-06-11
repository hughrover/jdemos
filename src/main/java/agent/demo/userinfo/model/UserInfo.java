package agent.demo.userinfo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息数据模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private String gender;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 公司
     */
    private String company;

    /**
     * 职位
     */
    private String position;

    /**
     * 姓名拼音（用于拼音匹配）
     */
    private String namePinyin;

    /**
     * 姓名拼音首字母（用于首字母匹配）
     */
    private String namePinyinInitial;
}
