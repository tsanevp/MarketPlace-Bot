package edu.northeastern.cs5500.starterbot.discord.handlers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

public interface LeaveGuildEventHandler {

    /**
     * Retrieves the name of the event when the bot leaves the guild.
     *
     * @return The name of the event.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when the bot leaves the guild.
     *
     * @param event - The event where the bot is removed/leaves the guild in JDA.
     */
    public void onGuildLeaveEvent(@Nonnull GuildLeaveEvent event);
}
