package agent.demo.branch.model;

import java.util.List;

/**
 * 网点活动数据类
 *
 * @author Diego
 * @since 2024
 */
public class BranchActivity {

    private String bankName;
    private String branchName;
    private String city;
    private int activityCount;
    private List<ActivityItem> activityList;
    private String period;

    public BranchActivity() {}

    public BranchActivity(String bankName, String branchName, String city,
                          int activityCount, List<ActivityItem> activityList, String period) {
        this.bankName = bankName;
        this.branchName = branchName;
        this.city = city;
        this.activityCount = activityCount;
        this.activityList = activityList;
        this.period = period;
    }

    // Getters and Setters
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getActivityCount() { return activityCount; }
    public void setActivityCount(int activityCount) { this.activityCount = activityCount; }

    public List<ActivityItem> getActivityList() { return activityList; }
    public void setActivityList(List<ActivityItem> activityList) { this.activityList = activityList; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("网点名称：%s%s\n", bankName, branchName));
        sb.append(String.format("活动场次：%d场\n", activityCount));
        sb.append(String.format("统计周期：%s\n\n", period));
        sb.append("活动明细：\n");

        if (activityList != null) {
            for (int i = 0; i < activityList.size(); i++) {
                sb.append(String.format("%d. %s\n", i + 1, activityList.get(i).toString()));
            }
        }

        return sb.toString();
    }
}
