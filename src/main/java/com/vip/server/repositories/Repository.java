package com.vip.server.repositories;

import com.vip.server.domain.AbstractEntityWithId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class Repository<OBJ extends AbstractEntityWithId<ID>, ID> {
    ConcurrentMap<ID, OBJ> storage = new ConcurrentHashMap<>();

    abstract ID getNextId();

    public OBJ save(OBJ obj) {
        if (obj.isNew()) {
            obj.setId(getNextId());
        }
        storage.put(obj.getId(), obj);
        return obj;
    }

    public Optional<OBJ> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<OBJ> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(OBJ obj) {
        if (storage.containsKey(obj.getId())) {
            storage.remove(obj.getId(), obj);
        }
    }
}
