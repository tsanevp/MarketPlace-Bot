package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.NonNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/*
 * Represents the model of the guild that is stored in MongoDB
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guild implements Model {
    ObjectId id;

    // The Guild id of the Guild
    @NonNull String guildId;

    // The id of the guild owner
    @NonNull String guildOwnerId;

    // The id of the trading channel in the Guild
    @NonNull String tradingChannelId;

    // A set of users ids in the Guild
    @NonNull Set<String> usersOnServer;
}
