package com.alexeygrigorev.rseq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.alexeygrigorev.rseq.BeanMatchers;
import com.alexeygrigorev.rseq.Match;
import com.alexeygrigorev.rseq.TransformerToList;
import com.alexeygrigorev.rseq.Matcher;
import com.alexeygrigorev.rseq.Matchers;
import com.alexeygrigorev.rseq.Pattern;
import com.alexeygrigorev.rseq.TransformerToElement;
import com.alexeygrigorev.rseq.XMatcher;

public class WordMatcherTest {

    private static final String DEFINITION_POS_REGEX = "(NN[PS]{0,2}|NP\\+?|NN\\+|LNK)";
    public static final XMatcher<Word> DEFINITION_MATCHER = posRegexp(DEFINITION_POS_REGEX);

    private static final XMatcher<Word> isOrAre = word("is").or(word("are"));
    private static final XMatcher<Word> POS_DET = pos("DT");

    public static XMatcher<Word> definition() {
        return DEFINITION_MATCHER.captureAs("definition");
    }

    public static XMatcher<Word> word(String token) {
        return BeanMatchers.eq(Word.class, "token", token);
    }

    public static XMatcher<Word> pos(String posTag) {
        return BeanMatchers.eq(Word.class, "pos", posTag);
    }

    public static XMatcher<Word> posRegexp(String posRegex) {
        return BeanMatchers.regex(Word.class, "pos", posRegex);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pattern_empty_throwsException() {
        Pattern.create();
    }

    @Test
    public void findPattern_first() {
        List<Word> sentence = sentence("p/LNK", "is/VBZ", "the/DT", "wave function/LNK", ",/,", "i/FW",
                "is/VBZ", "the/DT", "imaginary unit/LNK", ",/,", "ħ/NN", "is/VBZ", "the/DT",
                "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word("p"), word("is"), POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Map<String, Word> variables = Collections.singletonMap("definition", w("wave function/LNK"));
        Match<Word> match = new Match<Word>(0, sentence.subList(0, 4), variables);
        List<Match<Word>> expectedMatch = Arrays.asList(match);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_last() {
        List<Word> sentence = sentence("p/LNK", "is/VBZ", "the/DT", "wave function/LNK", ",/,", "ħ/NN",
                "is/VBZ", "the/DT", "reduced Planck constant/LNK");
        Pattern<Word> pattern = Pattern.create(word("ħ"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Map<String, Word> variables = Collections
                .singletonMap("definition", w("reduced Planck constant/LNK"));
        Match<Word> match = new Match<Word>(5, sentence.subList(5, 5 + 4), variables);
        List<Match<Word>> expectedMatch = Arrays.asList(match);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_twoInARow() {
        List<Word> sentence = sentence("p/LNK", "is/VBZ", "the/DT", "definition1/LNK", "p/LNK", "is/VBZ",
                "the/DT", "definition2/LNK");
        Pattern<Word> pattern = Pattern.create(word("p"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);

        Match<Word> match1 = new Match<>(0, sentence.subList(0, 4), Collections.singletonMap("definition",
                w("definition1/LNK")));
        Match<Word> match2 = new Match<>(4, sentence.subList(4, sentence.size()), Collections.singletonMap(
                "definition", w("definition2/LNK")));
        List<Match<Word>> expectedMatch = Arrays.asList(match1, match2);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_noMatch() {
        List<Word> sentence = sentence("p/LNK", "is/VBZ", "the/DT", "wave function/LNK", ",/,", "ħ/NN",
                "is/VBZ", "the/DT", "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word("h"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);
        assertTrue(actualMatch.isEmpty());
    }

    @Test
    public void findPattern_noMatchAtTheEnd() {
        List<Word> sentence = sentence("p/LNK", "is/VBZ", "the/DT");

        Pattern<Word> pattern = Pattern.create(word("h"), isOrAre, POS_DET, definition());
        List<Match<Word>> actualMatch = pattern.find(sentence);
        assertTrue(actualMatch.isEmpty());
    }

    @Test
    public void findPattern_noMatchAtTheEnd_2() {
        List<Word> sentence = sentence("p/LNK");

        Pattern<Word> pattern = Pattern.create(word("p"), word("is"));
        List<Match<Word>> actualMatch = pattern.find(sentence);
        assertTrue(actualMatch.isEmpty());
    }

    @Test
    public void find_in() {
        List<Word> sentence = sentence("p/LNK", "wave function/LNK", "i/FW", "imaginary unit/LNK", "h/NN",
                "reduced Planck constant/LNK");

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
        List<Word> sentence = sentence("p/LNK", "is/VBZ", "the/DT", "wave function/LNK", ",/,", "i/FW",
                "is/VBZ", "imaginary unit/LNK", ",/,", "ħ/NN", "is/VBZ", "the/DT",
                "reduced Planck constant/LNK");

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
        List<Word> sentence = sentence("p/LNK", ",/,", ",/,", ",/,", "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word(",").oneOrMoreGreedy());
        List<Match<Word>> matches = pattern.find(sentence);

        assertEquals(1, matches.size());
        Match<Word> actualMatch = matches.get(0);
        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> expectedMatch = new Match<Word>(1, sentence.subList(1, 1 + 3), variables);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_oneOrMore_end() {
        List<Word> sentence = sentence("p/LNK", "reduced Planck constant/LNK", ",/,", ",/,", ",/,");

        Pattern<Word> pattern = Pattern.create(word(",").oneOrMoreGreedy());
        List<Match<Word>> matches = pattern.find(sentence);

        assertEquals(1, matches.size());
        Match<Word> actualMatch = matches.get(0);
        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> expectedMatch = new Match<Word>(2, sentence.subList(2, 2 + 3), variables);

        assertEquals(expectedMatch, actualMatch);
    }

    @Test
    public void findPattern_oneOrMore_noMatch() {
        List<Word> sentence = sentence("p/LNK", "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word(",").oneOrMore(), word(","));
        List<Match<Word>> matches = pattern.find(sentence);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void findPattern_oneOrMoreGreedy_noMatch() {
        List<Word> sentence = sentence("p/LNK", "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word(",").oneOrMoreGreedy());
        List<Match<Word>> matches = pattern.find(sentence);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void findPattern_zeroOrMore_noMatch() {
        List<Word> sentence = sentence("p/LNK", "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word(".").zeroOrMore(), word(","));
        List<Match<Word>> matches = pattern.find(sentence);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void findPattern_zeroOrMoreGreedy_noMatch() {
        List<Word> sentence = sentence("p/LNK", "reduced Planck constant/LNK");

        Pattern<Word> pattern = Pattern.create(word(".").zeroOrMoreGreedy(), word(","));
        List<Match<Word>> matches = pattern.find(sentence);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void findPattern_oneOrMore_twoMatches() {
        List<Word> sentence = sentence("p/LNK", "w/LNK", ",/,", ",/,", ",/,", "p/LNK", "w/LNK", ",/,", ",/,",
                "w/LNK");

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
        List<Word> sentence = sentence("p/LNK");
        XMatcher<Word> p = BeanMatchers.eq(Word.class, "token", "p");
        XMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");

        Pattern<Word> pattern = Pattern.create(p, comma.optional());
        List<Match<Word>> result = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        assertEquals(1, result.size());
        assertEquals(new Match<>(0, sentence, variables), result.get(0));
    }

    @Test
    public void findPattern_optional_severalLast() {
        List<Word> sentence = sentence("p/LNK");
        XMatcher<Word> p = BeanMatchers.eq(Word.class, "token", "p");
        XMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");

        Pattern<Word> pattern = Pattern.create(p, p.optional(), comma.optional());
        List<Match<Word>> result = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        assertEquals(1, result.size());
        assertEquals(new Match<>(0, sentence, variables), result.get(0));
    }

    @Test
    public void findPattern_oneOrMore_zeroOrMore_severalMatchers() {
        List<Word> sentence = sentence("p/LNK", "w/LNK", ",/,", ",/,", ",/,", "token/POS", "p/LNK", "w/LNK",
                "i/LNK", ",/,", ",/,", "w/LNK");

        Set<String> identifiers = new HashSet<String>(Arrays.asList("p", "w", "i"));
        XMatcher<Word> identifier = BeanMatchers.in(Word.class, "token", identifiers);
        XMatcher<Word> comma = word(",");

        Pattern<Word> pattern = Pattern.create(identifier.oneOrMoreGreedy(), comma.zeroOrMoreGreedy());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(0, sentence.subList(0, 5), variables);
        Match<Word> match2 = new Match<Word>(6, sentence.subList(6, 6 + 5), variables);
        Match<Word> match3 = new Match<Word>(11, sentence.subList(11, 12), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2, match3);

        assertEquals(expectedMatches, matches);
    }

    @Test
    public void findPattern_oneOrMoreLazy() {
        List<Word> sentence = sentence("p/LNK", "w/LNK", ",/,", ",/,", ",/,", "token/POS", "p/LNK", "w/LNK",
                "i/LNK", ",/,", ",/,", "w/LNK");

        XMatcher<Word> any = Matchers.anything();
        XMatcher<Word> comma = word(",");

        Pattern<Word> pattern = Pattern.create(any.oneOrMore(), comma.zeroOrMore());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(0, sentence.subList(0, 5), variables);
        Match<Word> match2 = new Match<Word>(5, sentence.subList(5, 5 + 6), variables);
        Match<Word> match3 = new Match<Word>(11, sentence.subList(11, 11 + 1), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2, match3);

        assertEquals(expectedMatches, matches);
    }

    @Test
    public void findPattern_zeroOrMoreGreedy_noMatchForZeroOrMore() {
        List<Word> sentence = sentence("w/LNK", ",/,", "i/LNK", ",/,", ",/,", "i/LNK");
        Pattern<Word> pattern = Pattern.create(word(".").zeroOrMoreGreedy(), word(",").oneOrMore());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(1, sentence.subList(1, 1 + 1), variables);
        Match<Word> match2 = new Match<Word>(3, sentence.subList(3, 3 + 2), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2);

        assertEquals(expectedMatches, matches);
    }

    @Test
    public void findPattern_allZeroOrMoreGreedy() {
        List<Word> sentence = sentence("w/LNK", ",/,", "i/LNK", ",/,", ",/,", "i/LNK");
        Pattern<Word> pattern = Pattern.create(word(".").zeroOrMoreGreedy(), word(",").zeroOrMore());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(1, sentence.subList(1, 1 + 1), variables);
        Match<Word> match2 = new Match<Word>(3, sentence.subList(3, 3 + 2), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2);

        assertEquals(expectedMatches, matches);
    }

    @Test
    public void findPattern_zeroOrMoreLazy_noMatchForFirstMatcher() {
        List<Word> sentence = sentence("w/LNK", ",/,", "i/LNK", ",/,", ",/,", "i/LNK");

        Pattern<Word> pattern = Pattern.create(word(".").zeroOrMore(), word(",").zeroOrMore(), word("i"));
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(1, sentence.subList(1, 1 + 2), variables);
        Match<Word> match2 = new Match<Word>(3, sentence.subList(3, 3 + 3), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2);

        assertEquals(expectedMatches, matches);
    }

    @Test
    public void findPattern_wildcardGroupMatchers_capture() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "``/``", "reduced/NN", "Planck/NN",
                "constant/NN", "''/''", "./.");

        Pattern<Word> pattern = Pattern.create(word("``"), pos("NN").oneOrMore().captureAs("link"),
                word("''"));
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> captured = result.getCapturedGroup("link");

        List<Word> expected = sentence("reduced/NN", "Planck/NN", "constant/NN");
        assertEquals(expected, captured);
    }

    @Test
    public void findPattern_zeroOrMore_capture() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "``/``", "reduced/NN", "Planck/NN",
                "constant/NN", "''/''", "./.");

        Pattern<Word> pattern = Pattern.create(word("``"), pos("NN").zeroOrMore().captureAs("link"),
                word("''"));
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> captured = result.getCapturedGroup("link");

        List<Word> expected = sentence("reduced/NN", "Planck/NN", "constant/NN");
        assertEquals(expected, captured);
    }

    @Test
    public void findPattern_zeroOrMore_emptyCapture() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "``/``", "''/''", "./.");

        Pattern<Word> pattern = Pattern.create(word("``"), pos("NN").zeroOrMore().captureAs("link"),
                word("''"));

        List<Match<Word>> result = pattern.find(sentence);
        assertEquals(1, result.size());

        Match<Word> match = result.get(0);
        List<Word> captured = match.getCapturedGroup("link");
        assertTrue(captured.isEmpty());
    }

    
    @Test
    public void findPattern_groupMatchers() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "reduced/NN", "Planck/NN", "constant/NN",
                "./.", "./.");

        XMatcher<Word> the = word("the");
        XMatcher<Word> noun = pos("NN");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(noun, noun, noun), dot);
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> expected = sentence("the/DT", "reduced/NN", "Planck/NN", "constant/NN", "./.");
        assertEquals(expected, result.getMatchedSubsequence());
    }

    @Test
    public void findPattern_groupMatchers_noMatch() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "reduced/NN", "Planck/NN", "constant/NN",
                "./.", "./.");

        XMatcher<Word> the = word("the");
        XMatcher<Word> adj = pos("JJ");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(adj, adj, adj), dot);
        List<Match<Word>> result = pattern.find(sentence);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findPattern_groupMatchers_noMatch2() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "reduced/NN", "Planck/NN", "constant/NN",
                "./.", "./.");

        XMatcher<Word> the = word("the");
        XMatcher<Word> noun = pos("NN");
        XMatcher<Word> adj = pos("JJ");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(noun, noun, adj), dot);
        List<Match<Word>> result = pattern.find(sentence);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findPattern_groupMatchers_capture() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "reduced/NN", "Planck/NN", "constant/NN",
                "./.", "./.");

        XMatcher<Word> the = word("the");
        XMatcher<Word> noun = pos("NN");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(noun, noun, noun).captureAs("noun"), dot);
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> expected = sentence("reduced/NN", "Planck/NN", "constant/NN");
        assertEquals(expected, result.getCapturedGroup("noun"));
    }

    @Test
    public void findPattern_groupMatcher_lazyMatchGroup() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "reduced/NN", "Planck/NN", "constant/NN",
                "./.", "./.");

        XMatcher<Word> the = word("the");
        XMatcher<Word> any = Matchers.anything();
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(any.oneOrMore()).captureAs("noun"), dot);
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> expected = sentence("reduced/NN", "Planck/NN", "constant/NN");
        assertEquals(expected, result.getCapturedGroup("noun"));
    }

    @Test
    public void findPattern_groupMatcher_lazyMatchGroupLast() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "Planck/NN", "constant/NN", "./.", "./.");

        XMatcher<Word> is = word("is");
        XMatcher<Word> the = word("the");
        XMatcher<Word> any = Matchers.anything();
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(is,
                Matchers.group(the.optional(), any.oneOrMore()).captureAs("noun"), dot);
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> expected = sentence("the/DT", "Planck/NN", "constant/NN");
        assertEquals(expected, result.getCapturedGroup("noun"));
    }

    @Test
    public void findPattern_groupMatcher_lazyMatchGroupLast_optional() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "Planck/NN", "constant/NN", "./.", "./.");

        XMatcher<Word> is = word("is");
        XMatcher<Word> the = word("the");
        XMatcher<Word> any = Matchers.anything();
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(is,
                Matchers.group(the.optional(), any.oneOrMore()).captureAs("noun"), dot);
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> expected = sentence("Planck/NN", "constant/NN");
        assertEquals(expected, result.getCapturedGroup("noun"));
    }

    @Test
    public void findPattern_groupMatcher_lastOptional_match() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "Planck/NN", "constant/NN");

        XMatcher<Word> the = word("the");
        XMatcher<Word> noun = pos("NN");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(noun, noun, dot.optional()));
        Match<Word> result = pattern.find(sentence).get(0);

        List<Word> expected = sentence("the/DT", "Planck/NN", "constant/NN");
        assertEquals(expected, result.getMatchedSubsequence());
    }

    @Test
    public void findPattern_groupMatcher_lastNotMatch() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "Planck/NN", "constant/NN");

        XMatcher<Word> the = word("the");
        XMatcher<Word> noun = pos("NN");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(noun, noun, dot));
        List<Match<Word>> result = pattern.find(sentence);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findPattern_groupCapturer_noMatch() {
        List<Word> sentence = sentence("ħ/NN", "is/VBZ", "the/DT", "Planck/NN", "constant/NN");

        XMatcher<Word> the = word("the");
        XMatcher<Word> noun = pos("NN");
        XMatcher<Word> dot = word(".");

        Pattern<Word> pattern = Pattern.create(the, Matchers.group(noun, noun, dot).captureAs("name"));
        List<Match<Word>> result = pattern.find(sentence);

        assertTrue(result.isEmpty());
    }


    @Test
    public void findPattern_customMatcher_optional() {
        List<Word> sentence = sentence("is/VBZ", "these/DT", "Planck/NN", "constant/NN");
        XMatcher<Word> withS = new XMatcher<Word>() {
            @Override
            public boolean match(Word object) {
                return object.getToken().contains("s");
            }
        };

        Pattern<Word> pattern = Pattern.create(withS, withS.optional());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(0, sentence.subList(0, 0 + 2), variables);
        Match<Word> match2 = new Match<Word>(3, sentence.subList(3, 3 + 1), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2);

        assertEquals(expectedMatches, matches);
    }

    
    @Test
    public void findPattern_customMatcher_oneOrMore() {
        List<Word> sentence = sentence("is/VBZ", "these/DT", "Planck/NN", "constant/NN");
        XMatcher<Word> withS = new XMatcher<Word>() {
            @Override
            public boolean match(Word object) {
                return object.getToken().contains("s");
            }
        };

        Pattern<Word> pattern = Pattern.create(withS.oneOrMore());
        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(0, sentence.subList(0, 0 + 2), variables);
        Match<Word> match2 = new Match<Word>(3, sentence.subList(3, 3 + 1), variables);
        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2);

        assertEquals(expectedMatches, matches);
    }

    @Test
    public void findPattern_customMatcher_MatcherIntergace() {
        List<Word> sentence = sentence("is/VBZ", "these/DT", "Planck/NN", "constant/NN");
        Matcher<Word> withS = new Matcher<Word>() {
            @Override
            public boolean match(Word object) {
                return object.getToken().contains("s");
            }
        };

        Pattern<Word> pattern = Pattern.create(withS);
        List<Match<Word>> matches = pattern.find(sentence);
        assertEquals(3, matches.size());
    }
    
    @Test
    public void replacePattern_oneComma_everything() {
        List<Word> sentence = sentence(",/,", ",/,", ",/,", ",/,");

        XMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");
        Pattern<Word> pattern = Pattern.create(comma.oneOrMore());

        List<Word> result = pattern.replace(sentence, new TransformerToList<Word>() {
            @Override
            public List<Word> transform(Match<Word> match) {
                return Arrays.asList(new Word(",", ","));
            }
        });

        List<Word> expectedResult = Arrays.asList(new Word(",", ","));
        assertEquals(expectedResult, result);
    }

    @Test
    public void replacePattern_contentInQuotes() {
        List<Word> sentence = sentence("'/'", "a/POS", "b/POS", "'/'", "c/POS", "d/POS", "e/POS", "f/POS",
                "'/'", "g/POS", "h/POS", "'/'");

        XMatcher<Word> any = Matchers.anything();
        XMatcher<Word> quote = Matchers.eq(w("'/'"));
        Pattern<Word> pattern = Pattern.create(quote, any.zeroOrMore(), quote);

        List<Word> result = pattern.replaceToOne(sentence, new TransformerToElement<Word>() {
            @Override
            public Word transform(Match<Word> match) {
                List<String> toJoin = new ArrayList<String>();
                for (Word word : match.getMatchedSubsequence()) {
                    toJoin.add(word.getToken());
                }
                String joined = StringUtils.join(toJoin.subList(1, toJoin.size() - 1), " ");
                return new Word("'" + joined + "'", "Q");
            }
        });

        List<Word> expectedResult = sentence("'a b'/Q", "c/POS", "d/POS", "e/POS", "f/POS", "'g h'/Q");
        assertEquals(expectedResult, result);
    }

    @Test
    public void replacePattern_oneComma_oneOrMore() {
        List<Word> sentence = sentence(",/,", ",/,", ",/,", "w/LNK", ",/,", "token/POS", "p/LNK", ",/,",
                ",/,", "w/LNK", "i/LNK", ",/,", ",/,", "w/LNK", ",/,", ",/,");

        XMatcher<Word> comma = BeanMatchers.eq(Word.class, "token", ",");
        Pattern<Word> pattern = Pattern.create(comma.oneOrMore());

        List<Match<Word>> matches = pattern.find(sentence);

        Map<String, Word> variables = Collections.emptyMap();
        Match<Word> match1 = new Match<Word>(0, sentence.subList(0, 3), variables);
        Match<Word> match2 = new Match<Word>(4, sentence.subList(4, 4 + 1), variables);
        Match<Word> match3 = new Match<Word>(7, sentence.subList(7, 7 + 2), variables);
        Match<Word> match4 = new Match<Word>(11, sentence.subList(11, 11 + 2), variables);
        Match<Word> match5 = new Match<Word>(14, sentence.subList(14, 14 + 2), variables);

        List<Match<Word>> expectedMatches = Arrays.asList(match1, match2, match3, match4, match5);
        assertEquals(expectedMatches, matches);

        List<Word> result = pattern.replace(sentence, new TransformerToList<Word>() {
            @Override
            public List<Word> transform(Match<Word> match) {
                return sentence(",/,");
            }
        });

        List<Word> expectedResult = sentence(",/,", "w/LNK", ",/,", "token/POS", "p/LNK", ",/,", "w/LNK",
                "i/LNK", ",/,", "w/LNK", ",/,");
        assertEquals(expectedResult, result);
    }

    public static List<Word> sentence(String... words) {
        List<Word> res = new ArrayList<Word>();
        for (String word : words) {
            res.add(w(word));
        }
        return res;
    }

    public static Word w(String word) {
        String[] split = word.split("/");
        return new Word(split[0], split[1]);
    }

}
