package com.itshared.rseq;

import java.util.ListIterator;

class OneOrMoreLazyMatcher<E> extends EnhancedMatcher<E> {

    private final Matcher<E> matcher;

    public OneOrMoreLazyMatcher(Matcher<E> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(E e) {
        if (matcher.match(e)) {
            ListIterator<E> currentIterator = context.getCurrentMatchIterator();
            while (currentIterator.hasNext()) {
                E next = currentIterator.next();
                if (!matcher.match(next)) {
                    currentIterator.previous();
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void initialize(MatchingContext<E> context) {
        super.initialize(context);
        if (matcher instanceof EnhancedMatcher) {
            ((EnhancedMatcher<E>) matcher).initialize(context);
        }
    }

    @Override
    public EnhancedMatcher<E> captureAs(String name) {
        throw new UnsupportedOperationException("Capturing wildcard matchers is not yet supported");
    }

    @Override
    public String toString() {
        return "[" + matcher.toString() + "]+?";
    }
}
