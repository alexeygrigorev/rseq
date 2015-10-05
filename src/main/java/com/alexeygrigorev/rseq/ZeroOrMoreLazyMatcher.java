package com.alexeygrigorev.rseq;

import java.util.List;
import java.util.ListIterator;

class ZeroOrMoreLazyMatcher<E> extends ZeroOrMoreGreedyMatcher<E> {

    private DelegatingMatcher<E> nextMatcher;

    public ZeroOrMoreLazyMatcher(Matcher<E> matcher) {
        super(matcher);
    }

    @Override
    void initialize(MatchingContext<E> context) {
        this.nextMatcher = null;

        List<ParentMatcher<E>> pattern = context.getPattern();
        if (index + 1 < pattern.size()) {
            Matcher<E> nextMatcher = pattern.get(index + 1);
            this.nextMatcher = DelegatingMatcher.wrap(nextMatcher);
        }
    }

    @Override
    public boolean match(E object) {
        if (nextMatcher == null) {
            return super.match(object);
        }

        ListIterator<E> currentIterator = context().getCurrentMatchIterator();

        if (!delegateMatch(object)) {
            currentIterator.previous();
            return true;
        }

        while (currentIterator.hasNext()) {
            E next = currentIterator.next();

            boolean currentMatch = delegateMatch(next);
            boolean nextMatch = nextMatcher.unwrappingMatch(next);

            if (!currentMatch) {
                currentIterator.previous();
                break;
            }

            if (currentMatch && nextMatch) {
                currentIterator.previous();
                break;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (nextMatcher == null) {
            return super.toString();
        }

        return "[" + delegateToString() + "]*?";
    }

}
