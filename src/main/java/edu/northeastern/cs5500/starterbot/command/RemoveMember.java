package edu.northeastern.cs5500.starterbot.command;

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
    @Inject MessageBuilder messageBuilder;

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
        var user = event.getUser();
        var guild = event.getGuild();

        userController.removeUserByMemberAndGuildId(user.getId(), guild.getId());
        listingController.deleteListingsForUser(user.getId());
    }
}
