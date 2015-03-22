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

    private static final Matcher<Word> isOrAre = word("is").or(word("are"));
    private static final Matcher<Word> POS_DET = pos("DT");

    public static CapturingMatcher<Word> definition() {
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
        List<Match<Word>> expectedMatch = Arrays.asList(new Match<Word>(0, variables));

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
        List<Match<Word>> expectedMatch = Arrays.asList(new Match<Word>(5, variables));

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_twoInARow() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("definition1", "LNK"), w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("definition2", "LNK"));
        Pattern<Word> pattern = Pattern.create(word("p"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Match<Word> match1 = new Match<>(0, Collections.singletonMap("definition", w("definition1", "LNK")));
        Match<Word> match2 = new Match<>(4, Collections.singletonMap("definition", w("definition2", "LNK")));
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
    public void findPattern_allIdentifiers() {
        List<Word> sentence = Arrays.asList(w("p", "LNK"), w("is", "VBZ"), w("the", "DT"),
                w("wave function", "LNK"), w(",", ","), w("i", "FW"), w("is", "VBZ"), w("the", "DT"),
                w("imaginary unit", "LNK"), w(",", ","), w("ħ", "NN"), w("is", "VBZ"), w("the", "DT"),
                w("reduced Planck constant", "LNK"));

        Set<String> identifiers = new HashSet<String>(Arrays.asList("p", "i", "ħ"));
        Matcher<Word> identifier = BeanMatchers.in(Word.class, "token", identifiers).captureAs("identifier");

        Pattern<Word> pattern = Pattern.create(identifier, isOrAre, POS_DET, definition());
        List<Match<Word>> matches = pattern.find(sentence);

        List<Pair<String, String>> results = new ArrayList<Pair<String, String>>();
        for (Match<Word> match : matches) {
            String id = match.getVariable("identifier").getToken();
            String def = match.getVariable("definition").getToken();
            results.add(Pair.of(id, def));
        }

        List<Pair<String, String>> expected = Arrays.asList(Pair.of("p", "wave function"),
                Pair.of("i", "imaginary unit"), Pair.of("ħ", "reduced Planck constant"));
        assertEquals(expected, results);
    }

    public static Word w(String word, String tag) {
        return new Word(word, tag);
    }

}
