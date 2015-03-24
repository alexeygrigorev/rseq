package com.itshared.rseq;

import java.util.List;

public interface Transformer<E> {

    E transform(List<E> list);

}
