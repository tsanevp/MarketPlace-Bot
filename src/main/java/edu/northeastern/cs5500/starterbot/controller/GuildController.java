package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.NonNull;
import edu.northeastern.cs5500.starterbot.model.Guild;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GuildController {
    GenericRepository<Guild> guildRepository;

    @Inject
    GuildController(GenericRepository<Guild> guildRepository) {
        this.guildRepository = guildRepository;
    }

    /**
     * Sets the trading channel id for the current guild.
     *
     * @param guildId - The id of the guild to set the trading channel id for.
     * @param tradingChannelId - The trading channel id to set for the guild.
     */
    public void setTradingChannelId(@NonNull String guildId, @NonNull String tradingChannelId) {
        Guild guild = getGuildForId(guildId);
        guild.setTradingChannelId(tradingChannelId);
        guildRepository.update(guild);
    }

    /**
     * Adds the user to the list of user that are in the guild.
     *
     * @param guildId - The id of the guild to add the user to.
     * @param discordMemberId - The id of the user that should be added to the guild.
     */
    public void addUserToServer(@NonNull String guildId, @NonNull String discordMemberId) {
        Guild guild = getGuildForId(guildId);
        var usersOnServer = guild.getUsersOnServer();
        usersOnServer.add(discordMemberId);

        guild.setUsersOnServer(usersOnServer);
        guildRepository.update(guild);
    }

    /**
     * Gets the guild object for the guild id passed.
     *
     * @param guildId - The id of the guild object to return.
     * @return the guild object with the given guild id.
     */
    @NonNull
    public Guild getGuildForId(@NonNull String guildId) {
        Collection<Guild> guilds = guildRepository.getAll();
        for (Guild currentGuild : guilds) {
            if (currentGuild.getGuildId().equals(guildId)) {
                return currentGuild;
            }
        }

        Guild guild = new Guild();
        guild.setGuildId(guildId);
        guild.setUsersOnServer(new ArrayList<>());
        guildRepository.add(guild);
        return guild;
    }

    /**
     * Method to get the size of the guild collection. Used mainly for test purposes.
     *
     * @return the size of the guild collection.
     */
    long getSizeGuildCollection() {
        return guildRepository.count();
    }
}
