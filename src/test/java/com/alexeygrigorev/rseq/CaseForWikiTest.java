package com.alexeygrigorev.rseq;

import static com.alexeygrigorev.rseq.Matchers.anything;
import static com.alexeygrigorev.rseq.Matchers.eq;
import static com.alexeygrigorev.rseq.Matchers.group;
import static com.alexeygrigorev.rseq.Matchers.in;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

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
        Pattern<String> pattern = Pattern.create(in(ids), eq("is"), eq("the"), in("energy", "wavelength"));
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
        Pattern<String> pattern = Pattern.create(oneLetterRegexp.captureAs("ID"), eq("is"), eq("the")
                .optional(), anything.captureAs("DEF"));
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
    public void stringSeq_replace() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        XMatcher<String> anything = anything();
        Pattern<String> pattern = Pattern.create(oneLetterRegexp.captureAs("ID"), eq("is"), eq("the")
                .optional(), anything.captureAs("DEF"));

        final Map<String, String> db = new HashMap<String, String>();
        db.put("energy", "https://en.wikipedia.org/wiki/Energy");
        db.put("wavelength", "https://en.wikipedia.org/wiki/Wavelength");

        List<String> actual = pattern.replace(words, new TransformerToList<String>() {
            @Override
            public List<String> transform(Match<String> match) {
                List<String> result = new ArrayList<>(match.getMatchedSubsequence());
                String link = db.get(match.getVariable("DEF"));
                result.add("(see " + link + ")");
                return result;
            }
        });

        assertEquals("Where E is the energy (see https://en.wikipedia.org/wiki/Energy)"
                + " and λ is wavelength (see https://en.wikipedia.org/wiki/Wavelength)",
                StringUtils.join(actual, " "));
    }

    @Test
    public void stringSeq_replaceOne() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        XMatcher<String> anything = anything();
        Pattern<String> pattern = Pattern.create(oneLetterRegexp.captureAs("ID"), eq("is"), eq("the")
                .optional(), anything.captureAs("DEF"));

        List<String> actual = pattern.replaceToOne(words, new TransformerToElement<String>() {
            @Override
            public String transform(Match<String> match) {
                return StringUtils.join(match.getMatchedSubsequence(), " ");
            }
        });

        assertEquals(Arrays.asList("Where", "E is the energy", "and", "λ is wavelength"), actual);
    }

    @Test
    public void stringSeq_oneOrMore() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        XMatcher<String> treeLettersOrLess = new XMatcher<String>() {
            @Override
            public boolean match(String object) {
                return object.length() <= 3;
            }
        };

        Pattern<String> pattern = Pattern.create(treeLettersOrLess.oneOrMore());
        List<Match<String>> matches = pattern.find(words);
        assertEquals(2, matches.size());

        assertEquals(Arrays.asList("E", "is", "the"), matches.get(0).getMatchedSubsequence());
        assertEquals(Arrays.asList("and", "λ", "is"), matches.get(1).getMatchedSubsequence());
    }

    @Test
    public void stringSeq_group() {
        List<String> words = Arrays.asList("Where E is the energy and λ is wavelength".split(" "));

        XMatcher<String> anything = anything();
        Pattern<String> pattern = Pattern.create(oneLetterRegexp.captureAs("ID"), eq("is"),
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

    @Test
    public void wordSeq() {
        XMatcher<Word> id = new XMatcher<Word>() {
            @Override
            public boolean match(Word word) {
                return word.getToken().length() == 1;
            }
        };

        XMatcher<Word> is = new XMatcher<Word>() {
            @Override
            public boolean match(Word word) {
                return "is".equals(word.getToken());
            }
        };

        XMatcher<Word> the = new XMatcher<Word>() {
            @Override
            public boolean match(Word word) {
                return "the".equals(word.getToken());
            }
        };

        XMatcher<Word> adjective = new XMatcher<Word>() {
            @Override
            public boolean match(Word word) {
                return "JJ".equals(word.getPos());
            }
        };

        XMatcher<Word> noun = new XMatcher<Word>() {
            @Override
            public boolean match(Word word) {
                return "NN".equals(word.getPos());
            }
        };

        Pattern<Word> pattern = Pattern.create(id.captureAs("ID"), is, group(the, adjective.optional(), noun)
                .captureAs("DEF"));

        List<Word> words = words("where/WRB U/NNP is/VBZ the/DT internal/JJ energy/NN ,/, "
                + "T/NNP is/VBZ the/DT absolute/JJ temperature/NN ,/, and/CC S/NNP is/VBZ "
                + "the/DT entropy/NN ./.");

        List<Match<Word>> result = pattern.find(words);
        assertEquals(3, result.size());

        Match<Word> match1 = result.get(0);
        assertEquals("U", match1.getVariable("ID").getToken());
        assertEquals(words("the/DT internal/JJ energy/NN"), match1.getCapturedGroup("DEF"));

        Match<Word> match2 = result.get(1);
        assertEquals("T", match2.getVariable("ID").getToken());
        assertEquals(words("the/DT absolute/JJ temperature/NN"), match2.getCapturedGroup("DEF"));

        Match<Word> match3 = result.get(2);
        assertEquals("S", match3.getVariable("ID").getToken());
        assertEquals(words("the/DT entropy/NN"), match3.getCapturedGroup("DEF"));
    }

    private List<Word> words(String sentence) {
        String[] split = sentence.split(" ");
        List<Word> result = new ArrayList<Word>();
        for (String word : split) {
            String[] token = word.split("/");
            result.add(new Word(token[0], token[1]));
        }
        return result;
    }

    @Test
    public void wordSeq_beanMatcher() {
        XMatcher<Word> id = BeanMatchers.regex(Word.class, "token", ".");
        XMatcher<Word> is = BeanMatchers.eq(Word.class, "token", "is");
        XMatcher<Word> the = BeanMatchers.eq(Word.class, "token", "the");
        XMatcher<Word> adjective = BeanMatchers.eq(Word.class, "pos", "JJ");
        XMatcher<Word> noun = BeanMatchers.eq(Word.class, "pos", "NN");

        Pattern<Word> pattern = Pattern.create(id.captureAs("ID"), is, group(the, adjective.optional(), noun)
                .captureAs("DEF"));

        List<Word> words = words("where/WRB U/NNP is/VBZ the/DT internal/JJ energy/NN ,/, "
                + "T/NNP is/VBZ the/DT absolute/JJ temperature/NN ,/, and/CC S/NNP is/VBZ "
                + "the/DT entropy/NN ./.");

        List<Match<Word>> result = pattern.find(words);
        assertEquals(3, result.size());

        Match<Word> match1 = result.get(0);
        assertEquals("U", match1.getVariable("ID").getToken());
        assertEquals(words("the/DT internal/JJ energy/NN"), match1.getCapturedGroup("DEF"));

        Match<Word> match2 = result.get(1);
        assertEquals("T", match2.getVariable("ID").getToken());
        assertEquals(words("the/DT absolute/JJ temperature/NN"), match2.getCapturedGroup("DEF"));

        Match<Word> match3 = result.get(2);
        assertEquals("S", match3.getVariable("ID").getToken());
        assertEquals(words("the/DT entropy/NN"), match3.getCapturedGroup("DEF"));
    }

}
