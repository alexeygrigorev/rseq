package com.alexeygrigorev.rseq;

/**
 * Basic interface for testing if an object satisfies user-defined criteria. Some 
 * pre-defined matchers can be found in {@link Matchers} and in {@link XMatcher} 
 */
public interface Matcher<E> {

    /**
     * Must return <code>true</code> if the object satisfies some user-defined
     * criteria
     * 
     * @param object to test
     * @return <code>true</code> if the match is successful, <code>false</code>
     *         otherwise
     */
    boolean match(E object);

}
