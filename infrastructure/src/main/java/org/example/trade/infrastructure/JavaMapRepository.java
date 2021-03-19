package org.example.trade.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JavaMapRepository<T, R> {

    private final Map<T, R> map = new ConcurrentHashMap<>();

    public R findById(T id) {
        return map.get(id);
    }

    public void save(R r) {
        map.put(getId(r), r);
    }

    protected abstract T getId(R r);

}
