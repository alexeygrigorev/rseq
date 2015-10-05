package com.alexeygrigorev.rseq;

public interface Transformer<E> {

    E transform(Match<E> match);

}
