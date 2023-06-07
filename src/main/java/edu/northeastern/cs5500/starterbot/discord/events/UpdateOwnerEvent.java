package edu.northeastern.cs5500.starterbot.discord.events;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.handlers.UpdateOwnerEventHandler;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;

@Singleton
@Slf4j
public class UpdateOwnerEvent implements UpdateOwnerEventHandler {

    @Inject UserController userController;
    @Inject ListingController listingController;
    @Inject GuildController guildController;

    @Inject
    public UpdateOwnerEvent() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "updateowner";
    }

    @Override
    public void onGuildUpdateOwner(@Nonnull GuildUpdateOwnerEvent event) {
        log.info("event: updateowner");
        
        guildController.setGuildOwnerId(event.getGuild().getId(), event.getNewOwnerId());
    }
}
