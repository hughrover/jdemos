package agent.demo.insurance.service;

import agent.demo.insurance.model.InsurancePolicy;

import java.util.List;

/**
 * 保单查询服务接口
 */
public interface InsurancePolicyService {

    /**
     * 根据保单号查询保单
     */
    InsurancePolicy getPolicyById(String policyId);

    /**
     * 根据投保人姓名查询保单
     */
    List<InsurancePolicy> getPoliciesByPolicyholderName(String policyholderName);

    /**
     * 根据用户ID查询保单
     */
    List<InsurancePolicy> getPoliciesByUserId(Long userId);

    /**
     * 获取所有保单
     */
    List<InsurancePolicy> getAllPolicies();

    /**
     * 获取保单总数
     */
    int getPolicyCount();
}
