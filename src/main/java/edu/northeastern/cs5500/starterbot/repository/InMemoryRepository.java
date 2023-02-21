package edu.northeastern.cs5500.starterbot.repository;

import edu.northeastern.cs5500.starterbot.model.Model;
import java.util.Collection;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class InMemoryRepository<T extends Model> implements GenericRepository<T> {

    HashMap<ObjectId, T> collection;

    @Inject
    public InMemoryRepository() {
        collection = new HashMap<>();
    }

    @Nullable
    public T get(@Nonnull ObjectId id) {
        return collection.get(id);
    }

    @Override
    public T add(@Nonnull T item) {
        ObjectId id = item.getId();
        if (id == null) {
            id = new ObjectId();
            item.setId(id);
        }
        collection.put(id, item);
        return item;
    }

    @Override
    public T update(@Nonnull T item) {
        collection.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(@Nonnull ObjectId id) {
        collection.remove(id);
    }

    @Override
    public Collection<T> getAll() {
        return collection.values();
    }

    @Override
    public long count() {
        return collection.size();
    }
}
