package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonHandler {
    @Nonnull
    public String getName();

    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event);
}
