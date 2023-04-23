package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.RemoveMemberHandler;
import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

@Singleton
@Slf4j
public class RemoveMember implements RemoveMemberHandler {

    @Inject UserController userController;
    @Inject ListingController listingController;
    @Inject GuildController guildController;

    @Inject
    public RemoveMember() {
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

        // Remove user from discord server and delete all their listings
        guildController.removeUserInServer(userId, guildId);
        listingController.deleteListingsForUser(userId, guildId);

        // If user no longer exists in ANY guild, remove them from user collection
        if (guildController.verifyUserNoLongerExistsInAnyGuild(userId)) {
            userController.removeUserByMemberId(userId);
        }
    }
}
