package edu.northeastern.cs5500.starterbot.controller;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.model.Guild;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
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
     * Sets the guild owner id for the current guild.
     *
     * @param guildId - The id of the guild to set the trading channel id for.
     * @param guildOwnerId - The id of the owner to set for the guild.
     */
    public void setGuildOwnerId(@Nonnull String guildId, @Nonnull String guildOwnerId) {
        var guild = getGuildByGuildId(guildId);

        guild.setGuildOwnerId(guildOwnerId);
        guildRepository.update(guild);
    }

    /**
     * Sets the trading channel id for the current guild.
     *
     * @param guildId - The id of the guild to set the trading channel id for.
     * @param tradingChannelId - The trading channel id to set for the guild.
     */
    public void setTradingChannelId(@Nonnull String guildId, @Nonnull String tradingChannelId) {
        var guild = getGuildByGuildId(guildId);

        guild.setTradingChannelId(tradingChannelId);
        guildRepository.update(guild);
    }

    /**
     * Gets the trading channel id for the current guild.
     *
     * @param guildId - The id of the guild to get the trading channel id for.
     * @return The trading channel id for the guild id passed.
     */
    @Nonnull
    public String getTradingChannelIdByGuildId(@Nonnull String guildId) {
        return getGuildByGuildId(guildId).getTradingChannelId();
    }

    /**
     * Adds the user to the list of user that are in the guild.
     *
     * @param guildId - The id of the guild to add the user to.
     * @param discordMemberId - The id of the user that should be added to the guild.
     */
    public void addUserToServer(@Nonnull String guildId, @Nonnull String discordMemberId) {
        var guild = getGuildByGuildId(guildId);
        var usersOnServer = guild.getUsersOnServer();

        usersOnServer.add(discordMemberId);
        guild.setUsersOnServer(usersOnServer);
        guildRepository.update(guild);
    }

    /**
     * Adds all the current user in the guild to the list of user that are in the guild.
     *
     * @param guildId - The id of the guild to add the user to.
     * @param listOfUserIds - A list of the user ids that should be added to the guild.
     */
    public void addAllCurrentUsersToServer(
            @Nonnull String guildId, @Nonnull List<String> listOfUserIds) {
        var guild = getGuildByGuildId(guildId);
        var usersOnServer = guild.getUsersOnServer();

        for (String userId : listOfUserIds) {
            usersOnServer.add(userId);
        }

        guild.setUsersOnServer(usersOnServer);
        guildRepository.update(guild);
    }

    /**
     * Removes the user from the guild.
     *
     * @param discordMemberId - The id of the user that should be removed from the guild.
     * @param guildId - The id of the guild that the user is in.
     * @returns Whether the user was successfully removed from the guild.
     */
    public boolean removeUserInServer(@Nonnull String guildId, @Nonnull String discordMemberId) {
        var guild = getGuildByGuildId(guildId);
        var guildUsers = guild.getUsersOnServer();

        if (guildUsers.remove(discordMemberId)) {
            guild.setUsersOnServer(guildUsers);
            guildRepository.update(guild);
            return true;
        }
        return false;
    }

    /**
     * Method to check if the user already exists in the guild.
     *
     * @param discordMemberId - The discord user that may be contained in the guild.
     * @param guildId - The id of the guild to verify a user is in.
     * @return Whether the user already exists in the guild.
     */
    public boolean verifyUserInGuild(@Nonnull String guildId, @Nonnull String discordMemberId) {
        return getGuildByGuildId(guildId).getUsersOnServer().contains(discordMemberId);
    }

    /**
     * Method to check if the user exists in ANY guild.
     *
     * @param discordMemberId - The discord user that may be contained in the guild.
     * @return The true if the user is no longer exists in any guild, false if not.
     */
    public boolean verifyUserNoLongerExistsInAnyGuild(@Nonnull String discordMemberId) {
        return guildRepository.getAll().stream()
                .filter(guild -> guild.getUsersOnServer().contains(discordMemberId))
                .toList()
                .isEmpty();
    }

    /**
     * Gets the guild object for the guild id passed.
     *
     * @param guildId - The id of the guild object to return.
     * @return The guild object with the given guild id.
     */
    @Nonnull
    public Guild getGuildByGuildId(@Nonnull String guildId) {
        Collection<Guild> guilds = guildRepository.getAll();
        for (Guild currentGuild : guilds) {
            if (currentGuild.getGuildId().equals(guildId)) {
                return currentGuild;
            }
        }

        Guild guild = new Guild();
        guild.setGuildId(guildId);
        guild.setUsersOnServer(new HashSet<>());
        guildRepository.add(guild);
        return guild;
    }

    /**
     * Removes guild from database.
     *
     * @param guildId - The id of the guild that needs to be removed.
     * @return Whether the guild has been successfully deleted.
     */
    public boolean removeGuildByGuildId(@Nonnull String guildId) {
        Collection<Guild> guilds = guildRepository.getAll();
        for (Guild guild : guilds) {
            var guildObjectId = guild.getId();

            if (guild.getGuildId().equals(guildId) && Objects.nonNull(guildObjectId)) {
                guildRepository.delete(guildObjectId);
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get the size of the guild collection. Used mainly for test purposes.
     *
     * @return The size of the guild collection.
     */
    @VisibleForTesting
    @Nonnegative
    long getSizeGuildCollection() {
        return guildRepository.count();
    }
}
