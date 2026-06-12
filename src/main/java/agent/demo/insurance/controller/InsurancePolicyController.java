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
     * 根据policyholderId查询保单
     * GET /api/insurance-policies?policyholderId={id}
     */
    @GetMapping
    public List<InsurancePolicy> getPoliciesByPolicyholderId(
            @RequestParam Long policyholderId) {
        return insurancePolicyService.getPoliciesByPolicyholderId(policyholderId);
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
