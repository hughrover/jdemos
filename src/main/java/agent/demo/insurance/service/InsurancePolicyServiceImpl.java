package agent.demo.insurance.service;

import agent.demo.insurance.model.InsurancePolicy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 保单查询服务实现
 */
@Service
public class InsurancePolicyServiceImpl implements InsurancePolicyService {

    private static final String DATA_FILE_PATH = "data/insurance_policies.json";

    private List<InsurancePolicy> policies;

    public InsurancePolicyServiceImpl() {
        loadPolicies();
    }

    /**
     * 加载保单数据
     */
    private void loadPolicies() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 尝试从文件系统加载
            File file = new File(DATA_FILE_PATH);
            if (file.exists()) {
                policies = objectMapper.readValue(file, new TypeReference<List<InsurancePolicy>>() {});
                return;
            }

            // 尝试从classpath加载
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DATA_FILE_PATH);
            if (inputStream != null) {
                policies = objectMapper.readValue(inputStream, new TypeReference<List<InsurancePolicy>>() {});
                inputStream.close();
                return;
            }

            // 如果文件不存在，使用空列表
            System.out.println("保单数据文件不存在: " + DATA_FILE_PATH);
            policies = new ArrayList<>();

        } catch (IOException e) {
            System.err.println("加载保单数据失败: " + e.getMessage());
            policies = new ArrayList<>();
        }
    }

    @Override
    public InsurancePolicy getPolicyById(String policyId) {
        return policies.stream()
                .filter(policy -> policy.getPolicyId().equals(policyId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<InsurancePolicy> getPoliciesByPolicyholderName(String policyholderName) {
        return policies.stream()
                .filter(policy -> policy.getPolicyholderName().equals(policyholderName))
                .collect(Collectors.toList());
    }

    @Override
    public List<InsurancePolicy> getPoliciesByUserId(Long userId) {
        return policies.stream()
                .filter(policy -> policy.getPolicyholderId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<InsurancePolicy> getAllPolicies() {
        return new ArrayList<>(policies);
    }

    @Override
    public int getPolicyCount() {
        return policies.size();
    }
}
