package agent.demo.branch.model;

/**
 * 网点业绩数据类
 *
 * @author Diego
 * @since 2024
 */
public class BranchPerformance {

    private String bankName;
    private String branchName;
    private String city;
    private int policyCount;
    private double premiumAmount;
    private String period;

    public BranchPerformance() {}

    public BranchPerformance(String bankName, String branchName, String city,
                             int policyCount, double premiumAmount, String period) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.city = city;
        this.policyCount = policyCount;
        this.premiumAmount = premiumAmount;
        this.period = period;
    }

    // Getters and Setters
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getPolicyCount() { return policyCount; }
    public void setPolicyCount(int policyCount) { this.policyCount = policyCount; }

    public double getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(double premiumAmount) { this.premiumAmount = premiumAmount; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    @Override
    public String toString() {
        return String.format("网点名称：%s%s\n出单件数：%d件\n保费总和：%.1f万元\n统计周期：%s",
                bankName, branchName, policyCount, premiumAmount, period);
    }
}
