package com.alexeygrigorev.rseq;

import java.util.ArrayList;
import java.util.List;

/**
 * Common parent for all internal implementations
 * 
 * @author Alexey Grigorev
 */
abstract class ParentMatcher<E> extends XMatcher<E> {

    private MatchingContext<E> context;
    protected int index = -999;

    void setContext(MatchingContext<E> context) {
        this.context = context;
    }

    int register(MatchingContext<E> context) {
        // setContext(context);
        this.index = context.register(this);
        return this.index;
    }

    void initialize(MatchingContext<E> context) {
    }

    MatchingContext<E> context() {
        return context;
    }

    /**
     * TODO:
     * 
     * Marker interface for marking matchers that can accept no input. It's
     * needed when we run into the end of sequence and need to check if all the
     * remaining matchers are optional or not.
     */
    boolean isOptional() {
        return false;
    }

    static <E> ParentMatcher<E> wrap(final Matcher<E> matcher) {
        if (matcher instanceof ParentMatcher) {
            return (ParentMatcher<E>) matcher;
        } else {
            return DelegatingMatcher.wrap(matcher);
        }
    }

    static <E> List<ParentMatcher<E>> wrapMatchers(List<Matcher<E>> matchers) {
        List<ParentMatcher<E>> list = new ArrayList<ParentMatcher<E>>(matchers.size());
        for (Matcher<E> matcher : matchers) {
            list.add(wrap(matcher));
        }
        return list;
    }

}
