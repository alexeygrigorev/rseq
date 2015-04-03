package com.itshared.rseq;

class CapturingMatcher<E> extends DelegatingMatcher<E> {

    private String name;

    public CapturingMatcher(String name, Matcher<E> matcher) {
        super(matcher);
        this.name = name;
    }

    @Override
    public boolean match(E object) {
        if (delegateMatch(object)) {
            context().setVariable(name, object);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + "={" + delegateToString() + "}";
    }

}
