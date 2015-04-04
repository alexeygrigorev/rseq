package com.itshared.rseq;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Matchers {

    private Matchers() {
    }

    private static final ParentMatcher<Object> ANYTHING_MATCHER = new ParentMatcher<Object>() {
        @Override
        public boolean match(Object object) {
            return true;
        }

        @Override
        public String toString() {
            return ".";
        }
    };

    public static <E> XMatcher<E> anything() {
        @SuppressWarnings("unchecked")
        ParentMatcher<E> result = (ParentMatcher<E>) ANYTHING_MATCHER;
        return result;
    }

    public static <E> XMatcher<E> eq(final E other) {
        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                return Objects.equals(object, other);
            }

            @Override
            public String toString() {
                return "== " + other;
            }
        };
    }

    public static <E> XMatcher<E> or(final Matcher<E> a, final Matcher<E> b) {
        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                return a.match(object) || b.match(object);
            }

            @Override
            public String toString() {
                return a.toString() + " or " + b.toString();
            }
        };
    }

    public static <E> XMatcher<E> and(final Matcher<E> a, final Matcher<E> b) {
        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                return a.match(object) && b.match(object);
            }

            @Override
            public String toString() {
                return a.toString() + " and " + b.toString();
            }
        };
    }

    public static <E> XMatcher<E> not(final Matcher<E> matcher) {
        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                return !matcher.match(object);
            }

            @Override
            public String toString() {
                return "not [" + matcher.toString() + "]";
            }
        };
    }

    public static <E> XMatcher<E> isNull() {
        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                return object == null;
            }

            @Override
            public String toString() {
                return "== null";
            };
        };
    }

    @SafeVarargs
    public static <E> XMatcher<E> group(Matcher<E>... matchers) {
        List<ParentMatcher<E>> list = ParentMatcher.wrapMatchers(Arrays.asList(matchers));
        return new GroupMatcher<E>(list);
    }

    public static <E> XMatcher<E> capture(Matcher<E> matcher, String name) {
        return new CapturingMatcher<E>(name, matcher);
    }

    public static <E> XMatcher<E> optional(Matcher<E> matcher) {
        return new OptionalMatcher<E>(matcher);
    }

    public static <E> XMatcher<E> oneOrMoreGreedy(Matcher<E> matcher) {
        return new OneOrMoreGreedyMatcher<E>(matcher);
    }

    public static <E> XMatcher<E> zeroOrMoreGreedy(Matcher<E> matcher) {
        return new ZeroOrMoreGreedyMatcher<E>(matcher);
    }

    public static <E> XMatcher<E> oneOrMore(Matcher<E> matcher) {
        return new OneOrMoreLazyMatcher<E>(matcher);
    }

    public static <E> XMatcher<E> zeroOrMore(Matcher<E> matcher) {
        return new ZeroOrMoreLazyMatcher<E>(matcher);
    }

}
