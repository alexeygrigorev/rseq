package com.itshared.rseq;

public abstract class XMatcher<E> implements Matcher<E> {

    public abstract boolean match(E object);

    public XMatcher<E> captureAs(String name) {
        return new CapturingMatcher<E>(name, this);
    }

    public XMatcher<E> or(Matcher<E> other) {
        return Matchers.or(this, other);
    }

    public XMatcher<E> optional() {
        return new OptionalMatcher<E>(this);
    }

    public XMatcher<E> oneOrMoreGreedy() {
        return new OneOrMoreGreedyMatcher<E>(this);
    }

    public XMatcher<E> zeroOrMoreGreedy() {
        return new ZeroOrMoreGreedyMatcher<E>(this);
    }

    public XMatcher<E> oneOrMore() {
        return new OneOrMoreLazyMatcher<E>(this);
    }

    public XMatcher<E> zeroOrMore() {
        return new ZeroOrMoreLazyMatcher<E>(this);
    }

    @Override
    public String toString() {
        return "toString() not overriden!";
    }
}
