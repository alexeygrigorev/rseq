package com.alexeygrigorev.rseq;

import java.util.List;

public interface TransformerToList<E> {

    List<E> transform(Match<E> match);

}
