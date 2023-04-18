package edu.northeastern.cs5500.starterbot.command.handlers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;

public interface RemoveMemberHandler {
    @Nonnull
    public String getName();

    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event);
}
