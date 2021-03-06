package com.alexeygrigorev.rseq;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.alexeygrigorev.rseq.BeanMatchers;
import com.alexeygrigorev.rseq.XMatcher;

public class BeanMatchersTest {

    @Test
    public void eq() {
        XMatcher<Word> matcher = BeanMatchers.eq(Word.class, "token", "StringVal");
        assertFalse(matcher.match(new Word("token", "pos")));
        assertTrue(matcher.match(new Word("StringVal", "pos")));
    }

    @Test
    public void eq_superclassMethod() {
        XMatcher<SuperWord> matcher = BeanMatchers.eq(SuperWord.class, "token", "StringVal");
        assertFalse(matcher.match(new SuperWord("token", "pos", 1, false)));
        assertTrue(matcher.match(new SuperWord("StringVal", "pos", 1, false)));
    }

    @Test
    public void eq_intValue() {
        XMatcher<SuperWord> matcher = BeanMatchers.eq(SuperWord.class, "value", 100);
        assertFalse(matcher.match(new SuperWord("token", "pos", 1, false)));
        assertTrue(matcher.match(new SuperWord("StringVal", "pos", 100, false)));
    }

    @Test
    public void eq_booleanValue() {
        XMatcher<SuperWord> matcher = BeanMatchers.eq(SuperWord.class, "flag", true);
        assertFalse(matcher.match(new SuperWord("token", "pos", 1, false)));
        assertTrue(matcher.match(new SuperWord("StringVal", "pos", 100, true)));
    }

    @Test
    public void eq_publicField() {
        XMatcher<SuperWord> matcher = BeanMatchers.eq(SuperWord.class, "field", "someValue");
        assertFalse(matcher.match(new SuperWord("token", "pos", "someOtherValue")));
        assertTrue(matcher.match(new SuperWord("StringVal", "pos", "someValue")));
    }

    @Test
    public void regex() {
        XMatcher<Word> matcher = BeanMatchers.regex(Word.class, "token", "a+");
        assertFalse(matcher.match(new Word("token", "pos")));
        assertTrue(matcher.match(new Word("aaaaaa", "pos")));
    }

    @Test
    public void regex_onSuperclass() {
        XMatcher<Word> matcher = BeanMatchers.regex(Word.class, "token", "a+");
        assertFalse(matcher.match(new SuperWord("token", "pos", "")));
        assertTrue(matcher.match(new SuperWord("aaaaaa", "pos", "")));
    }

    @Test
    public void in() {
        Set<String> values = new HashSet<String>(Arrays.asList("Word1", "Word2", "Word3"));

        XMatcher<Word> matcher = BeanMatchers.in(Word.class, "token", values);
        assertFalse(matcher.match(new Word("token", "pos")));
        assertTrue(matcher.match(new Word("Word2", "pos")));
    }

    static class SuperWord extends Word {
        private int value;
        private boolean flag;
        public String field;

        public SuperWord(String token, String pos, int value, boolean flag) {
            super(token, pos);
            this.value = value;
            this.flag = flag;
        }

        public SuperWord(String token, String pos, String field) {
            super(token, pos);
            this.field = field;
        }

        public int getValue() {
            return value;
        }

        public boolean isFlag() {
            return flag;
        }
    }
}
