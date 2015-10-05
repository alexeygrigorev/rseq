package com.alexeygrigorev.rseq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang3.Validate;

class MatchingContext<E> {

    private final Map<String, E> variables = new HashMap<String, E>();
    private final Map<String, List<E>> groups = new HashMap<String, List<E>>();
    private final List<Match<E>> results = new ArrayList<Match<E>>();

    private final List<ParentMatcher<E>> flatPattern = new ArrayList<>();
    private final List<E> sequence;

    private int index = 0;
    private int currentMatcherIndex = 0;
    private ListIterator<E> currentListIterator;

    public MatchingContext(List<E> sequence) {
        this.sequence = sequence;
    }

    public void addSuccessfulMatch() {
        Validate.validState(currentListIterator != null, "currentListIterator must be initialized already");

        int nextIndex = currentListIterator.nextIndex();
        if (nextIndex == index) {
            // empty match, Optional or ZeroOrMore matcher, ignoring it
            return;
        }

        List<E> matchedSubsequence = new ArrayList<E>(sequence.subList(index, nextIndex));
        Map<String, E> variablesCopy = new HashMap<>(variables);
        Map<String, List<E>> groupsCopy = new HashMap<>(groups);
        Match<E> match = new Match<E>(index, matchedSubsequence, variablesCopy, groupsCopy);
        results.add(match);

        variables.clear();
        groups.clear();
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

    public List<ParentMatcher<E>> getPattern() {
        return flatPattern;
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

    void captureGroup(String name) {
        int currentIndex = currentListIterator.nextIndex();
        List<E> capturedGroup = new ArrayList<E>(sequence.subList(currentMatcherIndex, currentIndex));
        groups.put(name, capturedGroup);
    }

    int register(ParentMatcher<E> matcher) {
        flatPattern.add(matcher);
        return flatPattern.size() - 1;
    }

    void nextMatcher() {
        currentMatcherIndex = currentListIterator.nextIndex();
    }

}
