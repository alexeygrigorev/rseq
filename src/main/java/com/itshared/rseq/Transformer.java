package com.itshared.rseq;

public interface Transformer<E> {

    E transform(Match<E> match);

}
