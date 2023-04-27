package edu.northeastern.cs5500.starterbot.discord.commands;

import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * This class represents the command to search listings with a keyword and sort the result with
 * price or posted date in asending or descending order.
 */
@Singleton
@Slf4j
public class SearchListingsCommand implements SlashCommandHandler, StringSelectHandler {

    @Inject ListingController listingController;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject JDA jda;

    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final String ASCENDING = "Ascending";
    private static final String DESCENDING = "Descending";
    private static final String PRICE = "Price";
    private static final String DATE = "Date";
    private static final String NONE = "None";

    @Inject
    public SearchListingsCommand() {
        // Defined public and empty for Dagger injection
    }

    /**
     * Returns the name of the command.
     *
     * @return The name of the command.
     */
    @Override
    @Nonnull
    public String getName() {
        return "searchlistings";
    }

    /**
     * Returns the CommandData object representing the command and its options.
     *
     * @return The CommandData object representing the command and its options.
     */
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

    /**
     * Handles the SlashCommandInteractionEvent event by creating a Select Menu to sort the search
     * results. Sends the Select Menu as an ephemeral reply to the user.
     *
     * @param event The SlashCommandInteractionEvent event.
     */
    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
            throws GuildNotFoundException {
        log.info("event: /searchlistings");

        var keyword = Objects.requireNonNull(event.getOption("keyword")).getAsString();
        var guild = event.getGuild();

        if (guild == null) {
            throw new GuildNotFoundException("Event has no guild.");
        }

        var guildId = guild.getId();

        var sortingOptionSelectMenu = createSortingOptionSelectMenu(keyword, guildId);
        event.reply(sortingOptionSelectMenu.build()).setEphemeral(true).queue();
    }

    /**
     * Searches for listings with the given keyword in the given guild.
     *
     * @param keyword The keyword to search for.
     * @param guildId The ID of the guild to search in.
     * @return A list of listings that match the search criteria.
     */
    private List<Listing> searchListings(@Nonnull String keyword, @Nonnull String guildId) {

        return new ArrayList<>(listingController.getListingsWithKeyword(keyword, guildId));
    }

    /**
     * Sends a list of listings to a user via private message.
     *
     * @param user the user to whom the message will be sent
     * @param listings the list of listings to be sent
     */
    private void sendListingsMessageToUser(@Nonnull User user, @Nonnull List<Listing> listings) {

        for (Listing list : listings) {
            var message =
                    new MessageCreateBuilder()
                            .setEmbeds(messageBuilder.toMessageEmbed(list, user.getName()))
                            .build();
            messageBuilder.sendPrivateMessage(user, message);
        }
    }

    /**
     * Builds a message embed to confirm a listing.
     *
     * @param message the message to be displayed in the embed
     * @return the message embed
     */
    private MessageEmbed buildConfirmationEmbed(@Nonnull String message) {
        return new EmbedBuilder().setDescription(message).setColor(EMBED_COLOR).build();
    }

    /**
     * Converts the cost of a listing to a float value.
     *
     * @param listing the listing to convert
     * @return the cost of the listing as a float value
     */
    private Float getListingCostAsFloat(@Nonnull Listing listing) {
        return Float.valueOf(listing.getFields().getCost().split(" ")[1]);
    }

    /**
     * Converts the posted date of a listing to a LocalDateTime object.
     *
     * @param listing the listing to convert
     * @return the posted date of the listing as a LocalDateTime object
     */
    private LocalDateTime getListingPostedDateAsLocalDateTime(@Nonnull Listing listing) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        return LocalDateTime.parse(listing.getFields().getDatePosted(), formatter);
    }

    /**
     * Called when a user selects an option from a StringSelectMenu component. Sort the listings
     * depends on the user's choices and DM the listings.
     *
     * @param event the StringSelectInteractionEvent containing information about the user's
     *     selection
     */
    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {

        var buttonId = Objects.requireNonNull(event.getComponentId());
        var handlerName = buttonId.split(":", 5)[1];
        var keyword = buttonId.split(":", 5)[2];
        var guildId = buttonId.split(":", 5)[3];
        var choice = buttonId.split(":", 5)[4];

        var selectedChoice = event.getInteraction().getValues().get(0);
        var user = event.getUser();

        List<Listing> listings = searchListings(keyword, guildId);

        if ("SortOption".equals(handlerName)) {

            if (listings.isEmpty()) {
                event.reply("No listings available").setEphemeral(true).complete();
                return;
            }

            if (NONE.equals(selectedChoice)) {
                sendListingsMessageToUser(user, listings);
                var embedWithoutSorting =
                        buildConfirmationEmbed("The listings are sent to your DM.");
                event.deferEdit().setComponents().setEmbeds(embedWithoutSorting).complete();

            } else {
                var sortingOrderSelectMenu =
                        createSortingOrderSelectMenu(keyword, guildId, selectedChoice);
                event.deferEdit().setComponents(sortingOrderSelectMenu.getComponents()).complete();
            }

        } else {

            if (PRICE.equals(choice)) {
                Collections.sort(
                        listings,
                        (l1, l2) -> getListingCostAsFloat(l1).compareTo(getListingCostAsFloat(l2)));
            } else {
                Collections.sort(
                        listings,
                        (l1, l2) ->
                                getListingPostedDateAsLocalDateTime(l1)
                                        .compareTo(getListingPostedDateAsLocalDateTime(l1)));
            }

            if (DESCENDING.equals(selectedChoice)) {
                Collections.reverse(listings);
            }

            sendListingsMessageToUser(user, listings);

            String message =
                    String.format(
                            "The listings sorted by %s in %s order are sent to your DM.",
                            choice, selectedChoice);
            event.deferEdit().setComponents().setEmbeds(buildConfirmationEmbed(message)).complete();
        }
    }

    /**
     * Creates a StringSelectMenu component to allow the user to select price, date or none to sort
     * the listings.
     *
     * @param keyword the search keyword used to find the listings
     * @param guildId the ID of the guild in which the search was performed
     * @return a MessageCreateBuilder containing the StringSelectMenu component
     */
    @Nonnull
    private MessageCreateBuilder createSortingOptionSelectMenu(
            @Nonnull String keyword, @Nonnull String guildId) {
        var sortingOptionSelectMenu =
                StringSelectMenu.create(
                                getName() + ":SortOption:" + keyword + ":" + guildId + ":NoChoice")
                        .setPlaceholder("Sort the listing by:")
                        .addOption(PRICE, PRICE)
                        .addOption(DATE, DATE)
                        .addOption(NONE, NONE);
        return new MessageCreateBuilder().addActionRow(sortingOptionSelectMenu.build());
    }

    /**
     * Creates a StringSelectMenu component to allow the user to select a sorting
     * order(ascending/descending) for the listings.
     *
     * @param keyword the search keyword used to find the listings
     * @param guildId the ID of the guild in which the search was performed
     * @param choice the sorting option chosen by the user
     * @return a MessageCreateBuilder containing the StringSelectMenu component
     */
    @Nonnull
    private MessageCreateBuilder createSortingOrderSelectMenu(
            @Nonnull String keyword, @Nonnull String guildId, @Nonnull String choice) {
        var sortingOrderSelectMenu =
                StringSelectMenu.create(
                                getName() + ":SortOrder:" + keyword + ":" + guildId + ":" + choice)
                        .setPlaceholder("In which order?")
                        .addOption(ASCENDING, ASCENDING)
                        .addOption(DESCENDING, DESCENDING);
        return new MessageCreateBuilder().addActionRow(sortingOrderSelectMenu.build());
    }
}
