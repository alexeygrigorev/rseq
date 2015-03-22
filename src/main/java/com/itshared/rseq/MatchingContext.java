package com.itshared.rseq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MatchingContext<E> {

    private final Map<String, E> variables = new HashMap<String, E>();
    private final List<Match<E>> results = new ArrayList<Match<E>>();

    private final List<EnhancedMatcher<E>> pattern;
    private final List<E> sequence;

    private int index = 0;

    public MatchingContext(List<EnhancedMatcher<E>> pattern, List<E> sequence) {
        this.pattern = pattern;
        this.sequence = sequence;
    }

    public void addSuccessfulMatch() {
        Match<E> match = new Match<E>(index, new HashMap<String, E>(variables));
        results.add(match);
        index = index + pattern.size() - 1;
    }

    public List<Match<E>> getAllResults() {
        return results;
    }

    public void setVariable(String name, E value) {
        variables.put(name, value);
    }

    public E getVariable(String name) {
        return variables.get(name);
    }

    public Iterator<Void> findIterator() {
        return new Iterator<Void>() {
            @Override
            public boolean hasNext() {
                return index < sequence.size() - pattern.size() + 1;
            }

            @Override
            public Void next() {
                index++;
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() is not supported");
            }
        };
    }

    public Iterator<E> matchIterator() {
        return sequence.listIterator(index);
    }

}
