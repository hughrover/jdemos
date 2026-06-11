package agent.demo.pinyin;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 拼音转换性能基准测试
 * 测试 pinyin4j 将1000个中文名转换为拼音的耗时
 *
 * @author Diego Liu
 * @since 2026/6/11 10:25
 */
public class PinyinBenchMark {

    // 常用姓氏
    private static final String[] SURNAMES = {
        "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈",
        "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
        "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏",
        "陶", "姜", "戚", "谢", "邹", "苏", "潘", "葛", "范", "彭",
        "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任",
        "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛",
        "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬",
        "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康",
        "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和", "穆",
        "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄"
    };

    // 常用名字用字
    private static final String[] NAME_CHARS = {
        "伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "洋",
        "艳", "勇", "军", "杰", "娟", "涛", "明", "超", "秀兰", "霞",
        "平", "刚", "桂英", "华", "飞", "玉兰", "萍", "红", "玉梅", "辉",
        "建华", "建国", "建军", "志强", "志明", "志远", "文", "斌", "博", "宇",
        "浩", "皓", "子涵", "梓涵", "雨泽", "宇轩", "浩然", "子豪", "子墨", "梓豪",
        "欣", "心", "新", "鑫", "信", "星", "兴", "行", "杏", "幸",
        "雅", "亚", "娅", "娅楠", "娅红", "延", "严", "言", "岩", "炎"
    };

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println("===== 拼音转换性能基准测试 =====\n");

        // 生成1000个随机中文名
        List<String> names = generateChineseNames(1000);
        System.out.println("已生成 " + names.size() + " 个中文名\n");

        // 预热 JVM
        System.out.println("正在预热 JVM...");
        warmup(names);
        System.out.println("预热完成\n");

        // 测试 pinyin4j
        long pinyin4jTime = benchmarkPinyin4j(names);

        // 输出结果
        printResults(pinyin4jTime, names.size());
    }

    /**
     * 生成指定数量的随机中文名
     */
    private static List<String> generateChineseNames(int count) {
        List<String> names = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String surname = SURNAMES[RANDOM.nextInt(SURNAMES.length)];
            String givenName = NAME_CHARS[RANDOM.nextInt(NAME_CHARS.length)];
            names.add(surname + givenName);
        }
        return names;
    }

    /**
     * 预热 JVM，让 JIT 编译器优化代码
     */
    private static void warmup(List<String> names) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        for (int i = 0; i < 100; i++) {
            for (String name : names) {
                try {
                    for (char c : name.toCharArray()) {
                        PinyinHelper.toHanyuPinyinStringArray(c, format);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    /**
     * 测试 pinyin4j 性能
     */
    private static long benchmarkPinyin4j(List<String> names) {
        System.out.println("--- pinyin4j ---");

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        long startTime = System.currentTimeMillis();

        List<String> results = new ArrayList<>(names.size());
        for (String name : names) {
            StringBuilder pinyin = new StringBuilder();
            for (char c : name.toCharArray()) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyin.append(pinyinArray[0]);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            results.add(pinyin.toString());
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // 打印前10个结果作为示例
        System.out.println("示例转换结果:");
        for (int i = 0; i < Math.min(10, names.size()); i++) {
            System.out.printf("  %s -> %s%n", names.get(i), results.get(i));
        }
        System.out.printf("总耗时: %dms%n", totalTime);
        System.out.printf("平均每个名字: %.3fms%n%n", (double) totalTime / names.size());

        return totalTime;
    }

    /**
     * 打印结果
     */
    private static void printResults(long pinyin4jTime, int nameCount) {
        System.out.println("===== 测试结果 =====");
        System.out.printf("pinyin4j 总耗时: %dms (平均: %.3fms/个)%n",
                pinyin4jTime, (double) pinyin4jTime / nameCount);

        System.out.println("\n结论:");
        System.out.println("pinyin4j 是一个功能完整的拼音转换库，");
        System.out.println("支持多种拼音格式和声调标注。");
        System.out.println("对于1000个中文名的转换，耗时在毫秒级别，性能表现良好。");
    }
}
