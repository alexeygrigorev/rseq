package com.itshared.rseq;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Match<E> {

    private final int index;
    private final List<E> match;
    private final Map<String, E> variables;

    public Match(int index, List<E> match, Map<String, E> variables) {
        this.index = index;
        this.match = match;
        this.variables = variables;
    }

    public int getMatchFromIndex() {
        return index;
    }

    public int getMatchToIndex() {
        return index + match.size();
    }

    public List<E> getMatchedSubsequence() {
        return match;
    }

    public Map<String, E> getVariables() {
        return variables;
    }

    public E getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public String toString() {
        return "Match [index=" + index + ", match=" + match + ", variables=" + variables + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
