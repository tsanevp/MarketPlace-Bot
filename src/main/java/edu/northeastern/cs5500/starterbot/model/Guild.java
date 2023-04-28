package edu.northeastern.cs5500.starterbot.model;

import java.util.Set;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/*
 * Represents the model of the guild that is stored in MongoDB.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guild implements Model {

    // MongoDB id
    ObjectId id;

    // The Guild id of the Guild
    @Nonnull String guildId;

    // The id of the guild owner
    @Nonnull String guildOwnerId;

    // The id of the trading channel in the Guild
    @Nonnull String tradingChannelId;

    // A set of users ids in the Guild
    @Nonnull Set<String> usersOnServer;
}
