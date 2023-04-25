package edu.northeastern.cs5500.starterbot.discord.commands;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class CreateListingCommand implements SlashCommandHandler, ButtonHandler {
    private static final String CURRENCY_USED = "USD";
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject UserController userController;
    @Inject ListingController listingController;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject GuildController guildController;

    @Inject
    public CreateListingCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "createlisting";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(
                        getName(),
                        "Please input the following information to complete your listing")
                .addOption(
                        OptionType.STRING,
                        "title",
                        "Please provide the title of your listing",
                        true)
                .addOption(
                        OptionType.INTEGER,
                        "item_cost",
                        "How much do you wish to sell your item for?",
                        true)
                .addOption(
                        OptionType.BOOLEAN,
                        "shipping_included",
                        "Is the price of shipping included in your cost?",
                        true)
                .addOption(
                        OptionType.STRING,
                        "description",
                        "Please provide a decription of the item you wish to sell",
                        true)
                .addOption(
                        OptionType.STRING,
                        "condition",
                        "What condition is the item in: NEW, LIKE NEW, GOOD, USED, FOR PARTS",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image1",
                        "Please uplaod Image 1 of the item you wish to sell. *Required*",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image2",
                        "Please uplaod Image 2 of the item you wish to sell. *Optional*",
                        false)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image3",
                        "Please uplaod Image 3 of the item you wish to sell. *Optional*",
                        false)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image4",
                        "Please uplaod Image 4 of the item you wish to sell. *Optional*",
                        false)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image5",
                        "Please uplaod Image 5 of the item you wish to sell. *Optional*",
                        false)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image6",
                        "Please uplaod Image 6 of the item you wish to sell. *Optional*",
                        false);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /createlisting");

        // Retrieve user input
        var title = Objects.requireNonNull(event.getOption("title")).getAsString();
        var cost = Objects.requireNonNull(event.getOption("item_cost")).getAsInt();
        var shippingIncluded =
                Objects.requireNonNull(event.getOption("shipping_included")).getAsBoolean();
        var condition = Objects.requireNonNull(event.getOption("condition")).getAsString();
        var description = Objects.requireNonNull(event.getOption("description")).getAsString();

        // Get guild and user data
        var guildId = Objects.requireNonNull(event.getGuild()).getId();
        var userId = event.getUser().getId();
        var discordDisplayName = event.getUser().getName();

        // For all images attached by the user, store their urls in a list
        List<String> imageURLs = new ArrayList<>();
        for (OptionMapping image : event.getOptionsByType(OptionType.ATTACHMENT)) {
            imageURLs.add(image.getAsAttachment().getUrl());
        }

        // Create ListingFields and Listing objects
        var listingFields = buildListingFields(cost, shippingIncluded, condition, description);
        var listing = buildListing(title, guildId, userId, imageURLs, listingFields);

        // Temporarily store the listing in MongoDB
        userController.setCurrentListing(userId, listing);

        // Create listing confirmation message
        var listingAsEmbed = messageBuilder.toMessageEmbed(listing, discordDisplayName);
        var listingConfirmation = createListingConfirmationMessage(listingAsEmbed);

        // Send listing confirmation to the user
        event.reply(listingConfirmation).setEphemeral(true).queue();
    }

    /**
     * Build and returns a ListingFields object given its variables.
     *
     * @param cost - The cost of the item being sold.
     * @param shippingIncluded - Whether shipping is included in the cost.
     * @param condition - The condition of the item being sold.
     * @param description - A description of the item being sold.
     * @return a ListingFields object.
     */
    @Nonnull
    @VisibleForTesting
    ListingFields buildListingFields(
            int cost,
            boolean shippingIncluded,
            @Nonnull String condition,
            @Nonnull String description) {
        var datePosted = getDatePosted();
        var costValue = reformatCostValue(cost);

        // Create ListingFields Object
        var listingFields =
                ListingFields.builder()
                        .cost(costValue)
                        .shippingIncluded(shippingIncluded)
                        .condition(condition)
                        .description(description)
                        .datePosted(datePosted)
                        .build();

        Objects.requireNonNull(listingFields);
        return listingFields;
    }

    /**
     * Build and returns a Listing object given its variables.
     *
     * @param title - The title of the listing.
     * @param guildId - The id of the guild the listing was created in.
     * @param userId - The id of the user who created the listing.
     * @param imageURLs - A list of image urls. These are the images of the item.
     * @param listingFields - A ListingFields object that holds data of each listing field.
     * @return a Listing object.
     */
    @Nonnull
    @VisibleForTesting
    Listing buildListing(
            @Nonnull String title,
            @Nonnull String guildId,
            @Nonnull String userId,
            @Nonnull List<String> imageURLs,
            @Nonnull ListingFields listingFields) {
        var titleReformatted = reformatListingTitle(userId, title);
        var url = Objects.requireNonNull(imageURLs.get(0));

        // Create Listing Object
        var listing =
                Listing.builder()
                        .messageId(0)
                        .discordUserId(userId)
                        .guildId(guildId)
                        .title(titleReformatted)
                        .url(url)
                        .images(imageURLs)
                        .fields(listingFields)
                        .build();
        Objects.requireNonNull(listing);

        return listing;
    }

    /**
     * Create and return the confirmation message to send the user asking if the user wants to post,
     * edit, or delete the listing.
     *
     * @param discordDisplayName - The display name of the user who created the listing.
     * @param listing - The listing object that was created from the user input.
     * @return the confirmation message to send the user asking if the user wants to post, edit, or
     *     delete the listing.
     */
    @Nonnull
    @VisibleForTesting
    MessageCreateData createListingConfirmationMessage(
            @Nonnull List<MessageEmbed> listingAsMessageEmbed) {

        var postButton = Button.success(getName() + ":ok", "Post");
        var editButton = Button.primary(getName() + ":edit", "Edit");
        var cancelButton = Button.danger(getName() + ":cancel", "Cancel");

        return new MessageCreateBuilder()
                .addActionRow(postButton, editButton, cancelButton)
                .setEmbeds(listingAsMessageEmbed)
                .build();
    }

    /**
     * Method to reformat the Listing title to include the city and state the user is located in.
     *
     * @param userId - The userId of the member who posted the listing.
     * @param title - The original title of the listing.
     * @return the title of the listing with the city and state added to it.
     */
    @Nonnull
    private String reformatListingTitle(@Nonnull String userId, @Nonnull String title) {
        try {
            return Objects.requireNonNull(
                    String.format(
                            "[%s, %s]%s",
                            userController.getCityOfResidence(userId),
                            userController.getStateOfResidence(userId),
                            title));
        } catch (Exception e) {
            log.info("User has not set their city or state of residence");
        }
        return title;
    }

    /**
     * Method to get the date and time the listing was posted.
     *
     * @return the date and time the listing was posted.
     */
    @Nonnull
    @VisibleForTesting
    String getDatePosted() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        var currentdateTime = LocalDateTime.now();
        return Objects.requireNonNull(dateTimeFormatter.format(currentdateTime));
    }

    /**
     * Method to reformat the cost value to include the currency being used.
     *
     * @param cost - The monetary cost of the item being sold.
     * @param shippingIncluded - A boolean indicating whether shipping costs are included in the
     *     item's price.
     * @return a list where the first index is the Cost field name reformatted, and the second is
     *     the price reformatted.
     */
    @Nonnull
    @VisibleForTesting
    String reformatCostValue(int cost) {
        // Reformat the price to include the currency being used
        return Objects.requireNonNull(String.format("%s %s", CURRENCY_USED, cost));
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        var user = event.getUser();
        var userId = user.getId();

        // Remove the buttons so they are no longer clickable
        var buttonEvent = event.deferEdit().setComponents();
        var currentListing = userController.getCurrentListing(userId);
        var buttonLabel = event.getButton().getLabel();

        if (currentListing == null) {
            listingNotFoundOrIsNull(buttonEvent);
            return;
        }

        if ("Post".equals(buttonLabel)) {
            var guild = Objects.requireNonNull(event.getGuild());

            postAndSaveListing(user, buttonEvent, currentListing, guild);
        } else if ("Edit".equals(buttonLabel)) {
            sendEditListingInstructions(buttonEvent, currentListing);
        } else {
            cancelListing(buttonEvent);
        }

        // Set all the temporary listing back to null. User can only have one at a time
        userController.setCurrentListing(userId, null);
    }

    /**
     * Lets the user know that their listing could not be found. The user must resubmit their
     * listing in this case.
     *
     * @param buttonEvent - The button event.
     */
    private void listingNotFoundOrIsNull(@Nonnull MessageEditCallbackAction buttonEvent) {
        var listingNotFound =
                "Sorry, there was an error finding your listing. Please resubmit the listing!";
        var listingNotFoundEmbed =
                new EmbedBuilder().setDescription(listingNotFound).setColor(EMBED_COLOR).build();
        buttonEvent.setEmbeds(listingNotFoundEmbed).queue();
    }

    /**
     * Sends the listing to the trading channel and save it in the Listing collection.
     *
     * @param user - The user who is posting the listing.
     * @param buttonEvent - The button event.
     * @param currentListing - The listing the user is going to post.
     * @param guild - The guild the user is going to post the listing in.
     */
    private void postAndSaveListing(
            @Nonnull User user,
            @Nonnull MessageEditCallbackAction buttonEvent,
            @Nonnull Listing currentListing,
            @Nonnull Guild guild) {
        var guildObject = guildController.getGuildByGuildId(guild.getId());
        var tradingChannelId = Objects.requireNonNull(guildObject.getTradingChannelId());
        var textChannel = Objects.requireNonNull(guild.getTextChannelById(tradingChannelId));
        var embedToPost = messageBuilder.toMessageEmbed(currentListing, user.getName());
        var embedAsmesMessageCreateData = new MessageCreateBuilder().setEmbeds(embedToPost).build();

        // Send the listing to the "trading-channel"
        textChannel
                .sendMessage(embedAsmesMessageCreateData)
                .queue(
                        message -> {
                            // Set the message id and store the listing in the collection
                            currentListing.setMessageId(message.getIdLong());
                            listingController.addListing(currentListing);
                        });

        var successMessage =
                String.format(
                        "Your listing has been posted to the following text channel: %s!",
                        textChannel.getName());

        var successEmbed =
                new EmbedBuilder().setDescription(successMessage).setColor(EMBED_COLOR).build();

        // Replace the listing message embed with a success message embed
        buttonEvent.setEmbeds(successEmbed).queue();
    }

    /**
     * Sends the edit string back to the user with instructions on how to resubmit the listing.
     *
     * @param buttonEvent - The button event.
     * @param currentListing - The listing the user is going to post.
     */
    private void sendEditListingInstructions(
            @Nonnull MessageEditCallbackAction buttonEvent, @Nonnull Listing currentListing) {
        // Replace listing embed with instructions on how to create a new one
        var editMessage =
                String.format(
                        "To Edit your listing, COPY & PASTE the following to your message line. This will auto-fill each section BUT will not reattach your images. %n%n%s",
                        createListingCommandAsString(currentListing));
        var editEmbed =
                new EmbedBuilder().setDescription(editMessage).setColor(EMBED_COLOR).build();
        buttonEvent.setEmbeds(editEmbed).queue();
    }

    /**
     * Sends a message to the user confirming the creation of the listing has been cancelled.
     *
     * @param buttonEvent - The button event.
     */
    private void cancelListing(@Nonnull MessageEditCallbackAction buttonEvent) {
        // Replace listing message embed with cancellation message, delete buttons
        var messageEmbed =
                new EmbedBuilder()
                        .setDescription("The creation of you lisitng has been canceled.")
                        .setColor(EMBED_COLOR)
                        .build();
        buttonEvent.setEmbeds(messageEmbed).queue();
    }

    /**
     * Method to build the /command input the user gave when calling /createlisting.
     *
     * @param currentListing - The current listing the user is working on.
     * @return the command input the user entered as a string.
     */
    @Nonnull
    @VisibleForTesting
    String createListingCommandAsString(@Nonnull Listing currentListing) {
        var fields = currentListing.getFields();
        var cost = fields.getCost().replace(CURRENCY_USED, "");
        var title = currentListing.getTitle();
        if (title.contains("]")) {
            title = title.split("]")[1];
        }

        return Objects.requireNonNull(
                String.format(
                        "/createlisting title: %s item_cost: %s shipping_included: %s description: %s condition: %s image1: [attachment]",
                        title,
                        cost,
                        fields.getShippingIncluded(),
                        fields.getDescription(),
                        fields.getCondition()));
    }
}
