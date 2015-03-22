package com.itshared.rseq;

public class WrappingMatcher<E> extends EnhancedMatcher<E> {

    public static <E> EnhancedMatcher<E> wrap(Matcher<E> matcher) {
        if (matcher instanceof EnhancedMatcher) {
            return (EnhancedMatcher<E>) matcher;
        } else {
            return new WrappingMatcher<E>(matcher);
        }
    }

    private Matcher<E> matcher;

    public WrappingMatcher(Matcher<E> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(E e) {
        return matcher.match(e);
    }

    @Override
    public String toString() {
        return matcher.toString();
    }

}
