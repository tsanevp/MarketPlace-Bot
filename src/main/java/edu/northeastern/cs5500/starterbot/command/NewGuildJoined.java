package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.NewGuildJoinedHandler;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class NewGuildJoined implements NewGuildJoinedHandler, ButtonHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final String DEFAULT_TRADING_CHANNEL_NAME = "trading-channel";
    private static final String CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION =
            "Please call the /createtradingchannel bot command to create a new text channel with a name you specify. Without doing this, the bot cannot function.";

    @Inject Location location;
    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;
    @Inject CreateTradingChannel createTradingChannel;

    @Inject
    public NewGuildJoined() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "newguildjoined";
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.info("event: newguildjoined");

        // Get Guild Owner as a User
        var owner = Objects.requireNonNull(event.getGuild().getOwner()).getUser();

        // Embed builder with intro message later sent to guild owner
        var introMessageEmbed =
                new EmbedBuilder()
                        .setDescription(
                                "Thank you for adding our MarketPlace Bot! For the bot to function as intended, a new text channel that handles item postings needs to be created. Is it okay for the bot to create a new channel named 'trading-channel' in your server? If you wish to create a channel with a custom name, or if a channel with this name already exists, you will need to call the /createtradingchannel bot command. Without you or the bot creating this new channel, the bot cannot funciton as intended.")
                        .setColor(EMBED_COLOR)
                        .build();

        // Message builder with buttons and the embed message. Is later sent to guild owner
        var ownerIntroMessage =
                new MessageCreateBuilder()
                        .addActionRow(
                                Button.success(
                                        getName() + ":createnewchannel",
                                        "Bot Can Create The Channel"),
                                Button.primary(getName() + ":no", "I'll Create The Channel"))
                        .setEmbeds(introMessageEmbed)
                        .build();

        // Sends intro message to Guild owner as a DM
        messageBuilder.sendPrivateMessage(owner, ownerIntroMessage);

        var stateSelections = location.createStatesMessageBuilder().build();
        var membersInGuild = event.getGuild().getMembers();
        var guildId = event.getGuild().getId();
        var botId = event.getJDA().getSelfUser().getId();

        // Add each member to collection, set the GuildId, ask their location
        for (Member member : membersInGuild) {
            var user = member.getUser();
            var userId = user.getId();

            userController.setGuildIdForUser(userId, guildId);

            if (userId.equals(botId)) {
                continue;
            }

            // Send user DM w/ state & city selection
            messageBuilder.sendPrivateMessage(user, stateSelections);
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // Define the Guild owner and request the GuildId from the user collection
        var owner = event.getUser(); // Only the owner can toggle this event, no need to verify
        var ownerGuildId = Objects.requireNonNull(userController.getGuildIdForUser(owner.getId()));
        var guild = Objects.requireNonNull(event.getJDA().getGuildById(ownerGuildId));

        // Delete buttons so no longer clickable
        event.deferEdit().setComponents().queue();

        // Send instruction on how Guild Owner should create new trading channel
        if ("I'll Create The Channel".equals(event.getButton().getLabel())) {
            messageBuilder.sendPrivateMessage(
                    owner, CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION);
            return;
        }

        // Checks if a channel named trading-channel already exists on the server
        for (GuildChannel guildChannel : guild.getTextChannels()) {
            if (DEFAULT_TRADING_CHANNEL_NAME.equals(guildChannel.getName())) {
                messageBuilder.sendPrivateMessage(
                        owner,
                        Objects.requireNonNull(
                                String.format(
                                        "A text channel named %s already exists on your server. %s",
                                        DEFAULT_TRADING_CHANNEL_NAME,
                                        CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION)));
                return;
            }
        }

        // Create the new "trading-channel". Move it under Text Channels
        createTradingChannel.createNewTradingChannel(owner, guild, DEFAULT_TRADING_CHANNEL_NAME);
    }
}
