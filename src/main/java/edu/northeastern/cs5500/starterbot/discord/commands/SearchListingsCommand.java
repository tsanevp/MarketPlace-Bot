package edu.northeastern.cs5500.starterbot.discord.commands;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.model.Listing;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class SearchListingsCommand implements SlashCommandHandler, StringSelectHandler {

    @Inject ListingController listingController;
    @Inject UserController userController;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject GuildController guildController;
    @Inject JDA jda;
    private static List<Listing> listings;
    private static String choice = "None";

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
        return Commands.slash(getName(), "Search listings with keyword")
                .addOption(
                        OptionType.STRING,
                        "keyword",
                        "Please provide the keyword for the search",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /searchlistings");
        var guildId = event.getGuild().getId();
        var user = event.getUser();

        var keyword = Objects.requireNonNull(event.getOption("keyword")).getAsString();
        listings = new ArrayList<>(listingController.getListingsWithKeyword(keyword, guildId));
        var messageCreateBuilder = createSortingOptionMessageBuilder();
        messageBuilder.sendPrivateMessage(user, messageCreateBuilder.build());
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
    private List<MessageCreateData> convertListingToMessageBuilder(
            @Nonnull String discordUserId, @Nonnull String discordDisplayName) {

        List<MessageCreateData> messages = new ArrayList<>();

        if (listings.isEmpty()) {
            return messages;
        }

        for (Listing list : listings) {
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
    private void sendListingsMessageToUser(@Nonnull User user) {

        List<MessageCreateData> listingsMessages =
                convertListingToMessageBuilder(user.getId(), user.getName());

        for (MessageCreateData message : listingsMessages) {
            messageBuilder.sendPrivateMessage(user, message);
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        var buttonId = Objects.requireNonNull(event.getComponentId());
        var handlerName = buttonId.split(":", 2)[1];
        var selectedChoice = event.getInteraction().getValues().get(0);
        var user = event.getUser();

        if (listings.isEmpty()) {
            event.reply("No listings available").setEphemeral(true).complete();
            return;
        }

        if ("SortOption".equals(handlerName)) {
            if ("None".equals(selectedChoice)) {
                event.reply("Here are the listings").setEphemeral(true).complete();
                sendListingsMessageToUser(user);
            } else {
                choice = selectedChoice;
                var messageCreateBuilder = createSortingOrderMessageBuilder();
                event.deferEdit().setComponents(messageCreateBuilder.getComponents()).queue();
            }
        } else {
            if ("Price".equals(choice)) {
                Collections.sort(
                        listings,
                        (l1, l2) ->
                                Float.valueOf(l1.getFields().getCost().split(" ")[1])
                                        .compareTo(
                                                Float.valueOf(
                                                        l2.getFields().getCost().split(" ")[1])));
                if ("Descending".equals(selectedChoice)) {
                    Collections.reverse(listings);
                }
            } else if ("Date".equals(choice)) {
                Collections.sort(
                        listings,
                        (l1, l2) ->
                                LocalDateTime.parse(
                                                l2.getFields().getDatePosted(),
                                                DateTimeFormatter.ofPattern(
                                                        "MM/dd/yyyy HH:mm:ss", Locale.ENGLISH))
                                        .compareTo(
                                                LocalDateTime.parse(
                                                        l2.getFields().getDatePosted(),
                                                        DateTimeFormatter.ofPattern(
                                                                "MM/dd/yyyy HH:mm:ss",
                                                                Locale.ENGLISH))));
                if ("Descending".equals(selectedChoice)) {
                    Collections.reverse(listings);
                }
            } else {
                return;
            }
            choice = "None";
            event.reply("Here are the listings").setEphemeral(true).complete();
            sendListingsMessageToUser(user);
        }
    }

    @Nonnull
    MessageCreateBuilder createSortingOptionMessageBuilder() {
        var menu =
                StringSelectMenu.create(getName() + ":SortOption")
                        .setPlaceholder("Sort the listing by")
                        .addOption("Price", "Price")
                        .addOption("Date", "Date")
                        .addOption("None", "None");

        return new MessageCreateBuilder().addActionRow(menu.build());
    }

    @Nonnull
    MessageCreateBuilder createSortingOrderMessageBuilder() {
        var menu =
                StringSelectMenu.create(getName() + ":SortOrder")
                        .setPlaceholder("In which order?")
                        .addOption("Ascending", "Ascending")
                        .addOption("Descending", "Descending");

        return new MessageCreateBuilder().addActionRow(menu.build());
    }
}
