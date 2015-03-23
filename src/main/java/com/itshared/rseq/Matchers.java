package com.itshared.rseq;

import java.util.Objects;

public class Matchers {

    private Matchers() {
    }

    public static <E> EnhancedMatcher<E> anything() {
        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                return true;
            }

            @Override
            public String toString() {
                return ".";
            }
        };
    }

    public static <E> EnhancedMatcher<E> eq(final E other) {
        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                return Objects.equals(e, other);
            }

            @Override
            public String toString() {
                return "== " + other;
            }
        };
    }

    public static <E> EnhancedMatcher<E> or(final Matcher<E> a, final Matcher<E> b) {
        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                return a.match(e) || b.match(e);
            }

            @Override
            public String toString() {
                return a.toString() + " or " + b.toString();
            }
        };
    }

    public static <E> EnhancedMatcher<E> and(final Matcher<E> a, final Matcher<E> b) {
        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                return a.match(e) && b.match(e);
            }

            @Override
            public String toString() {
                return a.toString() + " and " + b.toString();
            }
        };
    }

    public static <E> EnhancedMatcher<E> not(final Matcher<E> matcher) {
        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                return !matcher.match(e);
            }

            @Override
            public String toString() {
                return "not (" + matcher.toString() + ")";
            }
        };
    }

    public static <E> EnhancedMatcher<E> isNull() {
        return new EnhancedMatcher<E>() {
            @Override
            public boolean match(E e) {
                return e == null;
            }

            @Override
            public String toString() {
                return "== null";
            };
        };
    }
}
