package com.vip.server.repositories;

import com.vip.server.domain.AbstractId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class Repository<OBJ extends AbstractId<ID>, ID> {
    private final static Logger logger = LoggerFactory.getLogger(Repository.class);
    ConcurrentMap<ID, OBJ> storage = new ConcurrentHashMap<>();

    public OBJ save(OBJ obj) {
        storage.put(obj.getId(), obj);
        logger.debug(String.format("%s: Object saved - %s",
                this.getClass().getSimpleName() + "Object saved", obj));
        return obj;
    }

    private Type getIdType() {
        ParameterizedType pType = (ParameterizedType) this.getClass().getGenericSuperclass();
        return pType.getActualTypeArguments()[1];
    }

    public boolean existsById(ID id) {
        return storage.containsKey(id);
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

    public boolean delete(OBJ obj) {
        return (storage.containsKey(obj.getId()) && storage.remove(obj.getId(), obj));
    }
}
