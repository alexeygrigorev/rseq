package com.itshared.rseq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class WordMatcherTest {

    private static final String DEFINITION_POS_REGEX = "(NN[PS]{0,2}|NP\\+?|NN\\+|LNK)";
    public static final EnhancedMatcher<Word> DEFINITION_MATCHER = posRegexp(DEFINITION_POS_REGEX);

    private static final EnhancedMatcher<Word> isOrAre = word("is").or(word("are"));
    private static final EnhancedMatcher<Word> POS_DET = pos("DT");

    public static EnhancedMatcher<Word> definition() {
        return DEFINITION_MATCHER.captureAs("definition");
    }

    public static EnhancedMatcher<Word> word(String token) {
        return BeanMatchers.eq(Word.class, "token", token);
    }

    public static EnhancedMatcher<Word> pos(String posTag) {
        return BeanMatchers.eq(Word.class, "pos", posTag);
    }

    public static EnhancedMatcher<Word> posRegexp(String posRegex) {
        return BeanMatchers.regex(Word.class, "pos", posRegex);
    }

    @Test
    public void findPattern_first() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("wave function", "LNK"), w(",", ","), w("i", "FW"), w("is", "VBZ"), w("the", "DT"),
                w("imaginary unit", "LNK"), w(",", ","), w("ħ", "NN"), w("is", "VBZ"), w("the", "DT"),
                w("reduced Planck constant", "LNK"));

        Pattern<Word> pattern = Pattern.create(word("p"), word("is"), POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Map<String, Word> variables = Collections.singletonMap("definition", w("wave function", "LNK"));
        Match<Word> match = new Match<Word>(0, sentence.subList(0, 4), variables);
        List<Match<Word>> expectedMatch = Arrays.asList(match);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_last() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("wave function", "LNK"), w(",", ","), w("ħ", "NN"), w("is", "VBZ"), w("the", "DT"),
                w("reduced Planck constant", "LNK"));
        Pattern<Word> pattern = Pattern.create(word("ħ"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Map<String, Word> variables = Collections.singletonMap("definition",
                w("reduced Planck constant", "LNK"));
        Match<Word> match = new Match<Word>(5, sentence.subList(5, 5 + 4), variables);
        List<Match<Word>> expectedMatch = Arrays.asList(match);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_twoInARow() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("definition1", "LNK"), w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("definition2", "LNK"));
        Pattern<Word> pattern = Pattern.create(word("p"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Match<Word> match1 = new Match<>(0, sentence.subList(0, 4), Collections.singletonMap("definition",
                w("definition1", "LNK")));
        Match<Word> match2 = new Match<>(4, sentence.subList(4, sentence.size()), Collections.singletonMap(
                "definition", w("definition2", "LNK")));
        List<Match<Word>> expectedMatch = Arrays.asList(match1, match2);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_noMatch() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("wave function", "LNK"), w(",", ","), w("ħ", "NN"), w("is", "VBZ"), w("the", "DT"),
                w("reduced Planck constant", "LNK"));

        Pattern<Word> pattern = Pattern.create(word("h"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);
        assertTrue(actualMatch.isEmpty());
    }

    @Test
    public void findPattern_noMatchAtTheEnd() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"));

        Pattern<Word> pattern = Pattern.create(word("h"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);
        assertTrue(actualMatch.isEmpty());
    }

    @Test
    public void findPattern_noMatchAtTheEnd_2() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"));

        Pattern<Word> pattern = Pattern.create(word("p"), word("is"));
        List<Match<Word>> actualMatch = pattern.find(sentence);
        assertTrue(actualMatch.isEmpty());
    }


    @Test
    public void find_in() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("wave function", "LNK"), w("i", "FW"),
                w("imaginary unit", "LNK"), w("h", "NN"), w("reduced Planck constant", "LNK"));

        Set<String> identifiers = new HashSet<String>(Arrays.asList("p", "h", "i"));
        Matcher<Word> identifier = BeanMatchers.in(Word.class, "token", identifiers).captureAs("identifier");

        Pattern<Word> pattern = Pattern.create(identifier, definition());
        List<Match<Word>> matches = pattern.find(sentence);

        List<Pair<String, String>> results = extractDefinitions(matches);

        List<Pair<String, String>> expected = Arrays.asList(Pair.of("p", "wave function"),
                Pair.of("i", "imaginary unit"), Pair.of("h", "reduced Planck constant"));
        assertEquals(expected, results);
    }

    private List<Pair<String, String>> extractDefinitions(List<Match<Word>> matches) {
        List<Pair<String, String>> results = new ArrayList<Pair<String, String>>();
        for (Match<Word> match : matches) {
            String id = match.getVariable("identifier").getToken();
            String def = match.getVariable("definition").getToken();
            results.add(Pair.of(id, def));
        }
        return results;
    }

    @Test
    public void findPattern_optional() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("wave function", "LNK"), w(",", ","), w("i", "FW"), w("is", "VBZ"),
                w("imaginary unit", "LNK"), w(",", ","), w("ħ", "NN"), w("is", "VBZ"), w("the", "DT"),
                w("reduced Planck constant", "LNK"));

        Set<String> identifiers = new HashSet<String>(Arrays.asList("p", "i", "ħ"));
        Matcher<Word> identifier = BeanMatchers.in(Word.class, "token", identifiers).captureAs("identifier");

        Pattern<Word> pattern = Pattern.create(identifier, isOrAre, POS_DET.optional(), definition());
        List<Match<Word>> matches = pattern.find(sentence);

        List<Pair<String, String>> results = extractDefinitions(matches);

        List<Pair<String, String>> expected = Arrays.asList(Pair.of("p", "wave function"),
                Pair.of("i", "imaginary unit"), Pair.of("ħ", "reduced Planck constant"));
        assertEquals(expected, results);
    }

    @Test
    public void findPattern_oneOrMore_middle() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w(",", ","), w(",", ","), w(",", ","),
                w("reduced Planck constant", "LNK"));

        Matcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",").oneOrMoreGreedy();

        Pattern<Word> pattern = Pattern.create(comma);
        List<Match<Word>> matches = pattern.find(sentence);

        assertEquals(1, matches.size());
        Match<Word> actualMatch = matches.get(0);
        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> expectedMatch = new Match<Word>(1, sentence.subList(1, 1 + 3), variables);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_oneOrMore_end() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("reduced Planck constant", "LNK"), w(",", ","),
                w(",", ","), w(",", ","));

        Matcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",").oneOrMoreGreedy();

        Pattern<Word> pattern = Pattern.create(comma);
        List<Match<Word>> matches = pattern.find(sentence);

        assertEquals(1, matches.size());
        Match<Word> actualMatch = matches.get(0);
        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> expectedMatch = new Match<Word>(2, sentence.subList(2, 2 + 3), variables);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_oneOrMore_twoMatches() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("w", "LNK"), w(",", ","), w(",", ","),
                w(",", ","), w("p", "LNK"), w("w", "LNK"), w(",", ","), w(",", ","), w("w", "LNK"));

        Matcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",").oneOrMoreGreedy();

        Pattern<Word> pattern = Pattern.create(comma);
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(2, sentence.subList(2, 2 + 3), variables);
        Match<Word> match2 = new Match<Word>(7, sentence.subList(7, 7 + 2), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2);

        assertEquals(expectedMatches, matches);
    }


    @Test
    public void findPattern_optional_last() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"));
        EnhancedMatcher<Word> p = BeanMatchers.eq(Word.class, "token", "p");
        EnhancedMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");

        Pattern<Word> pattern = Pattern.create(p, comma.optional());
        List<Match<Word>> result = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        assertEquals(1, result.size());
        assertEquals(new Match<>(0, sentence, variables), result.get(0));
    }

    @Test
    public void findPattern_optional_severalLast() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"));
        EnhancedMatcher<Word> p = BeanMatchers.eq(Word.class, "token", "p");
        EnhancedMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");

        Pattern<Word> pattern = Pattern.create(p, p.optional(), comma.optional());
        List<Match<Word>> result = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        assertEquals(1, result.size());
        assertEquals(new Match<>(0, sentence, variables), result.get(0));
    }

    @Test
    public void findPattern_oneOrMore_zeroOrMore_severalMatchers() {
        List<Word> sentence = Arrays.asList(
                w("p", "LNK"), w("w", "LNK"), w(",", ","), w(",", ","), w(",", ","), 
                w("token", "POS"), 
                w("p", "LNK"), w("w", "LNK"), w("i", "LNK"), w(",", ","), w(",", ","), 
                w("w", "LNK"));

        Set<String> identifiers = new HashSet<String>(Arrays.asList("p", "w", "i"));
        EnhancedMatcher<Word> identifier = BeanMatchers.in(Word.class, "token", identifiers);
        EnhancedMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");

        Pattern<Word> pattern = Pattern.create(identifier.oneOrMoreGreedy(), comma.zeroOrMoreGreedy());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(0, sentence.subList(0, 5), variables);
        Match<Word> match2 = new Match<Word>(6, sentence.subList(6, 6 + 5), variables);
        Match<Word> match3 = new Match<Word>(11, sentence.subList(11, 12), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2, match3);

        assertEquals(expectedMatches, matches);
    }

    public static Word w(String word, String tag) {
        return new Word(word, tag);
    }

}
