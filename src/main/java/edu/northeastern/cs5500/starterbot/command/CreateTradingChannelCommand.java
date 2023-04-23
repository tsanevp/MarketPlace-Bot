package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
@Slf4j
public class CreateTradingChannelCommand implements SlashCommandHandler {

    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;
    @Inject GuildController guildController;

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
                var channelExistsMessage =
                        String.format(
                                "A text channel named %s already exists on your server. Please call this command again and input a name not already in use. Thank you.",
                                title);
                Objects.requireNonNull(channelExistsMessage);

                event.reply(channelExistsMessage).setEphemeral(true).queue();
                return;
            }
        }

        // A new text channel is created and set as the new trading channel for the server
        createNewTradingChannel(guildOwner, guild, Objects.requireNonNull(title));
        var channelCreatedMessage =
                String.format(
                        "The new text channel with the name %s has been set as the main trading channel. All new listings will be posted there!",
                        title);
        Objects.requireNonNull(channelCreatedMessage);

        event.reply(channelCreatedMessage).setEphemeral(true).queue();
    }

    /**
     * Creates a trading channel with specific permissions and adds it to "Text Channels" grouping.
     *
     * @param owner - The owner of the Discord Guild.
     * @param guild - The guild to add the text channel to.
     * @param channelName - The name to give the channel.
     */
    public void createNewTradingChannel(
            @Nonnull User owner, @Nonnull Guild guild, @Nonnull String channelName) {
        var category = guild.getCategoriesByName("text channels", true).get(0);

        // Permissions that should be applied to the channel
        EnumSet<Permission> deny =
                EnumSet.of(
                        Permission.MESSAGE_SEND,
                        Permission.CREATE_PRIVATE_THREADS,
                        Permission.MESSAGE_MANAGE,
                        Permission.MANAGE_THREADS);

        // Creation of the new channel
        var textChannel =
                category.createTextChannel(channelName)
                        .addPermissionOverride(guild.getPublicRole(), null, deny)
                        .complete();
        textChannel.getManager().setParent(category);

        var successMessage =
                Objects.requireNonNull(
                        String.format(
                                "A new channel named %s has been created in your server %s.",
                                channelName, guild.getName()));

        // Send success message that the channel was created
        messageBuilder.sendPrivateMessage(owner, successMessage);

        // Set this channel as the trading channel for the Discord server
        guildController.setTradingChannelId(guild.getId(), textChannel.getId());
    }
}
