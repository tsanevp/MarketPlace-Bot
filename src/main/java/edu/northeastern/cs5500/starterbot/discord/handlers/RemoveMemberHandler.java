package edu.northeastern.cs5500.starterbot.discord.handlers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public interface RemoveMemberHandler {

    /**
     * Retrieves the name of the event when a member is removed from a guild.
     *
     * @return The name of the event.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when a member leaves a guild in JDA.
     *
     * @param event - The event where a member leaves a guild in JDA.
     */
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event);
}
