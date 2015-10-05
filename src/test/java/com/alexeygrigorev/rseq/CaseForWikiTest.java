package com.alexeygrigorev.rseq;

import static com.alexeygrigorev.rseq.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.alexeygrigorev.rseq.Match;
import com.alexeygrigorev.rseq.Pattern;

/**
 * Case tests used for tutorial on project wiki
 */
public class CaseForWikiTest {

    @Test
    public void stringSeq_basicMatch() {
        List<String> words = Arrays.asList("Where E is the energy and λ is the wavelength".split(" "));

        Pattern<String> pattern = Pattern.create(eq("E"), eq("is"), eq("the"), eq("energy"));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(1, matches.size());

        Match<String> match = matches.get(0);
        assertEquals(Arrays.asList("E", "is", "the", "energy"), match.getMatchedSubsequence());
        assertEquals(1, match.matchedFrom());
        assertEquals(1 + 4, match.matchedTo());
    }

    @Test
    public void stringSeq_or() {
        List<String> words = Arrays.asList("Where E is the energy and λ is the wavelength".split(" "));

        Pattern<String> pattern = Pattern.create(eq("E").or(eq("λ")), eq("is"), eq("the"),
                eq("energy").or(eq("wavelength")));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        assertEquals(Arrays.asList("E", "is", "the", "energy"), matches.get(0).getMatchedSubsequence());
        assertEquals(Arrays.asList("λ", "is", "the", "wavelength"), matches.get(1).getMatchedSubsequence());
    }

    @Test
    public void stringSeq_in() {
        List<String> words = Arrays.asList("Where E is the energy and λ is the wavelength".split(" "));

        List<String> ids = Arrays.asList("E", "λ", "p", "m", "c");
        Pattern<String> pattern = Pattern.create(in(ids), eq("is"), eq("the"),
                in("energy", "wavelength"));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        assertEquals(Arrays.asList("E", "is", "the", "energy"), matches.get(0).getMatchedSubsequence());
        assertEquals(Arrays.asList("λ", "is", "the", "wavelength"), matches.get(1).getMatchedSubsequence());
    }

    @Test
    public void stringSeq_optional() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        List<String> ids = Arrays.asList("E", "λ", "p", "m", "c");
        Pattern<String> pattern = Pattern.create(in(ids), eq("is"), eq("the").optional(),
                in("energy", "wavelength"));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        assertEquals(Arrays.asList("E", "is", "the", "energy"), matches.get(0).getMatchedSubsequence());
        assertEquals(Arrays.asList("λ", "is", "wavelength"), matches.get(1).getMatchedSubsequence());
    }

    final XMatcher<String> oneLetterRegexp = new XMatcher<String>() {
        @Override
        public boolean match(String object) {
            return object.matches(".");
        }
    };

    @Test
    public void stringSeq_own() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        Pattern<String> pattern = Pattern.create(oneLetterRegexp, eq("is"), eq("the").optional(),
                in("energy", "wavelength"));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        assertEquals(Arrays.asList("E", "is", "the", "energy"), matches.get(0).getMatchedSubsequence());
        assertEquals(Arrays.asList("λ", "is", "wavelength"), matches.get(1).getMatchedSubsequence());
    }

    @Test
    public void stringSeq_capture() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        XMatcher<String> anything = anything();
        Pattern<String> pattern = Pattern.create(oneLetterRegexp.captureAs("ID"), eq("is"), eq("the").optional(),
                anything.captureAs("DEF"));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        Match<String> match1 = matches.get(0);
        assertEquals("E", match1.getVariable("ID"));
        assertEquals("energy", match1.getVariable("DEF"));

        Match<String> match2 = matches.get(1);
        assertEquals("λ", match2.getVariable("ID"));
        assertEquals("wavelength", match2.getVariable("DEF"));
    }

    @Test
    public void stringSeq_group() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        XMatcher<String> anything = anything();
        Pattern<String> pattern = Pattern.create(
                oneLetterRegexp.captureAs("ID"), eq("is"), 
                group(eq("the").optional(), anything).captureAs("DEF"));
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        Match<String> match1 = matches.get(0);
        assertEquals("E", match1.getVariable("ID"));
        assertEquals(Arrays.asList("the", "energy"), match1.getCapturedGroup("DEF"));

        Match<String> match2 = matches.get(1);
        assertEquals("λ", match2.getVariable("ID"));
        assertEquals(Arrays.asList("wavelength"), match2.getCapturedGroup("DEF"));
    }

}
