package com.alexeygrigorev.rseq;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class QuantifiersTest {

    final XMatcher<String> matcher = new XMatcher<String>() {
        @Override
        public boolean match(String object) {
            return "*".equals(object);
        }
    };

    @Test
    public void all_true() {
        List<String> sequence = Arrays.asList("*", "*", "*");
        assertTrue(matcher.all(sequence));
    }

    @Test
    public void all_true_empty() {
        List<String> sequence = Collections.emptyList();
        assertTrue(matcher.all(sequence));
    }

    @Test
    public void all_false() {
        List<String> sequence = Arrays.asList("*", "*", "1");
        assertFalse(matcher.all(sequence));
    }

    @Test
    public void some_true_allElements() {
        List<String> sequence = Arrays.asList("*", "*", "*");
        assertTrue(matcher.any(sequence));
    }
    
    @Test
    public void some_true_oneElement() {
        List<String> sequence = Arrays.asList("1", "1", "*");
        assertTrue(matcher.any(sequence));
    }

    @Test
    public void some_false_empty() {
        List<String> sequence = Collections.emptyList();
        assertFalse(matcher.any(sequence));
    }

    @Test
    public void some_false_noElements() {
        List<String> sequence = Collections.emptyList();
        assertFalse(matcher.any(sequence));
    }

    @Test
    public void none_true() {
        List<String> sequence = Arrays.asList("1", "1", "1");
        assertTrue(matcher.none(sequence));
    }

    @Test
    public void none_true_empty() {
        List<String> sequence = Collections.emptyList();
        assertTrue(matcher.none(sequence));
    }

    @Test
    public void none_false() {
        List<String> sequence = Arrays.asList("*", "*", "1");
        assertFalse(matcher.none(sequence));
    }
}
