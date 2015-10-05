package com.alexeygrigorev.rseq;

import java.util.List;

public interface MatchTransformer<E> {

    List<E> transform(Match<E> match);

}
