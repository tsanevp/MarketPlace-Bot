package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Guild implements Model {
    ObjectId id;

    // The Guild id of the Guild
    @NonNull String guildId;

    // The id of the trading channel in the Guild
    @Nullable String tradingChannelId;

    // A list of users ids in the Guild
    @NonNull List<String> usersOnServer;
}
