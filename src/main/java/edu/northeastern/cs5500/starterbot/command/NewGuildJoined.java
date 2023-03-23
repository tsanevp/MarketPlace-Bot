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

        // Set the guildID for each existing member, this will also add each member to the
        // collection
        for (Member member : event.getGuild().getMembers()) {
            userController.setGuildIdForUser(member.getId(), event.getGuild().getId());
        }

        // Ask the guild owner whether they want to create a new trading-channel or use an existing
        // channel
        User owner = event.getGuild().getOwner().getUser();
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
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // Define the owner and pull the Guild ID from the user collection
        User user = event.getUser();
        Guild guild = event.getJDA().getGuildById(userController.getGuildIdForUser(user.getId()));

        event.deferEdit().setComponents().queue();
        // Checks to see if creation of a new trading-channel is selected
        if ("Create New Channel".equals(event.getButton().getLabel())) {
            // Checks if a channel named trading-channel already exists on the server
            for (GuildChannel guildChannel : guild.getTextChannels()) {
                if ("trading-channel".equals(guildChannel.getName())) {
                    user.openPrivateChannel()
                            .complete()
                            .sendMessage("\"trading-channel\" already exists on your server.")
                            .queue();
                    return;
                }
            }
            // Create new channel named "trading-channel" and added it to "Text Channels" grouping
            Category category = guild.getCategoriesByName("text channels", true).get(0);
            EnumSet<Permission> deny =
                    EnumSet.of(
                            Permission.MESSAGE_SEND,
                            Permission.CREATE_PRIVATE_THREADS,
                            Permission.MESSAGE_MANAGE,
                            Permission.MANAGE_THREADS);
            TextChannel textChannel =
                    category.createTextChannel("trading-channel")
                            .addPermissionOverride(guild.getPublicRole(), null, deny)
                            .complete();
            textChannel.getManager().setParent(category);
            user.openPrivateChannel()
                    .complete()
                    .sendMessage(
                            String.format(
                                    "A new channel named \"trading-channel\" has been created in your server %s.",
                                    guild.getName()))
                    .queue();
        } else {
            // Create a dropdown with all the existing channels a user can select as their trading
            // channel
            Builder menu =
                    StringSelectMenu.create(getName())
                            .setPlaceholder("Select Existing Channel To Assign For Sales");
            for (GuildChannel guildChannel : guild.getTextChannels()) {
                menu.addOption(guildChannel.getName(), guildChannel.getName());
            }
            // Send an embed message with the channel dropdown
            EmbedBuilder embedBuilder =
                    new EmbedBuilder()
                            .setDescription(
                                    "Select which text channel you wish to assign as your trading channel.")
                            .setColor(EMBED_COLOR);
            MessageCreateBuilder messageCreateBuilder =
                    new MessageCreateBuilder()
                            .mention(event.getUser())
                            .addActionRow(menu.build())
                            .addEmbeds(embedBuilder.build());
            user.openPrivateChannel().complete().sendMessage(messageCreateBuilder.build()).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        // Here, need to instead save the drop down selection in the DB collection maintaining the
        // guild information
        // The selection will be saved as the channel to POST new listing in
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
                        String.format("%s has been set as the main trading channel!", response))
                .queue();
    }
}
