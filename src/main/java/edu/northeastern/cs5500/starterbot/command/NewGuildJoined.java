package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserController;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class NewGuildJoined implements NewGuildJoinedHandler, ButtonHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final String DEFAULT_TRADING_CHANNEL_NAME = "trading-channel";
    private static final String BOT_INTRODUCTION_MESSAGE_TO_GUILD_OWNER =
            "Thank you for adding our MarketPlace Bot! For the bot to function as intended, a new text channel that handles item postings needs to be created. Is it okay for the bot to create a new channel named 'trading-channel' in your server? If you wish to create a channel with a custom name, or if a channel with this name already exists, you will need to call the /createtradingchannel bot command. Without you or the bot creating this new channel, the bot cannot funciton as intended.";
    private static final String CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION =
            "Please call the /createtradingchannel bot command to create a new text channel with a name you specify. Without doing this, the bot cannot function.";
    private static final String CREATE_NEW_CHANNEL_BUTTON_LABEL = "Bot Can Create The Channel";
    private static final String TRADING_CHANNEL_NAME_ALREADY_EXISTS_MESSAGE =
            String.format(
                    "A text channel named %s already exists on your server. %s",
                    DEFAULT_TRADING_CHANNEL_NAME, CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION);

    @Inject Location location;
    @Inject UserController userController;

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
                        .setDescription(BOT_INTRODUCTION_MESSAGE_TO_GUILD_OWNER)
                        .setColor(EMBED_COLOR)
                        .build();

        // Message builder with buttons and the embed message. Is later sent to guild owner
        var ownerIntroMessage =
                new MessageCreateBuilder()
                        .addActionRow(
                                Button.success(
                                        getName() + ":createnewchannel",
                                        CREATE_NEW_CHANNEL_BUTTON_LABEL),
                                Button.primary(getName() + ":no", "I'll Create The Channel"))
                        .setEmbeds(introMessageEmbed)
                        .build();

        // Sends intro message to Guild owner as a DM
        sendPrivateMessage(owner, ownerIntroMessage);

        var stateSelections = location.createStatesMessageBuilder().build();
        var membersInGuild = event.getGuild().getMembers();
        var guildId = event.getGuild().getId();
        var botId = event.getJDA().getSelfUser().getId();

        // Add each member to collection, set the GuildId, ask their location
        for (Member member : membersInGuild) {
            var user = member.getUser();
            var userId = user.getId();
            userController.setGuildIdForUser(userId, guildId);
            // If current member is not the bot, send them DM w/ state selection
            if (!userId.equals(botId)) {
                sendPrivateMessage(user, stateSelections);
            }
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

        // Create new trading-channel was selected
        if (CREATE_NEW_CHANNEL_BUTTON_LABEL.equals(event.getButton().getLabel())) {
            // Checks if a channel named trading-channel already exists on the server
            for (GuildChannel guildChannel : guild.getTextChannels()) {
                if (DEFAULT_TRADING_CHANNEL_NAME.equals(guildChannel.getName())) {
                    sendPrivateMessage(
                            owner,
                            Objects.requireNonNull(TRADING_CHANNEL_NAME_ALREADY_EXISTS_MESSAGE));
                    return;
                }
            }
            // Create the new "trading-channel". Move it under Text Channels
            createNewTradingChannel(owner, guild, DEFAULT_TRADING_CHANNEL_NAME);
        } else {
            sendPrivateMessage(owner, CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION);
        }
    }

    /**
     * Opens a private channel with the user provided and send the given message.
     *
     * @param user - The user to send the private message to.
     * @param messageToSend - The message to send the user.
     */
    private void sendPrivateMessage(User user, @Nonnull String messageToSend) {
        user.openPrivateChannel().complete().sendMessage(messageToSend).queue();
    }

    /**
     * Opens a private channel with the user provided and send the given message.
     *
     * @param user - The user to send the private message to.
     * @param messageToSend - The message to send the user.
     */
    private void sendPrivateMessage(User user, @Nonnull MessageCreateData messageToSend) {
        user.openPrivateChannel().complete().sendMessage(messageToSend).queue();
    }

    /**
     * Creates a trading channel with specific permissions and adds it to "Text Channels" grouping.
     *
     * @param owner - The owner of the Discord Guild.
     * @param guild - The guild to add the text channel to.
     * @param channelName - The name to give the channel.
     */
    private void createNewTradingChannel(User owner, Guild guild, @Nonnull String channelName) {
        Category category = guild.getCategoriesByName("text channels", true).get(0);

        // Permissions that should be applied to the channel
        EnumSet<Permission> deny =
                EnumSet.of(
                        Permission.MESSAGE_SEND,
                        Permission.CREATE_PRIVATE_THREADS,
                        Permission.MESSAGE_MANAGE,
                        Permission.MANAGE_THREADS);

        // Creation of the new channel
        TextChannel textChannel =
                category.createTextChannel(channelName)
                        .addPermissionOverride(guild.getPublicRole(), null, deny)
                        .complete();
        textChannel.getManager().setParent(category);

        // Send success message that the channel was created
        var successMessage =
                Objects.requireNonNull(
                        String.format(
                                "A new channel named %s has been created in your server %s.",
                                channelName, guild.getName()));
        sendPrivateMessage(owner, successMessage);

        // Set this channel as the trading channel for the Discord server
        userController.setTradingChannel(guild.getOwnerId(), textChannel.getId());
    }
}
