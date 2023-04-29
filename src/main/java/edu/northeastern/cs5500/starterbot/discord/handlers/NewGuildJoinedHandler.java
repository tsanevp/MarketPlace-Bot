package edu.northeastern.cs5500.starterbot.discord.handlers;

import edu.northeastern.cs5500.starterbot.exceptions.GuildOwnerNotFoundException;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

public interface NewGuildJoinedHandler {

    /**
     * Retrieves the name of the event when the bot joins a guild.
     *
     * @return The name of the event.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when the bot joins a guild.
     *
     * @param event - The event where the bot joins a guild in JDA.
     */
    public void onGuildJoin(@Nonnull GuildJoinEvent event) throws GuildOwnerNotFoundException;
}
