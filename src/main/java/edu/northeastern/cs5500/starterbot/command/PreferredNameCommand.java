package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserPreferenceController;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Singleton
@Slf4j
public class PreferredNameCommand implements SlashCommandHandler {

    @Inject UserPreferenceController userPreferenceController;

    @Inject
    public PreferredNameCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "preferredname";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Tell the bot what name to address you with")
                .addOptions(
                        new OptionData(
                                        OptionType.STRING,
                                        "name",
                                        "The bot will use this name to talk to you going forward")
                                .setRequired(true));
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /preferredname");
        String preferredName = Objects.requireNonNull(event.getOption("name")).getAsString();

        String discordUserId = event.getUser().getId();

        String oldPreferredName = userPreferenceController.getPreferredNameForUser(discordUserId);

        userPreferenceController.setPreferredNameForUser(discordUserId, preferredName);

        if (oldPreferredName == null) {
            event.reply("Your preferred name has been set to " + preferredName).queue();
        } else {
            event.reply(
                            "Your preferred name has been changed from "
                                    + oldPreferredName
                                    + " to "
                                    + preferredName)
                    .queue();
        }
    }
}
