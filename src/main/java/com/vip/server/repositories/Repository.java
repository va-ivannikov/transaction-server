package com.vip.server.repositories;

import com.vip.server.domain.AbstractEntityWithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class Repository<OBJ extends AbstractEntityWithId<ID>, ID> {
    private final static Logger logger = LoggerFactory.getLogger(Repository.class);
    ConcurrentMap<ID, OBJ> storage = new ConcurrentHashMap<>();

    abstract ID getNextId();

    public OBJ save(OBJ obj) {
        if (obj.isNew()) {
            obj.setId(getNextId());
        }
        storage.put(obj.getId(), obj);
        logger.debug(String.format("%s: Object saved - %s",
                this.getClass().getSimpleName() + "Object saved", obj));
        return obj;
    }

    public Optional<OBJ> findById(ID id) {
        return storage.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .findFirst()
                .map(Map.Entry::getValue);
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
