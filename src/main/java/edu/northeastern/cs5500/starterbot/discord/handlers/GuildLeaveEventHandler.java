package edu.northeastern.cs5500.starterbot.discord.handlers;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

public interface GuildLeaveEventHandler {
    @Nonnull
    public String getName();

    public void onGuildLeaveEvent(@Nonnull GuildLeaveEvent event);
}
