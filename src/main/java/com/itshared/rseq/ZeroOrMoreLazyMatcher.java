package com.itshared.rseq;

import java.util.ListIterator;

class ZeroOrMoreLazyMatcher<E> extends EnhancedMatcher<E> implements OptionalMatcherMarker {

    private final Matcher<E> matcher;

    public ZeroOrMoreLazyMatcher(Matcher<E> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(E e) {
        ListIterator<E> currentIterator = context.getCurrentMatchIterator();
        if (matcher.match(e)) {
            while (currentIterator.hasNext()) {
                E next = currentIterator.next();
                if (!matcher.match(next)) {
                    currentIterator.previous();
                    break;
                }
            }
            return true;
        }
        currentIterator.previous();
        return true;
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
        return "[" + matcher.toString() + "]*?";
    }

}
