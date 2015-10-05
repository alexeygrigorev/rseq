package com.alexeygrigorev.rseq;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A utility class that contains basic implementation of the {@link Matcher}
 * interface (e.g. {@link #eq(Object)}, {@link #or(Matcher, Matcher)} or
 * {@link #and(Matcher, Matcher)}).
 * 
 * <br><br>
 * For readability it might be better to <code>import static</code> the methods
 * of this class.
 * 
 * @see XMatcher
 */
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

    /**
     * Produces a matcher that matches anything
     * 
     * @return the "." matcher
     */
    public static <E> XMatcher<E> anything() {
        @SuppressWarnings("unchecked")
        ParentMatcher<E> result = (ParentMatcher<E>) ANYTHING_MATCHER;
        return result;
    }

    /**
     * @param other object to test for equality
     * @return returns a matcher that matches the given <code>other</code>
     *         object using the equality test
     */
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

    /**
     * Combines two matchers by using the OR test: i.e. it produces a matcher
     * that matches an object successfully if at least one of the matcher is
     * successful
     * 
     * @param a first matcher
     * @param b second matcher
     * @return a matcher that combines two matchers by using the OR test
     */
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

    /**
     * Combines two matchers by using the AND test: i.e. it produces a matcher
     * that matches an object successfully if both the matchers are successful
     * 
     * @param a first matcher
     * @param b second matcher
     * @return a matcher that combines two matchers by using the AND test
     */
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

    /**
     * Produces a matcher that inverts the result of the provided matcher
     * 
     * @param matcher the matcher to invert
     * @return NOT matcher
     */
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

    /**
     * Produces a matcher that returns true when an object to test is null
     * 
     * @return isNULL matcher
     */
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

    /**
     * Produces a matcher that checks if the passed object is contained in the
     * collection
     * 
     * @param col with elements to be tested
     * @return the IN matcher
     */
    public static <E> XMatcher<E> in(final Collection<E> col) {
        return new ParentMatcher<E>() {
            @Override
            public boolean match(E object) {
                return col.contains(object);
            }

            @Override
            public String toString() {
                return "in " + col;
            }
        };
    }

    /**
     * Produces a matcher that checks if the passed object is contained in the
     * array
     * 
     * @param col the elements to be tested with
     * @return the IN matcher
     */
    @SafeVarargs
    public static <E> XMatcher<E> in(E... col) {
        return in(Arrays.asList(col));
    }

    /**
     * Groups matchers into a subsequence
     * 
     * @param matchers to be grouped
     * @return matcher that matches against a group of elements
     */
    @SafeVarargs
    public static <E> XMatcher<E> group(Matcher<E>... matchers) {
        List<ParentMatcher<E>> list = ParentMatcher.wrapMatchers(Arrays.asList(matchers));
        return new GroupMatcher<E>(list);
    }

    /**
     * Produces a capturing matcher, i.e. a matcher that can remember the object
     * it matched against
     * 
     * @param matcher the basic matcher
     * @param name the variable name associated with the matcher
     * @return a capturing matcher
     */
    public static <E> XMatcher<E> capture(Matcher<E> matcher, String name) {
        return new CapturingMatcher<E>(name, matcher);
    }

    /**
     * Produces an optional matcher - a matcher that may or may not match the
     * object in the sequence
     * 
     * @param matcher that may be optional
     * @return the "?" matcher
     */
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
