package com.itshared.rseq;

public interface ContextAwareMatcher<E> extends Matcher<E> {

    /**
     * 
     * @param context
     * @param index position in the containing pattern
     */
    void initialize(MatchingContext<E> context, int index);

}
