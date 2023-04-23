package edu.northeastern.cs5500.starterbot.discord.events;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.discord.SettingLocationHelper;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.commands.CreateTradingChannelCommand;
import edu.northeastern.cs5500.starterbot.discord.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewGuildJoinedHandler;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class NewGuildJoinedEvent implements NewGuildJoinedHandler, ButtonHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final String DEFAULT_TRADING_CHANNEL_NAME = "trading-channel";
    private static final String CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION =
            "Please call the /createtradingchannel bot command to create a new text channel with a name you specify. Without doing this, the bot cannot function.";
    private static final String BOT_INTRO_MESSAGE_WHEN_FIRST_ADDED =
            "Thank you for adding our MarketPlace Bot! For the bot to function as intended, a new text channel that handles item postings needs to be created. Is it okay for the bot to create a new channel named 'trading-channel' in your server? If you wish to create a channel with a custom name, or if a channel with this name already exists, you will need to call the /createtradingchannel bot command. Without you or the bot creating this new channel, the bot cannot funciton as intended.";

    @Nonnull
    private static final String TRADING_CHANNEL_NAME_ALREADY_EXISTS =
            Objects.requireNonNull(
                    String.format(
                            "A text channel named %s already exists on your server. %s",
                            DEFAULT_TRADING_CHANNEL_NAME,
                            CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION));

    @Inject SettingLocationHelper location;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject GuildController guildController;
    @Inject CreateTradingChannelCommand createTradingChannelCommand;

    @Inject
    public NewGuildJoinedEvent() {
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
        var membersInGuild = event.getGuild().getMembers();
        var guildId = event.getGuild().getId();
        var botId = event.getJDA().getSelfUser().getId();

        askOwnerToCreateTradingChannel(owner, guildId);

        addUsersToGuildAndAskLocation(membersInGuild, guildId, botId);
    }

    /**
     * Send an intro message to the Guild owner and ask if a new trading channel can be created by
     * the bot.
     *
     * @param owner - The guild owner.
     * @param guildId - The id of the guild the bot was just added to.
     */
    private void askOwnerToCreateTradingChannel(@Nonnull User owner, @Nonnull String guildId) {
        // Embed builder with intro message sent to guild owner
        var introMessageEmbed =
                new EmbedBuilder()
                        .setDescription(BOT_INTRO_MESSAGE_WHEN_FIRST_ADDED)
                        .setColor(EMBED_COLOR)
                        .build();

        var buttonIdCreateChannel =
                Objects.requireNonNull(String.format("%s:%s:createnewchannel", getName(), guildId));
        var buttonIdDoNotCreateChannel =
                Objects.requireNonNull(String.format("%s:%s:no", getName(), guildId));

        var createChannelButton =
                Button.success(buttonIdCreateChannel, "Bot Can Create The Channel");
        var doNotCreateChannelButton =
                Button.primary(buttonIdDoNotCreateChannel, "I'll Create The Channel");

        // Message builder with buttons and the embed message. Is later sent to guild owner
        var ownerIntroMessage =
                new MessageCreateBuilder()
                        .addActionRow(createChannelButton, doNotCreateChannelButton)
                        .setEmbeds(introMessageEmbed)
                        .build();

        // Sends intro message to Guild owner as a DM
        messageBuilder.sendPrivateMessage(owner, ownerIntroMessage);
    }

    /**
     * Add all users to the list of members stored by their Guild object.
     *
     * @param membersInGuild - A list of all the members in the guild as Member objects.
     * @param guildId - The id of the guild the users are from.
     * @param botId - The id of the bot.
     */
    private void addUsersToGuildAndAskLocation(
            @Nonnull List<Member> membersInGuild, @Nonnull String guildId, @Nonnull String botId) {
        var stateSelections = location.createStatesMessageBuilder().build();

        // Add each member to guild collection & ask their location
        for (Member member : membersInGuild) {
            var user = member.getUser();
            var userId = user.getId();

            guildController.addUserToServer(guildId, userId);

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
        var owner = event.getUser(); // Only the owner can toggle this event, no need to
        var buttonLabel = event.getButton().getLabel();
        var buttonId = Objects.requireNonNull(event.getButton().getId());
        var guildId = Objects.requireNonNull(buttonId.split(":")[1]);
        var guild = Objects.requireNonNull(event.getJDA().getGuildById(guildId));

        // Delete buttons so no longer clickable
        event.deferEdit().setComponents().queue();

        attemptToCreateTradingChannel(owner, buttonLabel, guild);
    }

    /**
     * Checks to see which button guild owner selected. If "Bot Can Create The Channel" was
     * selected, the creation of a channel named 'trading-channel' is attempted.
     *
     * @param owner - A guild owner.
     * @param buttonLabel - The button label.
     * @param guild - The guild JDA object.
     */
    private void attemptToCreateTradingChannel(
            @Nonnull User owner, @Nonnull String buttonLabel, @Nonnull Guild guild) {

        // Checks to see if owner selected that they'll create the channel
        if ("I'll Create The Channel".equals(buttonLabel)) {
            messageBuilder.sendPrivateMessage(
                    owner, CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION);
            return;
        }

        // Checks if a channel named trading-channel already exists on the server
        for (GuildChannel guildChannel : guild.getTextChannels()) {
            if (DEFAULT_TRADING_CHANNEL_NAME.equals(guildChannel.getName())) {
                messageBuilder.sendPrivateMessage(owner, TRADING_CHANNEL_NAME_ALREADY_EXISTS);
                return;
            }
        }

        // Create the new "trading-channel". Move it under Text Channels
        createTradingChannelCommand.createNewTradingChannel(
                owner, guild, DEFAULT_TRADING_CHANNEL_NAME);
    }
}
