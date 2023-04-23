package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Model {
    ObjectId id;

    // This is the "snowflake id" of the user
    @NonNull String discordUserId;

    // The state the user lives in, null if user does not set state
    @Nullable String stateOfResidence;

    // The city the user lives in, null if user does not set city
    @Nullable String cityOfResidence;

    // The listing currently being worked on, null when not creating listing
    @Nullable Listing currentListing;
}
