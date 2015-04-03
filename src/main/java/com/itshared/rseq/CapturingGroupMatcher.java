package com.itshared.rseq;

class CapturingGroupMatcher<E> extends DelegatingMatcher<E> {

    private String name;

    public CapturingGroupMatcher(String name, Matcher<E> matcher) {
        super(matcher);
        this.name = name;
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
    public String toString() {
        return name + "=[" + delegateToString() + "]";
    }

}
