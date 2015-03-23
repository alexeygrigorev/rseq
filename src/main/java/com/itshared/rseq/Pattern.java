package com.itshared.rseq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Pattern<E> {

    private final List<EnhancedMatcher<E>> matchers;

    private Pattern(List<EnhancedMatcher<E>> matchers) {
        this.matchers = matchers;
    }

    public List<Match<E>> find(List<E> sequence) {
        MatchingContext<E> context = new MatchingContext<E>(matchers, sequence);
        for (EnhancedMatcher<E> matcher : matchers) {
            matcher.initialize(context);
        }

        // TODO: no need for iterator
        Iterator<Void> iterator = context.findIterator();
        while (iterator.hasNext()) {
            Iterator<E> matchIterator = context.matchIterator();
            boolean success = true;

            for (EnhancedMatcher<E> matcher : matchers) {
                if (!matchIterator.hasNext()) {
                    if (matcher instanceof OptionalMatcherMarker) {
                        continue;
                    }
                    success = false;
                    break;
                }
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

    public List<E> replace(List<E> sequence, MatchTransformer<E> transformer) {
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

        result.addAll(sequence.subList(notMatchedTo, sequence.size()));
        return result;
    }

    @SafeVarargs
    public static <E> Pattern<E> create(Matcher<E>... matchers) {
        return create(Arrays.asList(matchers));
    }

    public static <E> Pattern<E> create(List<Matcher<E>> matchers) {
        List<EnhancedMatcher<E>> list = new ArrayList<EnhancedMatcher<E>>();
        for (Matcher<E> matcher : matchers) {
            list.add(WrappingMatcher.wrap(matcher));
        }
        return new Pattern<E>(list);
    }

}
