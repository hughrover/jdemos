package agent.demo.userinfo.matcher;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 拼音匹配工具类
 * 提供拼音转换和匹配功能
 */
public class PinyinMatcher {

    private static final HanyuPinyinOutputFormat DEFAULT_FORMAT;

    static {
        DEFAULT_FORMAT = new HanyuPinyinOutputFormat();
        DEFAULT_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        DEFAULT_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 获取汉字的拼音
     */
    public static String getPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }

        StringBuilder pinyin = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, DEFAULT_FORMAT);
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
    public static String getPinyinInitial(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }

        StringBuilder initial = new StringBuilder();
        for (char c : chinese.toCharArray()) {
            try {
                String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, DEFAULT_FORMAT);
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
     * 判断两个汉字的拼音是否相同
     */
    public static boolean isPinyinMatch(String chinese1, String chinese2) {
        if (chinese1 == null || chinese2 == null) {
            return false;
        }
        String pinyin1 = getPinyin(chinese1);
        String pinyin2 = getPinyin(chinese2);
        return pinyin1.equalsIgnoreCase(pinyin2);
    }

    /**
     * 判断拼音首字母是否相同
     */
    public static boolean isPinyinInitialMatch(String chinese1, String chinese2) {
        if (chinese1 == null || chinese2 == null) {
            return false;
        }
        String initial1 = getPinyinInitial(chinese1);
        String initial2 = getPinyinInitial(chinese2);
        return initial1.equalsIgnoreCase(initial2);
    }

    /**
     * 判断拼音是否包含关键词
     */
    public static boolean isPinyinContains(String chinese, String keyword) {
        if (chinese == null || keyword == null) {
            return false;
        }
        String pinyin = getPinyin(chinese);
        return pinyin.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * 判断拼音首字母是否包含关键词
     */
    public static boolean isPinyinInitialContains(String chinese, String keyword) {
        if (chinese == null || keyword == null) {
            return false;
        }
        String initial = getPinyinInitial(chinese);
        return initial.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * 计算两个拼音的相似度（简单实现）
     */
    public static double calculateSimilarity(String pinyin1, String pinyin2) {
        if (pinyin1 == null || pinyin2 == null) {
            return 0;
        }

        String lower1 = pinyin1.toLowerCase();
        String lower2 = pinyin2.toLowerCase();

        if (lower1.equals(lower2)) {
            return 1.0;
        }

        // 使用编辑距离计算相似度
        int maxLen = Math.max(lower1.length(), lower2.length());
        if (maxLen == 0) {
            return 1.0;
        }

        int distance = levenshteinDistance(lower1, lower2);
        return 1.0 - (double) distance / maxLen;
    }

    /**
     * 计算编辑距离
     */
    private static int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
