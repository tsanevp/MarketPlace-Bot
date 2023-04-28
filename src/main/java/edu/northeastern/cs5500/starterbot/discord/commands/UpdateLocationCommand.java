package edu.northeastern.cs5500.starterbot.discord.commands;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.discord.SettingLocationHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class UpdateLocationCommand implements SlashCommandHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject SettingLocationHelper settingLocationHelper;

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
                getName(), "Update your location by selecting the State and City closest to you");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /updatelocation");

        var statesSelectMessageBuilder = settingLocationHelper.createStatesMessageBuilder();
        var updateLocationMessage = createUpdateLocationMessage(statesSelectMessageBuilder);

        event.reply(updateLocationMessage).setEphemeral(true).queue();
    }

    /**
     * Creates the message to send the user with instructions on how to properly select the state
     * and city they are located in.
     *
     * @param statesSelectMessageBuilder - A message create builder containing both the state select
     *     menus.
     * @return The message to send to the user with how to update their location and the state
     *     select menus.
     */
    @Nonnull
    @VisibleForTesting
    MessageCreateData createUpdateLocationMessage(
            @Nonnull MessageCreateBuilder statesSelectMessageBuilder) {
        var updateLocationString =
                "To set your State and City, plese select the correct values from the drop-down menus below.";
        var updateLocationInstructions =
                new EmbedBuilder()
                        .setDescription(updateLocationString)
                        .setColor(EMBED_COLOR)
                        .build();
        return statesSelectMessageBuilder.addEmbeds(updateLocationInstructions).build();
    }
}
