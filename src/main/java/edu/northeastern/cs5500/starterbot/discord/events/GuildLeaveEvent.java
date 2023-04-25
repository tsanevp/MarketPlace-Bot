package edu.northeastern.cs5500.starterbot.discord.events;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.discord.handlers.GuildLeaveEventHandler;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class GuildLeaveEvent implements GuildLeaveEventHandler {
    
    @Inject GuildController guildController;
    
    @Inject
    public GuildLeaveEvent() {
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


        
    }
    
}
