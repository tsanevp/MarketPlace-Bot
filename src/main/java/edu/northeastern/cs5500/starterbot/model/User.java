package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.Nullable;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/*
 * Represents the model of the user that is stored in MongoDB.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Model {

    // MongoDB id
    ObjectId id;

    // This is the "snowflake id" of the user
    @Nonnull String discordUserId;

    // The state the user lives in, null if user does not set state
    @Nullable String stateOfResidence;

    // The city the user lives in, null if user does not set city
    @Nullable String cityOfResidence;

    // The listing currently being worked on, null when not creating listing
    @Nullable Listing currentListing;
}
