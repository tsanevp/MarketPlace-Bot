package edu.northeastern.cs5500.starterbot.discord.handlers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface StringSelectHandler {

    /**
     * Retrieves name of the command that uses a string select.
     *
     * @return The name of the command.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when a string select interaction occurs.
     *
     * @param event - The event where a string select interaction occured in JDA.
     */
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event);
}
