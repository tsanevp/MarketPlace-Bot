package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.model.Listing;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class ViewMyListingCommand implements SlashCommandHandler, ButtonHandler {

    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject ListingController listingController;
    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;

    @Inject
    public ViewMyListingCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "viewmylisting";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "View all the listings that you have posted");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /viewmylisting");

        User user = event.getUser();
        var discordUserId = user.getId();
        var discordDisplayName = user.getName();
        List<MessageCreateBuilder> listingsMessages =
                getListingsMessages(discordUserId, discordDisplayName);
        if (listingsMessages.isEmpty()) {
            event.reply("No postings available").setEphemeral(true).complete();
            return;
        }
        sendListingsMessageToUser(user, listingsMessages);
        event.reply("Your postings has been sent to your DM").setEphemeral(true).complete();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        User user = event.getUser();
        var userId = user.getId();
        var guildId = Objects.requireNonNull(userController.getGuildIdForUser(userId));
        Guild guild = Objects.requireNonNull(event.getJDA().getGuildById(guildId));
        MessageChannel channel =
                guild.getTextChannelById(userController.getTradingChannelId(userId));
        MessageEditCallbackAction buttonEvent = event.deferEdit().setComponents();
        String[] buttonIds = event.getButton().getId().split(":");
        deleteListingMessages(user, channel, new ObjectId(buttonIds[2]), buttonIds[1]);

        buttonEvent
                .setEmbeds(
                        new EmbedBuilder()
                                .setDescription("Your post has been successfully deleted")
                                .setColor(EMBED_COLOR)
                                .build())
                .queue();
    }

    private void deleteListingMessages(
            User user,
            MessageChannel channel,
            @Nonnull ObjectId objectid,
            @Nonnull String buttonIds) {
        listingController.deleteListingById(objectid);
        channel.deleteMessageById(buttonIds).queue();
    }

    private void sendListingsMessageToUser(User user, List<MessageCreateBuilder> listingsMessages) {
        for (MessageCreateBuilder message : listingsMessages) {
            messageBuilder.sendPrivateMessage(user, message.build());
        }
    }

    private List<MessageCreateBuilder> getListingsMessages(
            String discordUserId, @Nonnull String discordDisplayName) {
        Collection<Listing> listing = listingController.getListingsByMemberId(discordUserId);
        List<MessageCreateBuilder> messages = new ArrayList<>();
        if (listing.isEmpty()) {
            return messages;
        }
        for (Listing list : listing) {
            String buttonId =
                    String.format("%s:%s:%X:delete", getName(), list.getMessageId(), list.getId());
            MessageCreateBuilder messageCreateBuilder =
                    new MessageCreateBuilder()
                            .addActionRow(Button.danger(buttonId, "Delete"))
                            .setEmbeds(messageBuilder.toMessageEmbed(list, discordDisplayName));
            messages.add(messageCreateBuilder);
        }
        return messages;
    }
}
