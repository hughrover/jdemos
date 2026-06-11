package agent.demo.userinfo;

import agent.demo.userinfo.matcher.PinyinMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PinyinMatcher单元测试
 */
class PinyinMatcherTest {

    @Test
    void testGetPinyin() {
        assertEquals("zhangsan", PinyinMatcher.getPinyin("张三"));
        assertEquals("lisi", PinyinMatcher.getPinyin("李四"));
        assertEquals("wangwu", PinyinMatcher.getPinyin("王五"));
    }

    @Test
    void testGetPinyinInitial() {
        assertEquals("zs", PinyinMatcher.getPinyinInitial("张三"));
        assertEquals("ls", PinyinMatcher.getPinyinInitial("李四"));
        assertEquals("ww", PinyinMatcher.getPinyinInitial("王五"));
    }

    @Test
    void testIsPinyinMatch() {
        assertTrue(PinyinMatcher.isPinyinMatch("张三", "张三"));
        assertFalse(PinyinMatcher.isPinyinMatch("张三", "李四"));
    }

    @Test
    void testIsPinyinInitialMatch() {
        assertTrue(PinyinMatcher.isPinyinInitialMatch("张三", "张三"));
        assertTrue(PinyinMatcher.isPinyinInitialMatch("张三", "赵四")); // 都是zs
        assertFalse(PinyinMatcher.isPinyinInitialMatch("张三", "李四"));
    }

    @Test
    void testIsPinyinContains() {
        assertTrue(PinyinMatcher.isPinyinContains("张三", "zhang"));
        assertTrue(PinyinMatcher.isPinyinContains("张三", "san"));
        assertFalse(PinyinMatcher.isPinyinContains("张三", "li"));
    }

    @Test
    void testIsPinyinInitialContains() {
        assertTrue(PinyinMatcher.isPinyinInitialContains("张三", "z"));
        assertTrue(PinyinMatcher.isPinyinInitialContains("张三", "s"));
        assertFalse(PinyinMatcher.isPinyinInitialContains("张三", "l"));
    }

    @Test
    void testCalculateSimilarity() {
        double similarity = PinyinMatcher.calculateSimilarity("zhangsan", "zhangshan");
        assertTrue(similarity > 0.8); // 相似度应该很高

        double exactMatch = PinyinMatcher.calculateSimilarity("zhangsan", "zhangsan");
        assertEquals(1.0, exactMatch);

        double noMatch = PinyinMatcher.calculateSimilarity("zhangsan", "lisi");
        assertTrue(noMatch < 0.5);
    }

    @Test
    void testNullInput() {
        assertEquals("", PinyinMatcher.getPinyin(null));
        assertEquals("", PinyinMatcher.getPinyinInitial(null));
        assertFalse(PinyinMatcher.isPinyinMatch(null, "张三"));
        assertFalse(PinyinMatcher.isPinyinMatch("张三", null));
    }

    @Test
    void testEmptyInput() {
        assertEquals("", PinyinMatcher.getPinyin(""));
        assertEquals("", PinyinMatcher.getPinyinInitial(""));
        assertFalse(PinyinMatcher.isPinyinMatch("", "张三"));
        assertFalse(PinyinMatcher.isPinyinMatch("张三", ""));
    }
}
