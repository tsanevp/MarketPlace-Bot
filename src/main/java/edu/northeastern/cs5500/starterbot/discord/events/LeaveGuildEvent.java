package edu.northeastern.cs5500.starterbot.discord.events;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.handlers.LeaveGuildEventHandler;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

@Singleton
@Slf4j
public class LeaveGuildEvent implements LeaveGuildEventHandler {

    @Inject GuildController guildController;
    @Inject ListingController listingController;
    @Inject UserController userController;

    @Inject
    public LeaveGuildEvent() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "guildleave";
    }

    @Override
    public void onGuildLeaveEvent(@Nonnull GuildLeaveEvent event) {
        log.info("event: guildleave");

        var guildId = event.getGuild().getId();

        guildController.removeGuildByGuildId(guildId);
        listingController.deleteListingsWithGuildId(guildId);

        var membersList = guildController.getGuildByGuildId(guildId).getUsersOnServer();
        var memberIdsToRemove =
                membersList.stream()
                        .filter(
                                memberId ->
                                        guildController.verifyUserNoLongerExistsInAnyGuild(
                                                memberId))
                        .toList();

        memberIdsToRemove.forEach(memberId -> userController.removeUserByMemberId(memberId));
    }
}
