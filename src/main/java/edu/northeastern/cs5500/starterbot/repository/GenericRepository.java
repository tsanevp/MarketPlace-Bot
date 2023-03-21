package edu.northeastern.cs5500.starterbot.repository;

import java.util.Collection;
import javax.annotation.Nonnull;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;

public interface GenericRepository<T> {
    public T get(@Nonnull ObjectId id);

    public T add(@Nonnull T item);

    public T update(@Nonnull T item);

    public void delete(@Nonnull ObjectId id);

    public Collection<T> getAll();

    public long count();

    public FindIterable<T> filter(Bson filter);
}
