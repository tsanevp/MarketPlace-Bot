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
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.Builder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class NewGuildJoined implements NewGuildJoinedHandler, ButtonHandler, StringSelectHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final String TRADING_CHANNEL_ID = "trading-channel";

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

        // Ask the guild owner if they want to create a new trading-channel or use existing channel
        User owner = Objects.requireNonNull(event.getGuild().getOwner()).getUser();
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(
                                "Thank you for adding our MarketPlace Bot! To function as intended, a text channel to POST new listings needs to be assigned. Would you like to create a new channel or use an existing channel in your server?")
                        .setColor(EMBED_COLOR);
        MessageCreateBuilder messageCreateBuilder =
                new MessageCreateBuilder()
                        .addActionRow(
                                Button.success(
                                        this.getName() + ":createnewchannel", "Create New Channel"),
                                Button.primary(
                                        this.getName() + ":useexistingchannel",
                                        "Use Existing Channel"))
                        .setEmbeds(embedBuilder.build());

        // Send the message to the owner
        owner.openPrivateChannel().complete().sendMessage(messageCreateBuilder.build()).queue();

        // For each member, set the GuildId, ask their location, add them to collection
        MessageCreateBuilder stateSelections = location.createStatesMessageBuilder();
        for (Member member : event.getGuild().getMembers()) {
            userController.setGuildIdForUser(member.getId(), event.getGuild().getId());
            // Makes sure member is not the bot itself
            if (!member.getId().equals(event.getJDA().getSelfUser().getId())) {
                member.getUser()
                        .openPrivateChannel()
                        .complete()
                        .sendMessage(stateSelections.build())
                        .queue();
            }
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // Define the Guild owner and request the GuildId from the user collection
        User user = event.getUser();
        Guild guild =
                event.getJDA()
                        .getGuildById(
                                Objects.requireNonNull(
                                        userController.getGuildIdForUser(user.getId())));
        Objects.requireNonNull(guild);

        // Delete buttons so no longer clickable
        event.deferEdit().setComponents().queue();

        // Checks to see if creation of a new trading-channel is selected
        if ("Create New Channel".equals(event.getButton().getLabel())) {
            // Checks if a channel named trading-channel already exists on the server
            for (GuildChannel guildChannel : guild.getTextChannels()) {
                if (TRADING_CHANNEL_ID.equals(guildChannel.getName())) {
                    user.openPrivateChannel()
                            .complete()
                            .sendMessage("\"trading-channel\" already exists on your server.")
                            .queue();
                    return;
                }
            }
            // Create the new "trading-channel". Move it under Text Channels
            this.createNewTradingChannel(user, guild);
        } else {
            // Owner selects an existing channel to make trading channel
            user.openPrivateChannel()
                    .complete()
                    .sendMessage(selectFromChannelsMenu(user, guild).build())
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);
        event.deferEdit()
                .setActionRow(
                        StringSelectMenu.create(getName())
                                .setPlaceholder(response)
                                .addOption(response, response)
                                .build()
                                .withDisabled(true))
                .queue();
        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessage(
                        Objects.requireNonNull(
                                String.format(
                                        "%s has been set as the main trading channel!", response)))
                .queue();
        userController.setTradingChannel(event.getUser().getId(), response);
    }

    // Build the string select menu of existing channels to choose from
    private MessageCreateBuilder selectFromChannelsMenu(User user, Guild guild) {
        Builder menu =
                StringSelectMenu.create("newguildjoined")
                        .setPlaceholder("Select Existing Channel To Assign For Trading");
        for (GuildChannel guildChannel : guild.getTextChannels()) {
            menu.addOption(guildChannel.getName(), guildChannel.getName());
        }
        // Send an embed message with the channel dropdown
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setDescription(
                                "Select which text channel you wish to assign as your trading channel.")
                        .setColor(EMBED_COLOR);
        return new MessageCreateBuilder()
                .mention(user)
                .addActionRow(menu.build())
                .addEmbeds(embedBuilder.build());
    }

    // Create the "trading-channel" channel and add it to "Text Channels" grouping
    private void createNewTradingChannel(User user, Guild guild) {
        Category category = guild.getCategoriesByName("text channels", true).get(0);
        EnumSet<Permission> deny =
                EnumSet.of(
                        Permission.MESSAGE_SEND,
                        Permission.CREATE_PRIVATE_THREADS,
                        Permission.MESSAGE_MANAGE,
                        Permission.MANAGE_THREADS);
        TextChannel textChannel =
                category.createTextChannel(TRADING_CHANNEL_ID)
                        .addPermissionOverride(guild.getPublicRole(), null, deny)
                        .complete();
        textChannel.getManager().setParent(category);
        user.openPrivateChannel()
                .complete()
                .sendMessage(
                        Objects.requireNonNull(
                                String.format(
                                        "A new channel named \"trading-channel\" has been created in your server %s.",
                                        guild.getName())))
                .queue();
        userController.setTradingChannel(guild.getOwnerId(), TRADING_CHANNEL_ID);
    }
}
