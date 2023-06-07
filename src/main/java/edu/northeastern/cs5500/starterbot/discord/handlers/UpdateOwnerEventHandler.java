package edu.northeastern.cs5500.starterbot.discord.handlers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;

public interface UpdateOwnerEventHandler {

    /**
     * Retrieves the name of the event when the guild owner is updated.
     *
     * @return The name of the event.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when the owner of the guild is updated.
     *
     * @param event - The JDA event where owner of the guild is updated.
     */
    public void onGuildUpdateOwner(@Nonnull GuildUpdateOwnerEvent event);
}
