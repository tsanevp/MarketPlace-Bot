package edu.northeastern.cs5500.starterbot.discord.handlers;

import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonHandler {

    /**
     * Retrieves the name of the command that uses the button event.
     *
     * @return The name of the command.
     */
    @Nonnull
    public String getName();

    /**
     * Handles the event when a user interacts with a button in JDA.
     *
     * @param event - The button interaction event that occured in JDA.
     * @throws IllegalStateException When accessing a variable that's unavailable.
     * @throws GuildNotFoundException If a guild is unavailable in JDA.
     */
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event)
            throws IllegalStateException, GuildNotFoundException;
}
