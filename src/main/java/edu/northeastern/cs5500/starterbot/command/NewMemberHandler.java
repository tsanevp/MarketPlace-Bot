package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public interface NewMemberHandler {
    @Nonnull
    public String getName();

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event);
}
