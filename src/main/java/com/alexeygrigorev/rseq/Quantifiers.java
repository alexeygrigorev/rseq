package com.alexeygrigorev.rseq;

import java.util.List;

/**
 * A class with quick tests for checking if a matcher applies to all the elements, 
 * some or none
 */
public class Quantifiers {

    /**
     * The "all" quantifier which returns <code>true</code> if 
     * all elements of the sequence are matched
     * 
     * @param matcher to use
     * @param seq to match
     * @return <code>true</code> if all elements match
     */
    public static <E> boolean all(Matcher<E> matcher, List<E> seq) {
        for (E e : seq) {
            if (!matcher.match(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * The "exists" quantifier which returns <code>true</code> if 
     * at least one element of the sequence is matcher
     * 
     * @param matcher to use
     * @param seq to match
     * @return <code>true</code> if at least one element matches
     */
    public static <E> boolean any(Matcher<E> matcher, List<E> seq) {
        for (E e : seq) {
            if (matcher.match(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The "does not exist" quantifier, which returns <code>true</code> 
     * if no elements of the sequence are matched 
     * 
     * @param matcher to use
     * @param seq to match
     * @return <code>true</code> if no match happened
     */
    public static <E> boolean none(Matcher<E> matcher, List<E> seq) {
        return !any(matcher, seq);
    }


}
