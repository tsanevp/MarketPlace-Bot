package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class UpdateLocationCommand implements SlashCommandHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject Location location;
    @Inject UserController userController;

    @Inject
    public UpdateLocationCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "updatelocation";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(
                getName(), "Update your location by selecting you new State &/or City");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: updatelocation");

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setDescription(
                                "To update your State and City, plese select the correct values from the drop-down menus below.")
                        .setColor(EMBED_COLOR);

        // Create the new user Message to send. Includes the built State location selection menus
        MessageCreateBuilder newMemberIntroMsg =
                location.createStatesMessageBuilder()
                        .mention(event.getUser())
                        .addEmbeds(embedBuilder.build());

        // Send the message to the user
        event.reply(newMemberIntroMsg.build()).setEphemeral(true).queue();
    }
}
