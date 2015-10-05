package com.alexeygrigorev.rseq;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A successful match produced by matching a {@link Pattern} against some
 * (sub)sequence of objects.
 * 
 */
public class Match<E> {

    private final int index;
    private final List<E> match;
    private final Map<String, E> variables;
    private final Map<String, List<E>> groups;

    /**
     * Shouldn't be used by users of the library
     * 
     * @param index
     * @param match
     * @param variables
     */
    public Match(int index, List<E> match, Map<String, E> variables) {
        this(index, match, variables, Collections.<String, List<E>> emptyMap());
    }

    /**
     * Shouldn't be used by users of the library
     * 
     * @param index
     * @param match
     * @param variables
     * @param groups
     */
    public Match(int index, List<E> match, Map<String, E> variables, Map<String, List<E>> groups) {
        this.index = index;
        this.match = match;
        this.variables = variables;
        this.groups = groups;
    }

    /**
     * @return the starting position of matched sequence
     */
    public int matchedFrom() {
        return index;
    }

    /**
     * @return the end position of matching sequence. it points to the next
     *         element in the sequence after the match
     */
    public int matchedTo() {
        return index + match.size();
    }

    public List<E> getMatchedSubsequence() {
        return match;
    }

    public Map<String, E> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public E getVariable(String name) {
        return variables.get(name);
    }

    public List<E> getCapturedGroup(String name) {
        return groups.get(name);
    }

    public Map<String, List<E>> getCapturedGroups() {
        return Collections.unmodifiableMap(groups);
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
