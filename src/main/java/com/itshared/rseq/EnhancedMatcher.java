package com.itshared.rseq;

public abstract class EnhancedMatcher<E> implements Matcher<E> {

    protected MatchingContext<E> context;

    public abstract boolean match(E e);

    public void initialize(MatchingContext<E> context) {
        this.context = context;
    }

    public EnhancedMatcher<E> captureAs(String name) {
        return new CapturingMatcher<E>(name, this);
    }

    public EnhancedMatcher<E> or(Matcher<E> other) {
        return Matchers.or(this, other);
    }

    public EnhancedMatcher<E> optional() {
        return new OptionalMatcher<E>(this);
    }

    public EnhancedMatcher<E> oneOrMoreGreedy() {
        return new OneOrMoreGreedyMatcher<E>(this);
    }

    public EnhancedMatcher<E> zeroOrMoreGreedy() {
        return new ZeroOrMoreGreedyMatcher<E>(this);
    }

    public EnhancedMatcher<E> oneOrMore() {
        return new OneOrMoreLazyMatcher<E>(this);
    }

    public EnhancedMatcher<E> zeroOrMore() {
        return new ZeroOrMoreLazyMatcher<E>(this);
    }

    @Override
    public String toString() {
        return "toString() not overriden!";
    }
}
