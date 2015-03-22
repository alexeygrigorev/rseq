package com.itshared.rseq;

public abstract class EnhancedMatcher<E> implements Matcher<E> {

    protected MatchingContext<E> context;

    public abstract boolean match(E e);

    public void initialize(MatchingContext<E> context) {
        this.context = context;
    }

    public CapturingMatcher<E> captureAs(String name) {
        return new CapturingMatcher<E>(name, this);
    }

    public EnhancedMatcher<E> or(Matcher<E> other) {
        return Matchers.or(this, other);
    }

    @Override
    public String toString() {
        return "AbstractMatcher";
    }
}
