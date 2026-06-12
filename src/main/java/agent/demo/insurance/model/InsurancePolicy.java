package agent.demo.insurance.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保单数据模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsurancePolicy {

    /**
     * 保单号
     */
    private String policyId;

    /**
     * 投保人ID（关联customers.json中的用户）
     */
    private Long policyholderId;

    /**
     * 投保人姓名
     */
    private String policyholderName;

    /**
     * 被保险人姓名
     */
    private String insuredName;

    /**
     * 险种
     */
    private String insuranceType;

    /**
     * 保费（元）
     */
    private Double premium;

    /**
     * 保额（元）
     */
    private Double sumInsured;

    /**
     * 保险开始日期
     */
    private String startDate;

    /**
     * 保险结束日期
     */
    private String endDate;

    /**
     * 保单状态
     */
    private String status;
}
