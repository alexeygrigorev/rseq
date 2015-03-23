package com.itshared.rseq;

import java.util.Collections;
import java.util.List;

public abstract class OneElementOutputTransformer<E> implements MatchTransformer<E> {

    public abstract E convert(Match<E> match);

    @Override
    public List<E> transform(Match<E> match) {
        return Collections.singletonList(convert(match));
    }

}
