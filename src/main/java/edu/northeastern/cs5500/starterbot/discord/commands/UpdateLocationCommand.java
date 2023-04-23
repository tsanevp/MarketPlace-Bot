package edu.northeastern.cs5500.starterbot.discord.commands;

import edu.northeastern.cs5500.starterbot.discord.Location;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class UpdateLocationCommand implements SlashCommandHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject Location location;

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

        var updateLocationMessage = createUpdateLocationMessage();

        event.reply(updateLocationMessage).setEphemeral(true).queue();
    }

    /**
     * Creates the message to send the user with instructions on how to properly select the state
     * and city they are located in.
     *
     * @param user - the user to send the message to.
     * @return the message to send to the user.
     */
    @Nonnull
    private MessageCreateData createUpdateLocationMessage() {
        var updateLocationString =
                "To update your State and City, plese select the correct values from the drop-down menus below.";

        // Embed with instructions on how to update your location
        var updateLocationInstructions =
                new EmbedBuilder()
                        .setDescription(updateLocationString)
                        .setColor(EMBED_COLOR)
                        .build();

        // Message that includes update instructions and location selection menus
        return location.createStatesMessageBuilder()
                .addEmbeds(updateLocationInstructions)
                .build();
    }
}
