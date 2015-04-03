package com.itshared.rseq;

class CapturingGroupMatcher<E> extends DelegatingMatcher<E> {

    private final String name;
    private final ParentMatcher<E> delegate;

    public CapturingGroupMatcher(String name, ParentMatcher<E> matcher) {
        super(matcher);
        this.name = name;
        this.delegate = matcher;
    }

    @Override
    public boolean match(E object) {
        if (delegateMatch(object)) {
            context().captureGroup(name);
            return true;
        }
        return false;
    }

    @Override
    int register(MatchingContext<E> context) {
        this.index = delegate.register(context);
        return index;
    }

    @Override
    public String toString() {
        return name + "={" + delegateToString() + "}";
    }

}
