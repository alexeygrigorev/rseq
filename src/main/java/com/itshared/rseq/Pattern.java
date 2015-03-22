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
