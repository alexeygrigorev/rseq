package com.alexeygrigorev.rseq;

public interface TransformerToElement<E> {

    E transform(Match<E> match);

}
