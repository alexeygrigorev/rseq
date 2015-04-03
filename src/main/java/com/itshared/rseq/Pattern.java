package com.itshared.rseq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a pattern to be found in a sequence. The pattern is
 * composed of {@link Matcher}s, each of which is able to match one or more
 * elements of the sequence. When all matchers in the pattern match some
 * subsequence of the input sequence, we find a {@link Match}. The found
 * subsequences can then be transformed using the
 * {@link #replace(List, Transformer)} method.<br>
 * <br>
 * 
 * To create a pattern, use {@link #create(Matcher...)} method. To see available
 * Matcher implementations, see {@link Matchers} and {@link BeanMatchers}
 * 
 * 
 * @author Alexey Grigorev
 * @see Matcher
 * @see Match
 * @see Transformer
 *
 * @param <E>
 */
public class Pattern<E> {

    private final List<ParentMatcher<E>> matchers;

    private Pattern(List<ParentMatcher<E>> matchers) {
        this.matchers = matchers;
    }

    /**
     * Tries to match the pattern with all the subsequences of the input
     * sequence. If a subsequence is matched, then a {@link Match} object is
     * created.
     * 
     * @param sequence to be matched
     * @return list of found matches
     */
    public List<Match<E>> find(List<E> sequence) {
        MatchingContext<E> context = new MatchingContext<E>(matchers, sequence);
        initialize(context);

        // TODO: no need for iterator
        Iterator<Void> iterator = context.findIterator();
        while (iterator.hasNext()) {
            Iterator<E> matchIterator = context.matchIterator();
            boolean success = true;

            for (ParentMatcher<E> matcher : matchers) {
                if (!matchIterator.hasNext()) {
                    if (matcher.isOptional()) {
                        continue;
                    }
                    success = false;
                    break;
                }
                context.nextMatcher();
                E next = matchIterator.next();
                if (!matcher.match(next)) {
                    success = false;
                    break;
                }
            }

            if (success) {
                context.addSuccessfulMatch();
            }
            iterator.next();
        }

        return context.getAllResults();
    }

    private void initialize(MatchingContext<E> context) {
        for (ParentMatcher<E> matcher : matchers) {
            matcher.setContext(context);
            matcher.register(context);
        }
        for (ParentMatcher<E> matcher : matchers) {
            matcher.initialize(context);
        }
    }

    /**
     * Matches the pattern against the provided sequence, and then replaces all
     * found matches by applying transformation to all matched subsequences and
     * replaces their content in the place of the original subsequence. If there
     * are no matches found, then the provided sequence is returned back. <br>
     * <br>
     * Note that the method does not modify the suppled sequence, but it creates
     * and returns a new list. <br>
     * <br>
     * You can use this method then you need to replace each matched
     * subsequences by exactly one element. If you need to replace them by other
     * sequences or use any additional information (like captured variables, as
     * provided by {@link ParentMatcher#captureAs(String)}), then you should use
     * {@link #replaceMatched(List, MatchTransformer)} method instead
     * 
     * @param sequence to transform
     * @param transformer transformation to be applied to each matched
     *        subsequence
     * @return transformed sequence
     */
    public List<E> replace(List<E> sequence, final Transformer<E> transformer) {
        return replaceMatched(sequence, new MatchTransformer<E>() {
            @Override
            public List<E> transform(Match<E> match) {
                E result = transformer.transform(match.getMatchedSubsequence());
                return Collections.singletonList(result);
            }
        });
    }

    /**
     * Matches the pattern against the provided sequence, and then replaces all
     * found matches by applying transformation to all matched subsequences and
     * replaces their content in the place of the original subsequence. If there
     * are no matches found, then the provided sequence is returned back. <br>
     * <br>
     * Note that the method does not modify the suppled sequence, but it creates
     * and returns a new list. <br>
     * <br>
     * Use this method then you need some additional information about the
     * match, like variables (e.g. from {@link ParentMatcher#captureAs(String)})
     * or when the result of the transformation is also a sequence. However in
     * most cases you probably will need to transform each subsequence to a
     * single element, and thus, use {@link #replace(List, Transformer)}.
     * 
     * @param sequence to transform
     * @param transformer transformation to be applied to each matched
     *        subsequence
     * @return transformed sequence
     */
    public List<E> replaceMatched(List<E> sequence, MatchTransformer<E> transformer) {
        List<Match<E>> matches = find(sequence);
        return replace(sequence, matches, transformer);
    }

    private List<E> replace(List<E> sequence, List<Match<E>> matches, MatchTransformer<E> transformer) {
        if (matches.isEmpty()) {
            return sequence;
        }

        List<E> result = new ArrayList<E>(sequence.size() - matches.size());
        Iterator<Match<E>> iterator = matches.iterator();

        int notMatchedFrom = 0;
        int notMatchedTo = -1;
        while (iterator.hasNext()) {
            Match<E> match = iterator.next();

            notMatchedTo = match.getMatchFromIndex();
            result.addAll(sequence.subList(notMatchedFrom, notMatchedTo));
            result.addAll(transformer.transform(match));
            notMatchedFrom = match.getMatchToIndex();
        }

        result.addAll(sequence.subList(notMatchedFrom, sequence.size()));
        return result;
    }

    @Override
    public String toString() {
        return matchers.toString();
    }

    /**
     * Creates a pattern from supplied matchers
     */
    @SafeVarargs
    public static <E> Pattern<E> create(Matcher<E>... matchers) {
        return create(Arrays.asList(matchers));
    }

    /**
     * Creates a pattern from supplied matchers
     */
    public static <E> Pattern<E> create(List<Matcher<E>> matchers) {
        return new Pattern<E>(ParentMatcher.wrapMatchers(matchers));
    }

}
