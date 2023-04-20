package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.model.Listing;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class ViewMyListingCommand implements SlashCommandHandler, ButtonHandler {

    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject ListingController listingController;
    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;
    @Inject GuildController guildController;
    @Inject JDA jda;

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

        var user = event.getUser();
        var discordUserId = user.getId();
        var discordDisplayName = user.getName();
        var guildId = event.getGuild().getId();
        var guild = guildController.getGuildByGuildId(guildId);
        var listingsMessages = getListingsMessages(discordUserId, discordDisplayName, guildId);

        if (listingsMessages.isEmpty()) {
            event.reply("No postings available").setEphemeral(true).complete();
            return;
        }

        sendListingsMessageToUser(user, listingsMessages);
        event.reply("Your postings has been sent to your DM").setEphemeral(true).complete();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        var userId = event.getUser().getId();
        var buttonEvent = event.deferEdit().setComponents();
        var buttonIds = event.getButton().getId().split(":");
        var objectId = new ObjectId(buttonIds[1]);
        var messageId = buttonIds[2];
        var guildId = buttonIds[3];
        var guild = guildController.getGuildByGuildId(guildId);

        var channel = jda.getGuildById(guildId).getTextChannelById(guild.getTradingChannelId());
        deleteListingMessages(channel, objectId, messageId, guildId, userId);

        buttonEvent
                .setEmbeds(
                        new EmbedBuilder()
                                .setDescription("Your post has been successfully deleted")
                                .setColor(EMBED_COLOR)
                                .build())
                .queue();
    }



    /**
     * Deletes listing in discord and in MongodDB
     *
     * @param channel - The channel where the message is stored.
     * @param objectid - The objectid of the listing in the database.
     * @param messageId - The messageId of the listing in the channel.
     */
    private void deleteListingMessages(
            MessageChannel channel, @Nonnull ObjectId objectid, @Nonnull String messageId, String guildId, @Nonnull String discordMemberId) {
        listingController.deleteListingById(objectid, discordMemberId, guildId);
        channel.deleteMessageById(messageId).queue();
    }

    /**
     * Sends the listing messages to user's DM
     *
     * @param user - The user who intiated the command.
     * @param listingsMessages - The user's listings in message format.
     */
    private void sendListingsMessageToUser(User user, List<MessageCreateBuilder> listingsMessages) {
        for (MessageCreateBuilder message : listingsMessages) {
            messageBuilder.sendPrivateMessage(user, message.build());
        }
    }

    /**
     * Retrieves all listings in message format from the user.
     *
     * @param discordUserId - The user's id in discord.
     * @param discordDisplayName - The user's display name in discord.
     * @return List<MessageCreateBuilder>
     */
    private List<MessageCreateBuilder> getListingsMessages(
            String discordUserId, @Nonnull String discordDisplayName, String guildId) {
        var listing = listingController.getListingsByMemberId(discordUserId, guildId);
        List<MessageCreateBuilder> messages = new ArrayList<>();
        if (listing.isEmpty()) {
            return messages;
        }
        for (Listing list : listing) {
            var button = Button.danger(
                String.format(
                        "%s:%s:%s:%s:delete",
                        getName(), list.getId(), list.getMessageId(), list.getGuildId()),
                "Delete");
            var messageCreateBuilder =
                    new MessageCreateBuilder()
                            .addActionRow(button)
                            .setEmbeds(messageBuilder.toMessageEmbed(list, discordDisplayName));
            messages.add(messageCreateBuilder);
        }
        return messages;
    }
}
