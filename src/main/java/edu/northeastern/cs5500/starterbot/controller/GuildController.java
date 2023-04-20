package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.NonNull;
import edu.northeastern.cs5500.starterbot.model.Guild;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
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

    public void setTradingChannelId(@NonNull String guildId, @NonNull String tradingChannelId) {
        Guild guild = getGuildForId(guildId);
        guild.setTradingChannelId(tradingChannelId);
        guildRepository.update(guild);
    }

    @NonNull
    public String getTradingChannelId(@NonNull String guildId) {
        return getGuildForId(guildId).getTradingChannelId();
    }

    public void addUserToServer(@NonNull String guildId, @NonNull String discordMemberId) {
        Guild guild = getGuildForId(guildId);
        var usersOnServer = guild.getUsersOnServer();
        usersOnServer.add(discordMemberId);

        guild.setUsersOnServer(usersOnServer);
        guildRepository.update(guild);
    }

    @NonNull
    public List<String> getUsersOnServer(@NonNull String guildId) {
        return getGuildForId(guildId).getUsersOnServer();
    }

    @NonNull
    private Guild getGuildForId(@NonNull String guildId) {
        Collection<Guild> guilds = guildRepository.getAll();
        for (Guild currentGuild : guilds) {
            if (currentGuild.getGuildId().equals(guildId)) {
                return currentGuild;
            }
        }

        Guild guild = new Guild();
        guild.setGuildId(guildId);
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
