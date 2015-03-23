package com.itshared.rseq;

class OptionalMatcher<E> extends DelegatingMatcher<E> implements OptionalMatcherMarker {

    public OptionalMatcher(Matcher<E> matcher) {
        super(matcher);
    }

    @Override
    public boolean match(E object) {
        if (delegateMatch(object)) {
            return true;
        }
        context().getCurrentMatchIterator().previous();
        return true;
    }

    @Override
    public EnhancedMatcher<E> captureAs(String name) {
        throw new UnsupportedOperationException("Capturing optional matchers is not yet supported");
    }

    @Override
    public String toString() {
        return "[" + delegateToString() + "]?";
    }
}
