package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
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
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class CreateListingCommand implements SlashCommandHandler, ButtonHandler {
    private static final String CURRENCY_USED = "USD ";
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject UserController userController;
    @Inject ListingController listingController;
    @Inject MessageBuilder messageBuilder;

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

        var title = Objects.requireNonNull(event.getOption("title")).getAsString();
        var cost = Objects.requireNonNull(event.getOption("item_cost")).getAsInt();
        var shippingIncluded =
                Objects.requireNonNull(event.getOption("shipping_included")).getAsBoolean();
        var condition = Objects.requireNonNull(event.getOption("condition")).getAsString();
        var description = Objects.requireNonNull(event.getOption("description")).getAsString();
        var userId = event.getUser().getId();
        var discordDisplayName = event.getUser().getName();

        // For all images associated with event, store their urls in a list
        List<String> imageURLs = new ArrayList<>();
        for (OptionMapping image : event.getOptionsByType(OptionType.ATTACHMENT)) {
            imageURLs.add(image.getAsAttachment().getUrl());
        }

        var datePosted = getDatePosted();
        var titleReformatted = reformatListingTitle(userId, title);
        var costValue = reformatCostValue(cost);
        var url = Objects.requireNonNull(imageURLs.get(0));
        var guildId = event.getGuild().getId();

        // Create ListingFields Object
        var listingFields =
                ListingFields.builder()
                        .cost(costValue)
                        .shippingIncluded(shippingIncluded)
                        .condition(condition)
                        .description(description)
                        .datePosted(datePosted)
                        .build();

        // Create Listing Object
        var listing =
                Listing.builder()
                        .messageId(0)
                        .discordUserId(userId)
                        .title(titleReformatted)
                        .url(url)
                        .guildId(guildId)
                        .fields(listingFields)
                        .build();

        // Temporarily add listing to MongoDB
        userController.setCurrentListing(userId, listing);

        // Create a confirmation message. User reviews the listing and decides whether to post it
        var listingConfirmation =
                new MessageCreateBuilder()
                        .addActionRow(
                                Button.success(getName() + ":ok", "Post"),
                                Button.primary(getName() + ":edit", "Edit"),
                                Button.danger(getName() + ":cancel", "Cancel"))
                        .setEmbeds(messageBuilder.toMessageEmbed(listing, discordDisplayName))
                        .build();

        // Send listing confirmation to the user
        event.reply(listingConfirmation).setEphemeral(true).queue();
    }

    /**
     * Method to reformat the Listing title to include the city and state the user is located in.
     *
     * @param userId - The userId of the member who posted the listing.
     * @param title - The original title of the listing.
     * @return the title of the listing with the city and state added to it.
     */
    @Nonnull
    private String reformatListingTitle(String userId, String title) {
        return Objects.requireNonNull(
                String.format(
                        "[%s, %s]%s",
                        userController.getCityOfResidence(userId),
                        userController.getStateOfResidence(userId),
                        title));
    }

    /**
     * Method to get the date and time the listing was posted.
     *
     * @return the date and time the listing was posted.
     */
    @Nonnull
    private String getDatePosted() {
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
    private String reformatCostValue(int cost) {
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

        if ("Post".equals(event.getButton().getLabel())) {
            var guild = Objects.requireNonNull(event.getGuild());
            var textChannelId =
                    Objects.requireNonNull(userController.getTradingChannelId(guild.getOwnerId()));
            var textChannel = Objects.requireNonNull(guild.getTextChannelById(textChannelId));
            var embedToPost = messageBuilder.toMessageEmbed(currentListing, user.getName());
            var embedAsmesMessageCreateData =
                    new MessageCreateBuilder().setEmbeds(embedToPost).build();

            // Send the listing to the "trading-channel"
            textChannel
                    .sendMessage(embedAsmesMessageCreateData)
                    .queue(
                            message -> {
                                // Set the message id and store the listing in the collection
                                currentListing.setMessageId(message.getIdLong());
                                listingController.addListing(currentListing);
                            });

            // Replace the listing message embed with a success message
            buttonEvent
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            Objects.requireNonNull(
                                                    String.format(
                                                            "Your listing has been posted to the following text channel: %s!",
                                                            textChannel.getName())))
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
        } else if ("Edit".equals(event.getButton().getLabel())) {
            // Replace listing embed with instructions on how to create a new one
            buttonEvent
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            String.format(
                                                    "To Edit your listing, COPY & PASTE the following to your message line. This will auto-fill each section BUT will not reattach your images. %n%n%s",
                                                    createListingCommandAsString(currentListing)))
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
        } else {
            // Replace listing message embed with cancellation message, delete buttons
            buttonEvent
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            "The creation of you lisitng has been canceled.")
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
        }

        // Set all the temporary listing back to null. User can only have one at a time
        userController.setCurrentListing(userId, null);
    }

    /**
     * Method to build the /command input the user gave when calling /createlisting.
     *
     * @param currentListing - The current listing the user is working on.
     * @return the command input the user entered as a string.
     */
    private String createListingCommandAsString(Listing currentListing) {
        var fields = currentListing.getFields();
        var cost = fields.getCost().replace(CURRENCY_USED, "");
        var titleStateCityRemoved = currentListing.getTitle().split("]")[1];
        return String.format(
                "/createlisting title: %s item_cost: %s shipping_included: %s description: %s condition: %s image1: [attachment]",
                titleStateCityRemoved,
                cost,
                fields.getShippingIncluded(),
                fields.getDescription(),
                fields.getCondition());
    }
}
