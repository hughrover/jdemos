package agent.demo.insurance.controller;

import agent.demo.insurance.model.InsurancePolicy;
import agent.demo.insurance.service.InsurancePolicyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 保单查询控制器
 * 暴露REST API接口
 */
@RestController
@RequestMapping("/api/insurance-policies")
public class InsurancePolicyController {

    private final InsurancePolicyService insurancePolicyService;

    public InsurancePolicyController(InsurancePolicyService insurancePolicyService) {
        this.insurancePolicyService = insurancePolicyService;
    }

    /**
     * 根据保单号查询保单
     * GET /api/insurance-policies/{policyId}
     */
    @GetMapping("/{policyId}")
    public InsurancePolicy getPolicyById(@PathVariable String policyId) {
        return insurancePolicyService.getPolicyById(policyId);
    }

    /**
     * 根据投保人姓名查询保单
     * GET /api/insurance-policies?policyholderName={name}
     */
    @GetMapping
    public List<InsurancePolicy> getPolicies(
            @RequestParam(required = false) String policyholderName,
            @RequestParam(required = false) Long userId) {

        if (policyholderName != null && !policyholderName.isEmpty()) {
            return insurancePolicyService.getPoliciesByPolicyholderName(policyholderName);
        }

        if (userId != null) {
            return insurancePolicyService.getPoliciesByUserId(userId);
        }

        return insurancePolicyService.getAllPolicies();
    }

    /**
     * 获取保单总数
     * GET /api/insurance-policies/count
     */
    @GetMapping("/count")
    public int getPolicyCount() {
        return insurancePolicyService.getPolicyCount();
    }
}
