package edu.northeastern.cs5500.starterbot.discord.commands;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.exceptions.ChannelNotFoundException;
import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
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
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class MyListingsCommand implements SlashCommandHandler, ButtonHandler {

    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject JDA jda;
    @Inject UserController userController;
    @Inject ListingController listingController;
    @Inject GuildController guildController;
    @Inject MessageBuilderHelper messageBuilder;

    @Inject
    public MyListingsCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "mylistings";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "View all the listings that you have posted");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
            throws GuildNotFoundException {
        log.info("event: /mylistings");

        var user = event.getUser();
        var discordUserId = user.getId();
        var discordDisplayName = user.getName();
        var guild = event.getGuild();

        if (guild == null) {
            throw new GuildNotFoundException("Event has no guild.");
        }

        var guildId = guild.getId();
        List<MessageCreateData> listingsMessages =
                getListingsMessages(discordUserId, discordDisplayName, guildId);

        if (listingsMessages.isEmpty()) {
            event.reply("No listings available").setEphemeral(true).complete();
            return;
        }

        sendListingsMessageToUser(user, listingsMessages);
        event.reply("Your listings has been sent to your DM").setEphemeral(true).complete();
    }

    /**
     * Retrieves all listings in message format from the user.
     *
     * @param discordUserId - The user's id in discord.
     * @param discordDisplayName - The user's display name in discord.
     * @param guildId - The id of the guild that the user is in.
     * @return All the listings as discord messages.
     * @throws IllegalStateException If button is null, an exception is thrown.
     */
    @Nonnull
    private List<MessageCreateData> getListingsMessages(
            @Nonnull String discordUserId,
            @Nonnull String discordDisplayName,
            @Nonnull String guildId)
            throws IllegalStateException {
        var listing = listingController.getListingsByMemberId(discordUserId, guildId);
        List<MessageCreateData> messages = new ArrayList<>();

        if (listing.isEmpty()) {
            return messages;
        }

        for (Listing list : listing) {
            var buttonId = String.format("%s:%s:delete", getName(), list.getId());
            if (buttonId == null) {
                throw new IllegalStateException("Button ID was unavailable for listings.");
            }

            var button = Button.danger(buttonId, "Delete");
            var messageCreateData =
                    new MessageCreateBuilder()
                            .addActionRow(button)
                            .setEmbeds(messageBuilder.toMessageEmbed(list, discordDisplayName))
                            .build();
            messages.add(messageCreateData);
        }
        return messages;
    }

    /**
     * Sends the listing messages to user's DM.
     *
     * @param user - The user who intiated the command.
     * @param listingsMessages - The user's listings in message format.
     */
    private void sendListingsMessageToUser(
            @Nonnull User user, @Nonnull List<MessageCreateData> listingsMessages) {
        for (MessageCreateData message : listingsMessages) {
            if (message == null) {
                continue;
            }
            messageBuilder.sendPrivateMessage(user, message);
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event)
            throws IllegalStateException {
        var userId = event.getUser().getId();

        var buttonId = event.getButton().getId();
        if (buttonId == null) {
            throw new IllegalStateException("Button event had no id");
        }

        var buttonIdSplit = buttonId.split(":");
        var buttonEvent = event.deferEdit().setComponents();

        var listing = listingController.getListingById(new ObjectId(buttonIdSplit[1]));
        if (listing == null) {
            log.error("Listing is no longer in database");
            event.reply(
                            "Listings are not updated. Please use /mylistings "
                                    + "to recieve an updated list.")
                    .queue();
            return;
        }

        try {
            onDeleteListingButtonClick(userId, listing);
        } catch (GuildNotFoundException | ChannelNotFoundException e) {
            log.error("myListing encountered an error when deleting listing", e);
            event.reply("Unable to remove listing because the channel/server no longer exists.")
                    .queue();
        }

        var deleteSuccessEmbed =
                new EmbedBuilder()
                        .setDescription("Your post has been successfully deleted")
                        .setColor(EMBED_COLOR)
                        .build();

        buttonEvent.setEmbeds(deleteSuccessEmbed).queue();
    }

    /**
     * Deletes listing in the trading channel and database when the "delete" button is clicked.
     *
     * @param userId - The id of the user.
     * @param listing - The listing to be deleted.
     * @throws GuildNotFoundException If guild was not found in JDA.
     * @throws ChannelNotFoundException If text channel was not found in JDA.
     * @throws IllegalStateException If listing was not found.
     */
    private void onDeleteListingButtonClick(@Nonnull String userId, @Nonnull Listing listing)
            throws GuildNotFoundException, ChannelNotFoundException, IllegalStateException {
        var guildId = listing.getGuildId();

        if (guildId.length() == 0) {
            throw new IllegalStateException(
                    "Guild ID was invalid when attempting to delete listing");
        }

        var channel = getTradingChannel(guildId);
        var listingId = listing.getId();

        if (listingId == null) {
            throw new IllegalStateException(
                    "Unable to delete listing ID because ID was not found.");
        }

        listingController.deleteListingById(listingId, userId);
        channel.deleteMessageById(listing.getMessageId()).queue();
    }

    /**
     * Retrieves the trading channel where the listing is located.
     *
     * @param guildId - The id of the guild where the listing is located.
     * @return The trading channel.
     * @throws GuildNotFoundException If guild was not found in JDA.
     * @throws ChannelNotFoundException If text channel was not found in JDA.
     */
    @Nonnull
    private MessageChannel getTradingChannel(@Nonnull String guildId)
            throws GuildNotFoundException, ChannelNotFoundException {
        var guild = jda.getGuildById(guildId);

        if (guild == null) {
            guildController.removeGuildByGuildId(guildId);
            throw new GuildNotFoundException("Guild ID no longer exists in JDA.");
        }

        var tradingChannelId = guildController.getTradingChannelIdByGuildId(guildId);
        var channel = guild.getTextChannelById(tradingChannelId);

        if (channel == null) {
            throw new ChannelNotFoundException(
                    "Trading channel ID no longer exists in the specified guild in JDA.");
        }

        return channel;
    }
}
