package com.itshared.rseq;

class OptionalMatcher<E> extends EnhancedMatcher<E> implements OptionalMatcherMarker {

    private Matcher<E> matcher;

    public OptionalMatcher(Matcher<E> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean match(E e) {
        if (matcher.match(e)) {
            return true;
        }
        context.getCurrentMatchIterator().previous();
        return true;
    }

    @Override
    public void initialize(MatchingContext<E> context) {
        super.initialize(context);
        if (matcher instanceof EnhancedMatcher) {
            ((EnhancedMatcher<E>) matcher).initialize(context);
        }
    }

    @Override
    public EnhancedMatcher<E> captureAs(String name) {
        throw new UnsupportedOperationException("Capturing optional matchers is not yet supported");
    }

    @Override
    public String toString() {
        return "[" + matcher.toString() + "]?";
    }
}
