package com.itshared.rseq;

abstract class DelegatingMatcher<E> extends EnhancedMatcher<E> implements ContextAwareMatcher<E> {

    private final Matcher<E> delegate;
    private MatchingContext<E> context;

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
     * Tries to match with simplest possible matcher. Used to avoid wildcard matchers that can 
     * use the whole sequence when testing a lazy matchers 
     * 
     * @param e object to be matched with
     * @return <code>true</code> if the underlying matcher returns <code>true</code>, <code>false</code> otherwise 
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
    public void initialize(MatchingContext<E> context, int index) {
        this.context = context;
        if (delegate instanceof ContextAwareMatcher) {
            ((ContextAwareMatcher<E>) delegate).initialize(context, index);
        }
    }

    MatchingContext<E> context() {
        return context;
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
