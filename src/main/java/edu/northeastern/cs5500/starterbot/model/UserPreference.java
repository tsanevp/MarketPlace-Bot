package edu.northeastern.cs5500.starterbot.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UserPreference implements Model {
    ObjectId id;

    // This is the "snowflake id" of the user
    // e.g. event.getUser().getId()
    String discordUserId;

    // The user wants to be referred to by this name
    String preferredName;
}
