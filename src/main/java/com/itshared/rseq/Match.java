package com.itshared.rseq;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Match<E> {

    private int index;
    private Map<String, E> variables;

    public Match(int index, Map<String, E> variables) {
        this.index = index;
        this.variables = variables;
    }

    public int getIndex() {
        return index;
    }

    public Map<String, E> getVariables() {
        return variables;
    }

    public E getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public String toString() {
        return "Match [index=" + index + ", variables=" + variables + "]";
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
