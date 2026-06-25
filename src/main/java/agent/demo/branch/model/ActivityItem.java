package agent.demo.branch.model;

/**
 * 活动明细类
 *
 * @author Diego
 * @since 2024
 */
public class ActivityItem {

    private String name;
    private String date;
    private String type;
    private int participantCount;

    public ActivityItem() {}

    public ActivityItem(String name, String date, String type, int participantCount) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.participantCount = participantCount;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    @Override
    public String toString() {
        return String.format("%s - %s - %s - 参与人数：%d人",
                name, date, type, participantCount);
    }
}
