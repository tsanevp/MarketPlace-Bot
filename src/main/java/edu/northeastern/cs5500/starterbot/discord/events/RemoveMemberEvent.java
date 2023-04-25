package edu.northeastern.cs5500.starterbot.discord.events;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.handlers.RemoveMemberHandler;
import edu.northeastern.cs5500.starterbot.model.Listing;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

@Singleton
@Slf4j
public class RemoveMemberEvent implements RemoveMemberHandler {

    @Inject UserController userController;
    @Inject ListingController listingController;
    @Inject GuildController guildController;

    @Inject
    public RemoveMemberEvent() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "removemember";
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        log.info("event: removemember");
        var userId = event.getUser().getId();
        var guildId = event.getGuild().getId();

        var tradingChannelId = guildController.getGuildByGuildId(guildId).getTradingChannelId();
        var channel = event.getGuild().getTextChannelById(tradingChannelId);

        // Remove all the posted listing the user has made from the trading channel
        for (Listing listing : listingController.getListingsByMemberId(userId, guildId)) {
            channel.deleteMessageById(listing.getMessageId()).queue();
        }

        deleteUserAndListingsMade(userId, guildId);
    }

    /**
     * Removes the user from the guild user set, deletes their stored listings, and deletes the user
     * object if they no longer belong to any guilds with the bot in them.
     *
     * @param userId - The userId of the discord user.
     * @param guildId - The guild id that the user was removed from or left.
     */
    private void deleteUserAndListingsMade(@Nonnull String userId, @Nonnull String guildId) {
        // Remove user from discord server and delete all their listings
        guildController.removeUserInServer(guildId, userId);
        listingController.deleteListingsForUser(userId, guildId);

        // If user no longer exists in ANY guild, remove them from user collection
        if (guildController.verifyUserNoLongerExistsInAnyGuild(userId)) {
            userController.removeUserByMemberId(userId);
        }
    }
}
