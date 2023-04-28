package edu.northeastern.cs5500.starterbot.discord.events;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.SettingLocationHelper;
import edu.northeastern.cs5500.starterbot.discord.commands.CreateTradingChannelCommand;
import edu.northeastern.cs5500.starterbot.discord.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewGuildJoinedHandler;
import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
import edu.northeastern.cs5500.starterbot.exceptions.GuildOwnerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class NewGuildJoinedEvent implements NewGuildJoinedHandler, ButtonHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final String DEFAULT_TRADING_CHANNEL_NAME = "trading-channel";
    private static final String CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION =
            "Please call the /createtradingchannel bot command to create a new text channel with a name you specify. Without doing this, the bot cannot function.";
    private static final String OWNER_INTRO_MESSAGE_WHEN_BOT_FIRST_ADDED =
            "Thank you for adding our MarketPlace Bot! For the bot to function as intended, a new text channel that handles item postings needs to be created. "
                    + "Is it okay for the bot to create a new channel named 'trading-channel' in your server? If you wish to create a channel with a custom name, or "
                    + "if a channel with this name already exists, you will need to call the /createtradingchannel bot command. Without you or the bot creating this "
                    + "new channel, the bot cannot funciton as intended.";
    @Inject JDA jda;
    @Inject GuildController guildController;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject SettingLocationHelper settingLocationHelper;
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
    public void onGuildJoin(@Nonnull GuildJoinEvent event) throws GuildOwnerNotFoundException {
        log.info("event: newguildjoined");

        var botId = jda.getSelfUser().getId();
        var guild = event.getGuild();
        var guildId = guild.getId();
        var membersInGuild = guild.getMembers();

        var guildOwner = guild.getOwner();
        if (guildOwner == null) {
            throw new GuildOwnerNotFoundException("Guild owner cannot be found or does not exist.");
        }

        // Sets the owner as the guild owner
        guildController.setGuildOwnerId(guildId, guildOwner.getId());

        // Create and send owner intro message to Guild owner as a DM
        var ownerIntroMessage = createIntroMessageForOwner(guildId);
        messageBuilder.sendPrivateMessage(guildOwner.getUser(), ownerIntroMessage);

        // Adds each user to the guild and send them an intro message
        addUsersToGuildAndAskLocation(membersInGuild, guildId, botId);
    }

    /**
     * Creates an intro message that will be sent to the Guild owner to ask if a new trading channel
     * can be created by the bot.
     *
     * @param guildId - The id of the guild the bot was just added to.
     * @return The intro message created that will eventually be sent to the guild owner.
     */
    @Nonnull
    @VisibleForTesting
    MessageCreateData createIntroMessageForOwner(@Nonnull String guildId) {
        // Embed builder with intro message sent to guild owner
        var introMessageEmbed =
                new EmbedBuilder()
                        .setDescription(OWNER_INTRO_MESSAGE_WHEN_BOT_FIRST_ADDED)
                        .setColor(EMBED_COLOR)
                        .build();

        var buttonIdCreateChannel = String.format("%s:%s:createnewchannel", getName(), guildId);
        var buttonIdDoNotCreateChannel = String.format("%s:%s:no", getName(), guildId);
        if (buttonIdCreateChannel == null || buttonIdDoNotCreateChannel == null) {
            throw new IllegalStateException("Button id creation failed in NewGuildJoinedEvent");
        }

        var createChannelButton =
                Button.success(buttonIdCreateChannel, "Bot Can Create The Channel");
        var doNotCreateChannelButton =
                Button.primary(buttonIdDoNotCreateChannel, "I'll Create The Channel");

        // Message builder with buttons and the embed message. Is later sent to guild owner
        return new MessageCreateBuilder()
                .addActionRow(createChannelButton, doNotCreateChannelButton)
                .setEmbeds(introMessageEmbed)
                .build();
    }

    /**
     * For the guild the bot was added to, add each existing user's id to the list of users to add
     * to a guild object. This is passed to guild controller where each user is added. Users are not
     * added to the User collection here since each addition requires a call to MongoDB. A User
     * objects gets created and added when a user first interacts with the bot.
     *
     * @param membersInGuild - A list of all the members in the guild as Member objects.
     * @param guildId - The id of the guild the users are from.
     * @param botId - The id of the bot.
     */
    private void addUsersToGuildAndAskLocation(
            @Nonnull List<Member> membersInGuild, @Nonnull String guildId, @Nonnull String botId) {
        var stateSelections = settingLocationHelper.createStatesMessageBuilder().build();
        List<String> listOfUserIds = new ArrayList<>();

        // Add each member to guild collection & ask their location
        for (Member member : membersInGuild) {
            var user = member.getUser();
            var userId = user.getId();

            listOfUserIds.add(userId);
            if (userId.equals(botId)) {
                continue;
            }

            // Send user DM w/ state & city selection
            messageBuilder.sendPrivateMessage(user, stateSelections);
        }

        // Sends a list of user ids to add to the guild to controller
        guildController.addAllCurrentUsersToServer(guildId, listOfUserIds);
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event)
            throws IllegalStateException, GuildNotFoundException {
        var userClicked = event.getUser();
        var buttonLabel = event.getButton().getLabel();

        var buttonId = event.getButton().getId();
        if (buttonId == null) {
            throw new IllegalStateException("Button event had no id");
        }

        var guildId = buttonId.split(":")[1];
        if (guildId == null) {
            throw new IllegalStateException("Button id did not have a guild id in it");
        }

        var guild = jda.getGuildById(guildId);
        if (guild == null) {
            throw new GuildNotFoundException("JDA could not find a guild with that id");
        }

        var buttonEvent = event.deferEdit().setComponents();

        // Verify that the user who the button is the guild owner
        if (!userClicked.getId().equals(guild.getOwnerId())) {
            buttonEvent
                    .setContent("Only the owner of this Discord server can select these buttons.")
                    .queue();
            return;
        }

        buttonEvent.queue();
        attemptToCreateTradingChannel(userClicked, buttonLabel, guild);
    }

    /**
     * Checks to see which button guild owner selected. If "Bot Can Create The Channel" was
     * selected, the creation of a channel named 'trading-channel' is attempted.
     *
     * @param owner - A guild owner.
     * @param buttonLabel - The button label.
     * @param guild - The guild JDA object.
     * @throws IllegalStateException Cannot retrieve message.
     */
    private void attemptToCreateTradingChannel(
            @Nonnull User owner, @Nonnull String buttonLabel, @Nonnull Guild guild)
            throws IllegalStateException {

        // Checks to see if owner selected that they'll create the channel
        if ("I'll Create The Channel".equals(buttonLabel)) {
            messageBuilder.sendPrivateMessage(
                    owner, CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION);
            return;
        }

        // Checks if a channel named trading-channel already exists on the server
        for (GuildChannel guildChannel : guild.getTextChannels()) {
            if (DEFAULT_TRADING_CHANNEL_NAME.equals(guildChannel.getName())) {
                var nameExistsMessage =
                        String.format(
                                "A text channel named %s already exists on your server. %s",
                                DEFAULT_TRADING_CHANNEL_NAME,
                                CALL_CREATE_TRADING_CHANNEL_COMMAND_INSTRUCTION);
                if (nameExistsMessage == null) {
                    throw new IllegalStateException("This message cannot be delivered");
                }

                messageBuilder.sendPrivateMessage(owner, nameExistsMessage);
                return;
            }
        }

        // Create the new "trading-channel". Move it under Text Channels
        createTradingChannelCommand.createNewTradingChannel(
                owner, guild, DEFAULT_TRADING_CHANNEL_NAME);
    }
}
