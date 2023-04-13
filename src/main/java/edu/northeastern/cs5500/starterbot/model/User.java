package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.DBObject;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class User implements Model {
    ObjectId id;

    // This is the "snowflake id" of the user
    String discordUserId;

    // This is the ID of the guild the user is a part of
    String guildId;

    // This is the Id of the channel used for trading
    String tradingChannelId;

    // The state the user lives in
    String stateOfResidence;

    // The city the user lives in
    String cityOfResidence;

    // This is the current listing in string format
    String currentListingAsString;

    // This is the current listing in JSON string format
    DBObject currentListing;
}
