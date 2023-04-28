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
        var tradingChannelId = guildController.getTradingChannelIdByGuildId(guildId);

        var channel = event.getGuild().getTextChannelById(tradingChannelId);
        if (channel == null) {
            throw new IllegalStateException("Channel was unable to be retrieved.");
        }

        // Remove all the posted listing the user has made from the trading channel
        var listingsOfMember = listingController.getListingsByMemberId(userId, guildId);
        for (Listing listing : listingsOfMember) {
            channel.deleteMessageById(listing.getMessageId()).queue();
        }

        // Remove user from discord server and delete all their listings
        guildController.removeUserInServer(guildId, userId);
        listingController.deleteCollectionOfListings(listingsOfMember);

        // If user no longer exists in ANY guild, remove them from user collection
        if (guildController.verifyUserNoLongerExistsInAnyGuild(userId)) {
            userController.removeUserByMemberId(userId);
        }
    }
}
