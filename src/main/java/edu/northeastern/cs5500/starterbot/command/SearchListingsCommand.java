package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.exceptions.ChannelNotFoundException;
import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
import edu.northeastern.cs5500.starterbot.model.Listing;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class SearchListingsCommand implements SlashCommandHandler,  {

    @Inject ListingController listingController;
    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;
    @Inject GuildController guildController;
    @Inject JDA jda;

    @Inject
    public SearchListingsCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "searchlistings";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "View all the listings that you have posted")
                    .addOption(OptionType.STRING, "keyword",
                        "Please provide the keyword for the search", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /searchlistings");

        var keyword = Objects.requireNonNull(event.getOption("keyword")).getAsString();
        var user = event.getUser();
        var discordUserId = user.getId();
        var discordDisplayName = user.getName();
        var guildId = event.getGuild().getId();

        var listingsMessages = getListingsMessagesWithKeyword(keyword, discordUserId, discordDisplayName, guildId);

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
     * @return List<MessageCreateBuilder>
     */
    @Nonnull
    private List<MessageCreateData> getListingsMessagesWithKeyword(
            @Nonnull String keyword,
            @Nonnull String discordUserId,
            @Nonnull String discordDisplayName,
            @Nonnull String guildId) {
        var listing = listingController.getListingsWithKeyword(keyword, guildId);
        List<MessageCreateData> messages = new ArrayList<>();

        if (listing.isEmpty()) {
            return messages;
        }

        for (Listing list : listing) {
            var messageCreateData =
                    new MessageCreateBuilder()
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
    private void sendListingsMessageToUser(User user, List<MessageCreateData> listingsMessages) {
        for (MessageCreateData message : listingsMessages) {
            messageBuilder.sendPrivateMessage(user, message);
        }
    }

    /**
     * Retrieves the trading channel where the listing is located.
     *
     * @param guildId - the id of the guild where the listing is located.
     * @return - The trading channel
     * @throws GuildNotFoundException - guild was not found in JDA.
     * @throws ChannelNotFoundException - text channel was not found in JDA.
     */
    MessageChannel getTradingChannel(@Nonnull String guildId)
            throws GuildNotFoundException, ChannelNotFoundException {
        var guild = jda.getGuildById(guildId);

        if (guild == null) {
            guildController.removeGuildByGuildId(guildId);
            throw new GuildNotFoundException("Guild ID no longer exists in JDA.");
        }

        var tradingChannelId = guildController.getGuildByGuildId(guildId).getTradingChannelId();
        var channel = guild.getTextChannelById(tradingChannelId);

        if (channel == null) {
            guildController.setTradingChannelId(guildId, null);
            throw new ChannelNotFoundException(
                    "Trading channel ID no longer exists in the specified guild in JDA.");
        }
        return channel;
    }
}
