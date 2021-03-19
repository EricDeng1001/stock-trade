package org.example.trade.domain;

import java.util.Map;

public abstract class MockRepo<T, R> {

    Map<T, R> map;

    public R findById(T id) {
        return map.get(id);
    }

    public void save(R r) {
        map.put(getId(r), r);
    }

    protected abstract T getId(R r);

}
