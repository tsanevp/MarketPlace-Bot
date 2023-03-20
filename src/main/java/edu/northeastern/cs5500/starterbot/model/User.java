package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.DBObject;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class User implements Model {
    ObjectId id;

    // This is the "snowflake id" of the user
    // e.g. event.getUser().getId()
    String discordUserId;

    // This is the ID of the guild the user is a part of
    String guildId;

    // The location the user lives at
    String locationOfResidence;

    // This is the current listing in string format
    String currentListingAsString;

    // This is the current listing in JSON string format
    DBObject currentListing;
}
