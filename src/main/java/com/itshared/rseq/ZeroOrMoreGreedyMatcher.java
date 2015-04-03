package com.itshared.rseq;

import java.util.ListIterator;

class ZeroOrMoreGreedyMatcher<E> extends DelegatingMatcher<E> {

    public ZeroOrMoreGreedyMatcher(Matcher<E> matcher) {
        super(matcher);
    }

    @Override
    public boolean match(E object) {
        ListIterator<E> currentIterator = context().getCurrentMatchIterator();
        if (!delegateMatch(object)) {
            currentIterator.previous();
            return true;
        }

        while (currentIterator.hasNext()) {
            E next = currentIterator.next();
            if (!delegateMatch(next)) {
                currentIterator.previous();
                break;
            }
        }

        return true;
    }

    @Override
    public ParentMatcher<E> captureAs(String name) {
        return new CapturingGroupMatcher<E>(name, this);
    }

    @Override
    boolean isOptional() {
        return true;
    }

    @Override
    public String toString() {
        return "[" + delegateToString() + "]*";
    }

}
