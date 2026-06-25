package agent.demo.branch.model;

/**
 * 网点概览聚合类
 *
 * @author Diego
 * @since 2024
 */
public class BranchOverview {

    private String bankName;
    private String branchName;
    private String city;
    private BranchPerformance performance;
    private BranchActivity activities;
    private String customerProfile;
    private String peerPerformance;
    private String bankDynamics;
    private String localNews;

    public BranchOverview() {}

    public BranchOverview(String bankName, String branchName, String city) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.city = city;
    }

    // Getters and Setters
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public BranchPerformance getPerformance() { return performance; }
    public void setPerformance(BranchPerformance performance) { this.performance = performance; }

    public BranchActivity getActivities() { return activities; }
    public void setActivities(BranchActivity activities) { this.activities = activities; }

    public String getCustomerProfile() { return customerProfile; }
    public void setCustomerProfile(String customerProfile) { this.customerProfile = customerProfile; }

    public String getPeerPerformance() { return peerPerformance; }
    public void setPeerPerformance(String peerPerformance) { this.peerPerformance = peerPerformance; }

    public String getBankDynamics() { return bankDynamics; }
    public void setBankDynamics(String bankDynamics) { this.bankDynamics = bankDynamics; }

    public String getLocalNews() { return localNews; }
    public void setLocalNews(String localNews) { this.localNews = localNews; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== 网点概览：%s%s ===\n\n", bankName, branchName));

        // 网点业绩
        sb.append("【网点业绩】\n");
        if (performance != null) {
            sb.append(String.format("本地数据：出单件数 %d件，保费总和 %.1f万元\n",
                    performance.getPolicyCount(), performance.getPremiumAmount()));
        }
        if (peerPerformance != null) {
            sb.append(String.format("同业对比：%s\n", peerPerformance));
        }
        sb.append("\n");

        // 客经活动
        sb.append("【客经活动】\n");
        if (activities != null) {
            sb.append(String.format("本月活动：%d场\n", activities.getActivityCount()));
            if (activities.getActivityList() != null && !activities.getActivityList().isEmpty()) {
                sb.append("活动明细：\n");
                for (int i = 0; i < activities.getActivityList().size(); i++) {
                    sb.append(String.format("%d. %s\n", i + 1, activities.getActivityList().get(i).toString()));
                }
            }
        }
        sb.append("\n");

        // 客群特征
        sb.append("【客群特征】\n");
        if (customerProfile != null) {
            sb.append(String.format("潜客画像：%s\n", customerProfile));
        }
        sb.append("\n");

        // 周边时事
        sb.append("【周边时事】\n");
        if (bankDynamics != null) {
            sb.append(String.format("银行动态：%s\n", bankDynamics));
        }
        if (localNews != null) {
            sb.append(String.format("地区新闻：%s\n", localNews));
        }

        return sb.toString();
    }
}
