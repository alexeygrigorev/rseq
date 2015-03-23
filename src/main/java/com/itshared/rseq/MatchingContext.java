package com.itshared.rseq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

class MatchingContext<E> {

    private final Map<String, E> variables = new HashMap<String, E>();
    private final List<Match<E>> results = new ArrayList<Match<E>>();

    private final List<Matcher<E>> pattern;
    private final List<E> sequence;

    private int index = 0;
    private ListIterator<E> currentListIterator;

    public MatchingContext(List<Matcher<E>> pattern, List<E> sequence) {
        this.pattern = pattern;
        this.sequence = sequence;
    }

    public void addSuccessfulMatch() {
        if (currentListIterator == null) {
            throw new IllegalStateException("currentListIterator must be initialized already");
        }

        int nextIndex = currentListIterator.nextIndex();
        List<E> matchedSubsequence = new ArrayList<E>(sequence.subList(index, nextIndex));
        Match<E> match = new Match<E>(index, matchedSubsequence, new HashMap<String, E>(variables));
        results.add(match);
        variables.clear();
        index = nextIndex - 1;
    }

    public void setVariable(String name, E value) {
        variables.put(name, value);
    }

    public E getVariable(String name) {
        return variables.get(name);
    }

    public List<Match<E>> getAllResults() {
        return results;
    }

    public List<Matcher<E>> getPattern() {
        return pattern;
    }

    public Iterator<Void> findIterator() {
        return new Iterator<Void>() {
            @Override
            public boolean hasNext() {
                return index < sequence.size();
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
        currentListIterator = sequence.listIterator(index);
        return currentListIterator;
    }

    ListIterator<E> getCurrentMatchIterator() {
        return currentListIterator;
    }

}
