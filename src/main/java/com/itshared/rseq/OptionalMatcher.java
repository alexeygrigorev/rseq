package com.itshared.rseq;

class OptionalMatcher<E> extends DelegatingMatcher<E> {

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
    public ParentMatcher<E> captureAs(String name) {
        throw new UnsupportedOperationException("Capturing optional matchers is not yet supported");
    }

    @Override
    boolean isOptional() {
        return true;
    }

    @Override
    public String toString() {
        return "[" + delegateToString() + "]?";
    }
}
