package com.itshared.rseq;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Word {

    private String token;
    private String pos;

    public Word(String token, String pos) {
        this.token = token;
        this.pos = pos;
    }

    public String getToken() {
        return token;
    }

    public String getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return token + "/" + pos;
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
