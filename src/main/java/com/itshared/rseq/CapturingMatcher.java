package com.itshared.rseq;

public class CapturingMatcher<E> extends EnhancedMatcher<E> {

    private String name;
    private Matcher<E> matcher;

    public CapturingMatcher(String name, Matcher<E> matcher) {
        this.name = name;
        this.matcher = matcher;
    }

    @Override
    public boolean match(E e) {
        if (matcher.match(e)) {
            context.setVariable(name, e);
            return true;
        }
        return false;
    }

    @Override
    public void initialize(MatchingContext<E> context) {
        super.initialize(context);
        if (matcher instanceof EnhancedMatcher) {
            ((EnhancedMatcher<E>) matcher).initialize(context);
        }
    }

    @Override
    public String toString() {
        return name + "=[" + matcher + "]";
    }

}
