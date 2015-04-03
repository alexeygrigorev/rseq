package com.itshared.rseq;

abstract class DelegatingMatcher<E> extends ParentMatcher<E> {

    private final Matcher<E> delegate;

    public DelegatingMatcher(Matcher<E> delegate) {
        this.delegate = delegate;
    }

    public abstract boolean match(E object);

    public boolean delegateMatch(E e) {
        return delegate.match(e);
    }

    public String delegateToString() {
        return delegate.toString();
    }

    /**
     * Tries to match with simplest possible matcher. Used to avoid wildcard
     * matchers that can use the whole sequence when testing a lazy matchers
     * 
     * @param e object to be matched with
     * @return <code>true</code> if the underlying matcher returns
     *         <code>true</code>, <code>false</code> otherwise
     * 
     * @see ZeroOrMoreLazyMatcher
     * @see OneOrMoreLazyMatcher
     */
    boolean unwrappingMatch(E e) {
        if (delegate instanceof DelegatingMatcher) {
            return ((DelegatingMatcher<E>) delegate).unwrappingMatch(e);
        } else {
            return delegate.match(e);
        }
    }

    @Override
    public void setContext(MatchingContext<E> context) {
        super.setContext(context);
        if (delegate instanceof ParentMatcher) {
            ((ParentMatcher<E>) delegate).setContext(context);
        }
    }
/*
    @Override
    void register(MatchingContext<E> context) {
        if (delegate instanceof CMatcher) {
            ((CMatcher<E>) delegate).register(context);
        } else {
            super.register(context);
        }
    }
*/
    @Override
    void initialize(MatchingContext<E> context) {
        if (delegate instanceof ParentMatcher) {
            ((ParentMatcher<E>) delegate).initialize(context);
        }
    }

    static <E> DelegatingMatcher<E> wrap(Matcher<E> matcher) {
        return new DelegatingMatcher<E>(matcher) {
            @Override
            public boolean match(E object) {
                return delegateMatch(object);
            }

            @Override
            public String toString() {
                return delegateToString();
            }
        };
    }
}
