package edu.northeastern.cs5500.starterbot.discord.commands;

import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
import edu.northeastern.cs5500.starterbot.model.Listing;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnegative;
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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * This class represents the command to search listings with a keyword and sort the result with
 * price or posted date in asending or descending order.
 */
@Singleton
@Slf4j
public class SearchListingsCommand implements SlashCommandHandler, StringSelectHandler {

    @Inject JDA jda;
    @Inject ListingController listingController;
    @Inject MessageBuilderHelper messageBuilder;

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
                        "Please provide the keyword to filter your search by",
                        true);
    }

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

        List<Listing> listings = searchListings(keyword, guildId);
        if (listings.isEmpty()) {
            event.reply("No listings available").setEphemeral(true).complete();
            return;
        }

        var sortingOptionSelectMenu = createSortingOptionSelectMenu(keyword, guildId);
        event.reply(sortingOptionSelectMenu).setEphemeral(true).queue();
    }

    /**
     * Searches for listings with the given keyword in the given guild.
     *
     * @param keyword - The keyword to search for.
     * @param guildId - The ID of the guild to search in.
     * @return A list of listings that match the search criteria.
     */
    @Nonnull
    private List<Listing> searchListings(@Nonnull String keyword, @Nonnull String guildId) {
        return new ArrayList<>(listingController.getListingsWithKeyword(keyword, guildId));
    }

    /**
     * Sends a list of listings to a user via private message.
     *
     * @param user - The user to whom the message will be sent.
     * @param listings - The list of listings to be sent.
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
     * @param message - The message to be displayed in the embed.
     * @return The message embed.
     */
    @Nonnull
    private MessageEmbed buildConfirmationEmbed(@Nonnull String message) {
        return new EmbedBuilder().setDescription(message).setColor(EMBED_COLOR).build();
    }

    /**
     * Converts the cost of a listing to a float value.
     *
     * @param listing - The listing to convert.
     * @return The cost of the listing as a float value.
     */
    @Nonnegative
    private Float getListingCostAsFloat(@Nonnull Listing listing) {
        return Float.valueOf(listing.getFields().getCost().split(" ")[1]);
    }

    /**
     * Converts the posted date of a listing to a LocalDateTime object.
     *
     * @param listing - The listing to convert.
     * @return The posted date of the listing as a LocalDateTime object.
     * @throws DateTimeException If there was an error reformatting the date.
     */
    @Nonnull
    private LocalDateTime getListingPostedDateAsLocalDateTime(@Nonnull Listing listing)
            throws DateTimeException {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        var dateReformatted = LocalDateTime.parse(listing.getFields().getDatePosted(), formatter);

        if (dateReformatted == null) {
            throw new DateTimeException("There was an error refortmatting the date.");
        }

        return dateReformatted;
    }

    /**
     * Called when a user selects an option from a StringSelectMenu component. Sort the listings
     * depends on the user's choices and DM the listings.
     *
     * @param event - The StringSelectInteractionEvent containing information about the user's
     *     selection.
     * @throws IllegalStateException If a string formatted or obtained is null.
     */
    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event)
            throws IllegalStateException {
        var user = event.getUser();
        var menuId = event.getComponentId();
        var handlerName = menuId.split(":", 5)[1];

        // Verify string values parsed from menu id are not null
        var keyword = menuId.split(":", 5)[2];
        var guildId = menuId.split(":", 5)[3];
        var choice = menuId.split(":", 5)[4];
        if (keyword == null || guildId == null || choice == null) {
            throw new IllegalStateException("Menu ID could not be split and indexed.");
        }

        // Verify the selected menu option is not null
        var selectedChoice = event.getInteraction().getValues().get(0);
        if (selectedChoice == null) {
            throw new IllegalStateException("Selected menu choice could not be accessed.");
        }

        List<Listing> listings = searchListings(keyword, guildId);

        if ("SortOption".equals(handlerName)) {
            if (NONE.equals(selectedChoice)) {
                sendListingsMessageToUser(user, listings);
                var embedWithoutSorting =
                        buildConfirmationEmbed("Search results are sent to your DM.");
                event.deferEdit().setComponents().setEmbeds(embedWithoutSorting).complete();
                return;
            }

            var sortingOrderSelectMenu =
                    createSortingOrderSelectMenu(keyword, guildId, selectedChoice);
            event.deferEdit().setComponents(ActionRow.of(sortingOrderSelectMenu)).complete();
        } else {
            // Options to sort by
            if (PRICE.equals(choice)) {
                sortListingsByPrice(listings);
            } else {
                sortListingsByDateTime(listings);
            }

            // Sort in decending order, if selected
            if (DESCENDING.equals(selectedChoice)) {
                Collections.reverse(listings);
            }

            // Send sorted listing to user dms
            sendListingsMessageToUser(user, listings);

            // Create and send message to user that sorting was successful
            String message =
                    String.format(
                            "Search result sorted by %s in %s order are sent to your DM.",
                            choice, selectedChoice);
            if (message == null) {
                throw new IllegalStateException("Success message could not be properly formatted.");
            }

            event.deferEdit().setComponents().setEmbeds(buildConfirmationEmbed(message)).complete();
        }
    }

    /**
     * Sorts the List<Listings> by price in ascending order. Has null checks intergrated into the
     * sort method.
     *
     * @param listings - The list of listings to sort in place.
     */
    private void sortListingsByPrice(List<Listing> listings) {
        Collections.sort(
                listings,
                (l1, l2) -> {
                    if (l1 == null || l2 == null) {
                        return -1;
                    }
                    return getListingCostAsFloat(l1).compareTo(getListingCostAsFloat(l2));
                });
    }

    /**
     * Sorts the List<Listings> by DateTime in ascending order. Has null checks intergrated into the
     * sort method.
     *
     * @param listings - The list of listings to sort in place.
     */
    private void sortListingsByDateTime(List<Listing> listings) {
        Collections.sort(
                listings,
                (l1, l2) -> {
                    if (l1 == null || l2 == null) {
                        return -1;
                    }
                    return getListingPostedDateAsLocalDateTime(l1)
                            .compareTo(getListingPostedDateAsLocalDateTime(l1));
                });
    }

    /**
     * Creates a StringSelectMenu component to allow the user to select price, date or none to sort
     * the listings.
     *
     * @param keyword - The search keyword used to find the listings.
     * @param guildId - The ID of the guild in which the search was performed.
     * @return A MessageCreateData containing the StringSelectMenu component.
     */
    @Nonnull
    private MessageCreateData createSortingOptionSelectMenu(
            @Nonnull String keyword, @Nonnull String guildId) {
        var sortingOptionSelectMenu =
                StringSelectMenu.create(
                                getName() + ":SortOption:" + keyword + ":" + guildId + ":NoChoice")
                        .setPlaceholder("Sort the listing by:")
                        .addOption(PRICE, PRICE)
                        .addOption(DATE, DATE)
                        .addOption(NONE, NONE);
        return new MessageCreateBuilder().addActionRow(sortingOptionSelectMenu.build()).build();
    }

    /**
     * Creates a StringSelectMenu component to allow the user to select a sorting
     * order(ascending/descending) for the listings.
     *
     * @param keyword - The search keyword used to find the listings.
     * @param guildId - The ID of the guild in which the search was performed.
     * @param choice - The sorting option chosen by the user.
     * @return The StringSelectMenu containing the sorting order selections.
     */
    @Nonnull
    private StringSelectMenu createSortingOrderSelectMenu(
            @Nonnull String keyword, @Nonnull String guildId, @Nonnull String choice) {
        var sortingOrderSelectMenu =
                StringSelectMenu.create(
                                getName() + ":SortOrder:" + keyword + ":" + guildId + ":" + choice)
                        .setPlaceholder("In which order?")
                        .addOption(ASCENDING, ASCENDING)
                        .addOption(DESCENDING, DESCENDING);
        return sortingOrderSelectMenu.build();
    }
}
