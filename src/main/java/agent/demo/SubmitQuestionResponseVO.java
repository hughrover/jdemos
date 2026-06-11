package agent.demo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 提交问题返回参数
 *
 * @author zhonger
 * @Date 2024/5/7 18:17
 * @see
 * @since
 */
@Data
@ToString
public class SubmitQuestionResponseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long conversationId;
    /**
     * 三方会话ID-（招募会话ID）
     */
    private String thirdConversationId;
    /**
     * 问答ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recordId;

    /**
     * 问题
     */
    private String question;

    /**
     * 答案
     */
    private String answer;


    /**
     * 当前客户ID
     */
    private String customerId;

    /**
     * 当前客户姓名
     */
    private String customerName;
    /**
     * 当前客户任务ID
     */
    private String taskId;

    /**
     * 当前客户性别
     */
    private String sex;

    /**
     * 当前是否是追问,客户需要回复此内容，再下次提问时携带此字段
     */
    private Boolean replyFlag;

    /**
     * 答案是否结束
     */
    private Boolean endAnswerFlag;

    /**
     * 思考过程
     */
    private String thinkContent;


    /**
     * 思考耗时
     */
    private Long thinkCost;

    /**
     * 答案是否结束
     */
    private Boolean endThinkContentFlag;

    /**
     * 问答时间
     */
    private String qaTime;

    /**
     * 业务线类型类型为01：问答记录类型 01:用户交互问答 02:业务信息采集问答 03:业务记录  04:欢迎语
     * 业务线类型类型为02：问答记录类型 01:用户交互问答 02:业务信息采集问答 03:业务记录  04:欢迎语 05：科技个险-获取客户信息 06:科技个险-意图识别
     */
    private String recordType;

    /**
     * 客户标签
     */
    private String customerLabel;

    /**
     * 完成度
     */
    private Integer completion;

    /**
     * 缺失项（银保需要计算 缺失项信息）
     */
    private String missItem;

    /**
     * 新对话标志
     */
    @Schema(description = "新进入对话标志")
    private Boolean newSessionFlag;

    /**
     * 问答时间
     */
    private Boolean isHaiWenAnswer;

    /**
     * 客户id列表
     */
    private List<String> customerIdList;


    /**
     * 是否银发客群
     */
    private Boolean silverHairFlag;

    /**
     * 理财经理客情分析情况
     */
    private String analyze;

    /**
     * 理财经理客情分析情况
     */
    private String analyzeFlag;

    /**
     * 客户活动ID
     */
    private String activityId;

    /**
     * 客户信息
     */
    private Boolean customerInfoFlag;

    /**
     * 招募客户ID
     */
    @Schema(description = "客户ID")
    private String zmCustomerId;

    /**
     * 招募客户姓名，对于企业微信时客户昵称
     */
    @Schema(description = "客户姓名")
    private String zmCustomerName;

    /**
     * 是否是招募请求
     */
    private Boolean isZm;

    private String isInterview; //1:是，0:不是

    private String intentionQuestion;//上一轮问题

    private String subordinateCode;//面谈建议-属员编号

    private String displayType;//图表展示标志


    //面谈建议个人画像：componentsType:‘cppc’
    //团队分析达成情况进度条内容：componentsType:'cpbz'
    //团队分析达成情况成员对比table表格：componentsType:'cmct'
    private String componentsType;//渲染图标类型
}