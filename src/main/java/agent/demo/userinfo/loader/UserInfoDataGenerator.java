package agent.demo.userinfo.loader;

import agent.demo.userinfo.model.UserInfo;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.*;

/**
 * 用户信息数据生成器
 * 生成1000个模拟客户信息
 */
public class UserInfoDataGenerator {

    private static final String[] SURNAMES = {
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
        "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
        "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏",
        "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
        "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦",
        "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳",
        "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺",
        "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常",
        "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余",
        "元", "卜", "顾", "孟", "平", "黄", "和", "穆", "萧", "尹"
    };

    private static final String[] GIVEN_NAMES = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军",
        "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰", "霞",
        "平", "刚", "桂英", "文", "华", "飞", "玉兰", "桂兰", "素梅", "秀珍",
        "玉梅", "玉英", "素英", "春梅", "海燕", "建", "红", "小红", "小明", "小华",
        "建华", "建国", "建军", "志强", "志明", "志远", "志豪", "志伟", "志刚", "志军",
        "海涛", "海波", "海燕", "海华", "海明", "海龙", "海翔", "海鹏", "海军", "海峰",
        "国强", "国华", "国明", "国军", "国庆", "国平", "国栋", "国梁", "国柱", "国才",
        "德明", "德华", "德军", "德强", "德伟", "德才", "德海", "德山", "德福", "德贵",
        "春华", "春明", "春生", "春燕", "春花", "春丽", "春芳", "春梅", "春兰", "春桃",
        "秋菊", "秋梅", "秋兰", "秋芳", "秋华", "秋明", "秋生", "秋燕", "秋花", "秋丽"
    };

    private static final String[] GENDERS = {"男", "女"};

    private static final String[] COMPANIES = {
        "阿里巴巴", "腾讯", "百度", "华为", "小米", "京东", "美团", "字节跳动",
        "网易", "拼多多", "滴滴", "快手", "携程", "知乎", "微博", "哔哩哔哩",
        "中国银行", "工商银行", "建设银行", "农业银行", "招商银行", "交通银行",
        "中国移动", "中国联通", "中国电信", "中国石油", "中国石化", "中国中车",
        "万科", "恒大", "碧桂园", "融创", "绿地", "保利", "华润", "中海"
    };

    private static final String[] POSITIONS = {
        "软件工程师", "产品经理", "项目经理", "设计师", "测试工程师",
        "运维工程师", "数据分析师", "算法工程师", "前端开发", "后端开发",
        "全栈工程师", "架构师", "技术总监", "CTO", "CEO", "CFO",
        "市场经理", "销售经理", "人力资源经理", "财务经理", "行政经理",
        "实习生", "初级工程师", "中级工程师", "高级工程师", "专家工程师",
        "研究员", "科学家", "教授", "讲师", "顾问"
    };

    private static final String[] CITIES = {
        "北京", "上海", "广州", "深圳", "杭州", "南京", "成都", "重庆",
        "武汉", "西安", "天津", "苏州", "长沙", "郑州", "青岛", "大连",
        "宁波", "厦门", "福州", "济南", "昆明", "贵阳", "南昌", "太原",
        "石家庄", "哈尔滨", "长春", "沈阳", "合肥", "兰州"
    };

    private static final String[] DISTRICTS = {
        "朝阳区", "海淀区", "西城区", "东城区", "丰台区", "浦东新区",
        "徐汇区", "静安区", "黄浦区", "天河区", "越秀区", "福田区",
        "南山区", "罗湖区", "西湖区", "江干区", "鼓楼区", "玄武区",
        "锦江区", "青羊区", "武侯区", "江岸区", "江汉区", "雁塔区",
        "碑林区", "和平区", "河西区", "天心区", "岳麓区", "开福区"
    };

    private static final Random RANDOM = new Random(42); // 固定种子，保证数据可重现

    /**
     * 生成1000个模拟客户信息
     */
    public static List<UserInfo> generateCustomers(int count) {
        List<UserInfo> customers = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            UserInfo user = new UserInfo();
            user.setId((long) i);

            // 生成姓名
            String surname = SURNAMES[RANDOM.nextInt(SURNAMES.length)];
            String givenName = GIVEN_NAMES[RANDOM.nextInt(GIVEN_NAMES.length)];
            String fullName = surname + givenName;
            user.setName(fullName);

            // 生成年龄（18-70岁）
            user.setAge(18 + RANDOM.nextInt(53));

            // 生成性别
            user.setGender(GENDERS[RANDOM.nextInt(GENDERS.length)]);

            // 生成电话（11位手机号）
            user.setPhone(generatePhone());

            // 生成邮箱
            user.setEmail(generateEmail(fullName, i));

            // 生成地址
            String city = CITIES[RANDOM.nextInt(CITIES.length)];
            String district = DISTRICTS[RANDOM.nextInt(DISTRICTS.length)];
            user.setAddress(city + "市" + district + "街道" + (RANDOM.nextInt(100) + 1) + "号");

            // 生成公司
            user.setCompany(COMPANIES[RANDOM.nextInt(COMPANIES.length)]);

            // 生成职位
            user.setPosition(POSITIONS[RANDOM.nextInt(POSITIONS.length)]);

            customers.add(user);
        }

        return customers;
    }

    /**
     * 获取汉字拼音
     */
    private static String getPinyin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder pinyin = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    pinyin.append(pinyinArray[0]);
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                // 非汉字字符，直接跳过
            }
        }
        return pinyin.toString();
    }

    /**
     * 获取拼音首字母
     */
    private static String getPinyinInitial(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder initial = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyinArray != null && pinyinArray.length > 0) {
                    initial.append(pinyinArray[0].charAt(0));
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                // 非汉字字符，直接跳过
            }
        }
        return initial.toString();
    }

    /**
     * 生成手机号
     */
    private static String generatePhone() {
        String[] prefixes = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
                            "150", "151", "152", "153", "155", "156", "157", "158", "159",
                            "170", "176", "177", "178",
                            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};
        String prefix = prefixes[RANDOM.nextInt(prefixes.length)];
        StringBuilder phone = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            phone.append(RANDOM.nextInt(10));
        }
        return phone.toString();
    }

    /**
     * 生成邮箱
     */
    private static String generateEmail(String name, int id) {
        String[] domains = {"qq.com", "163.com", "126.com", "gmail.com", "outlook.com", "hotmail.com"};
        String domain = domains[RANDOM.nextInt(domains.length)];

        // 使用拼音作为邮箱前缀
        String pinyin = getPinyin(name);
        if (pinyin.isEmpty()) {
            pinyin = "user";
        }

        return pinyin.toLowerCase() + id + "@" + domain;
    }

    /**
     * 主方法，用于测试数据生成
     */
    public static void main(String[] args) {
        List<UserInfo> customers = generateCustomers(1000);
        System.out.println("生成了 " + customers.size() + " 个客户信息");
        System.out.println("前5个客户：");
        for (int i = 0; i < 5 && i < customers.size(); i++) {
            System.out.println(customers.get(i));
        }
    }
}
