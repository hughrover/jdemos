package agent.demo.insurance.service;

import agent.demo.insurance.model.InsurancePolicy;

import java.util.List;

/**
 * 保单查询服务接口
 */
public interface InsurancePolicyService {

    /**
     * 根据policyholderId查询保单
     */
    List<InsurancePolicy> getPoliciesByPolicyholderId(Long policyholderId);

    /**
     * 获取保单总数
     */
    int getPolicyCount();
}
