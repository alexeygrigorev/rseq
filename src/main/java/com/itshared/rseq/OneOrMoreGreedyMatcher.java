package com.itshared.rseq;

import java.util.ListIterator;

class OneOrMoreGreedyMatcher<E> extends DelegatingMatcher<E> {

    public OneOrMoreGreedyMatcher(Matcher<E> matcher) {
        super(matcher);
    }

    @Override
    public boolean match(E object) {
        if (!delegateMatch(object)) {
            return false;
        }

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

    @Override
    public ParentMatcher<E> captureAs(String name) {
        return new CapturingGroupMatcher<E>(name, this);
    }

    @Override
    public String toString() {
        return "[" + delegateToString() + "]+";
    }
}
