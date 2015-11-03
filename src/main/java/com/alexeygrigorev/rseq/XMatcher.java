package com.alexeygrigorev.rseq;

import java.util.List;

/**
 * A class that implements {@link Matcher} and adds some extra methods for
 * convenience (like {@link #or(Matcher)} or {@link #optional()})
 */
public abstract class XMatcher<E> implements Matcher<E> {

    public abstract boolean match(E object);

    public XMatcher<E> captureAs(String name) {
        return Matchers.capture(this, name);
    }

    public XMatcher<E> or(Matcher<E> other) {
        return Matchers.or(this, other);
    }

    public XMatcher<E> optional() {
        return Matchers.optional(this);
    }

    public XMatcher<E> oneOrMoreGreedy() {
        return Matchers.oneOrMoreGreedy(this);
    }

    public XMatcher<E> zeroOrMoreGreedy() {
        return Matchers.zeroOrMoreGreedy(this);
    }

    public XMatcher<E> oneOrMore() {
        return Matchers.oneOrMore(this);
    }

    public XMatcher<E> zeroOrMore() {
        return Matchers.zeroOrMore(this);
    }

    public XMatcher<E> invert() {
        return Matchers.not(this);
    }

    public boolean all(List<E> sequence) {
        return Quantifiers.all(this, sequence);
    }

    public boolean any(List<E> sequence) {
        return Quantifiers.any(this, sequence);
    }

    public boolean none(List<E> sequence) {
        return Quantifiers.none(this, sequence);
    }

    @Override
    public String toString() {
        return "toString() not overriden!";
    }
}
