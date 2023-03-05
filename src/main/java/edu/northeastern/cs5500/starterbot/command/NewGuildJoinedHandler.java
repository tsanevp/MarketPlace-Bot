package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public interface NewGuildJoinedHandler {
    @Nonnull
    public String getName();

    public void onGuildJoin(@Nonnull GuildJoinEvent event);
}
