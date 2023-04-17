package edu.northeastern.cs5500.starterbot.command;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
@Slf4j
public class CreateTradingChannelCommand implements SlashCommandHandler {

    @Inject CreateTradingChannel createTradingChannel;

    @Inject
    public CreateTradingChannelCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "createtradingchannel";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(
                        getName(),
                        "Please input the name you wish to give the new trading channel. The name must be lower-case.")
                .addOption(
                        OptionType.STRING,
                        "name",
                        "Please provide the name of the text channel",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /createtradingchannel");

        var title = Objects.requireNonNull(event.getOption("name")).getAsString().toLowerCase();
        var guild = Objects.requireNonNull(event.getGuild());
        var guildOwner = Objects.requireNonNull(guild.getOwner()).getUser();

        // Verify that the user who called the command is the guild owner
        if (!guildOwner.getId().equals(event.getUser().getId())) {
            event.reply("Only the owner of this Discord server can create a new trading channel!")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        // Verify that the given name for the new trading channel does not already exist
        for (GuildChannel guildChannel : guild.getTextChannels()) {
            if (title.equals(guildChannel.getName())) {
                event.reply(
                                Objects.requireNonNull(
                                        String.format(
                                                "A text channel named %s already exists on your server. Please call this command again and input a name not already in use. Thank you.",
                                                title)))
                        .setEphemeral(true)
                        .queue();
                return;
            }
        }

        // A new text channel is created and set as the new trading channel for the server
        createTradingChannel.createNewTradingChannel(
                guildOwner, guild, Objects.requireNonNull(title));
        event.reply(
                        Objects.requireNonNull(
                                String.format(
                                        "The new text channel with the name %s has been set as the main trading channel. All new listings will be posted there!",
                                        title)))
                .setEphemeral(true)
                .queue();
    }
}
