package edu.northeastern.cs5500.starterbot.discord.handlers;

import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
import edu.northeastern.cs5500.starterbot.exceptions.GuildOwnerNotFoundException;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface SlashCommandHandler {

    /**
     * Retrieves the name of the command that uses the slash command.
     *
     * @return The name of the command.
     */
    @Nonnull
    public String getName();

    /**
     * Retrieves user input for each option displayed in the slash command.
     *
     * @return The data from the slash command.
     */
    @Nonnull
    public CommandData getCommandData();

    /**
     * Handles the event when a slash command interaction occurs.
     *
     * @param event - The event where a slash command interaction occured in JDA.
     */
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
            throws GuildNotFoundException, GuildOwnerNotFoundException;
}
