package org.example.trade.adapter.translator;

public interface Translator<T, R> {

    T from(R r);

    R to(T t);

}
