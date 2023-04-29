package edu.northeastern.cs5500.starterbot.discord.handlers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public interface NewMemberHandler {

    /**
     * Retrieves the name of the event when a member joins a guild.
     *
     * @return The name of the event.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when a member joins a guild in JDA.
     *
     * @param event - The event where a member joins a guild in JDA.
     */
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event);
}
