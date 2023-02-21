package edu.northeastern.cs5500.starterbot.model;

import org.bson.types.ObjectId;

public interface Model {
    ObjectId getId();

    void setId(ObjectId id);
}
