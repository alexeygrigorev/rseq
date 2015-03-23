package com.itshared.rseq;

import java.util.ListIterator;

class OneOrMoreGreedyMatcher<E> extends DelegatingMatcher<E> {

    public OneOrMoreGreedyMatcher(Matcher<E> matcher) {
        super(matcher);
    }

    @Override
    public boolean match(E object) {
        if (delegateMatch(object)) {
            ListIterator<E> currentIterator = context().getCurrentMatchIterator();
            while (currentIterator.hasNext()) {
                E next = currentIterator.next();
                if (!delegateMatch(next)) {
                    currentIterator.previous();
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public EnhancedMatcher<E> captureAs(String name) {
        throw new UnsupportedOperationException("Capturing wildcard matchers is not yet supported");
    }

    @Override
    public String toString() {
        return "[" + delegateToString() + "]+";
    }
}
