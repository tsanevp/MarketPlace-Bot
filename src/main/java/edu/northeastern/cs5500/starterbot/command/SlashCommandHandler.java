package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface SlashCommandHandler {
    @Nonnull
    public String getName();

    @Nonnull
    public CommandData getCommandData();

    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event);
}
