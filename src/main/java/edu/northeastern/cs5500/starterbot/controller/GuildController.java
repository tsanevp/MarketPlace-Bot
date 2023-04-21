package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.NonNull;
import edu.northeastern.cs5500.starterbot.model.Guild;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public void setTradingChannelId(@NonNull String guildId, String tradingChannelId) {
        Guild guild = getGuildByGuildId(guildId);
        guild.setTradingChannelId(tradingChannelId);
        guildRepository.update(guild);
    }

    /**
     * Adds the user to the list of user that are in the guild.
     *
     * @param guildId - The id of the guild to add the user to.
     * @param discordMemberId - The id of the user that should be added to the guild.
     * @returns Whether the user was successfully added into the guild.
     */
    public boolean addUserToServer(@NonNull String guildId, @NonNull String discordMemberId) {
        if (verifyUserInGuild(discordMemberId, guildId)) {
            return false;
        }
        Guild guild = getGuildByGuildId(guildId);
        var usersOnServer = guild.getUsersOnServer();
        usersOnServer.add(discordMemberId);

        guild.setUsersOnServer(usersOnServer);
        guildRepository.update(guild);
        return true;
    }

    /**
     * Removes the user from the guild.
     *
     * @param discordMemberId - The id of the user that should be removed from the guild.
     * @param guildId - The id of the guild that the user is in.
     * @returns Whether the user was successfully removed from the guild.
     */
    public boolean removeUserInServer(@NonNull String discordMemberId, @NonNull String guildId) {
        List<String> guildUsers = getGuildByGuildId(guildId).getUsersOnServer();
        return guildUsers.remove(discordMemberId);
    }

    /**
     * Method to get the size of the guild collection. Used mainly for test purposes.
     *
     * @param discordMemberId - The discord user that may be contained in the guild.
     * @param guildId - The id of the guild to verify a user is in.
     * @return the size of the guild collection.
     */
    public boolean verifyUserInGuild(@NonNull String discordMemberId, @NonNull String guildId) {
        return getGuildByGuildId(guildId).getUsersOnServer().contains(discordMemberId);
    }

    /**
     * Gets the guild object for the guild id passed.
     *
     * @param guildId - The id of the guild object to return.
     * @return the guild object with the given guild id.
     */
    @NonNull
    public Guild getGuildByGuildId(@NonNull String guildId) {
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
     * Removes guild from database.
     *
     * @param guildId - The id of the guild that needs to be removed.
     * @return Whether the guild has been successfully deleted.
     */
    public boolean removeGuildByGuildId(@NonNull String guildId) {
        Collection<Guild> guilds = guildRepository.getAll();
        for (Guild guild : guilds) {
            if (guild.getGuildId().equals(guildId)) {
                guildRepository.delete(guild.getId());
                return true;
            }
        }
        return false;
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
