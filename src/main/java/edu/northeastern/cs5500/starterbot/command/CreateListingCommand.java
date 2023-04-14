package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
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

@Singleton
@Slf4j
public class CreateListingCommand implements SlashCommandHandler, ButtonHandler {
    private static final int MAX_NUM_IMAGES = 6;
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

        // // Stores the user input as a string to the user object, which is saved in the DB
        // userController.setCurrentListingAsString(event.getUser().getId(),
        // event.getCommandString());

        // For all images associated with event, store their urls in a list
        List<String> imageURLs = new ArrayList<>();
        for (OptionMapping image : event.getOptionsByType(OptionType.ATTACHMENT)) {
            imageURLs.add(image.getAsAttachment().getUrl());
        }

        var datePosted = getDatePosted();
        var titleReformatted = reformatListingTitle(userId, title);
        var costTitleAndPrice = getCostTitleAndPrice(cost, shippingIncluded);
        var url = Objects.requireNonNull(imageURLs.get(0));

        // Create ListingFields Object
        var listingFields =
                listingController.createListingFields(
                        costTitleAndPrice, shippingIncluded, condition, description, datePosted);

        // Create Listing Object
        var listing =
                listingController.createListing(
                        0, userId, titleReformatted, url, imageURLs, listingFields, false);

        // Temporarily add listing to MongoDB
        listingController.addListing(listing);

        // Create a confirmation message. User reviews the listing and decides whether to post it
        var listingConfirmation =
                new MessageCreateBuilder()
                        .addActionRow(
                                Button.success(this.getName() + ":ok", "Post"),
                                Button.primary(this.getName() + ":edit", "Edit"),
                                Button.danger(this.getName() + ":cancel", "Cancel"))
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime currentdateTime = LocalDateTime.now();
        return Objects.requireNonNull(dateTimeFormatter.format(currentdateTime));
    }

    /**
     * Method to add whether shipping is included in the price and to add the currency being used
     * for this listing. Both values are added to a list and the list is returned.
     *
     * @param cost - The monetary cost of the item being sold.
     * @param shippingIncluded - A boolean indicating whether shipping costs are included in the
     *     item's price.
     * @return a list where the first index is the Cost field name reformatted, and the second is
     *     the price reformatted.
     */
    @Nonnull
    private List<String> getCostTitleAndPrice(int cost, boolean shippingIncluded) {
        // Reformat the cost title to include + Shipping if shipping is included in the cost
        StringBuilder costTitleReformatted = new StringBuilder("Cost:");
        if (Boolean.TRUE.equals(shippingIncluded)) {
            costTitleReformatted.insert(4, " + Shipping");
        }

        // Reformat the price to include the currency being used
        var costReformatted = Objects.requireNonNull(String.format("%s %s", CURRENCY_USED, cost));

        return Objects.requireNonNull(
                Arrays.asList(costTitleReformatted.toString(), costReformatted));
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        User user = event.getUser();
        // Guild guild = Objects.requireNonNull(event.getGuild());
        // TextChannel textChannel =
        //         guild.getTextChannelsByName(
        //                         Objects.requireNonNull(
        //                                 userController.getTradingChannelId(guild.getOwnerId())),
        //                         true)
        //                 .get(0);

        // Remove the buttons so they are no longer clickable
        MessageEditCallbackAction buttonEvent = event.deferEdit().setComponents();

        if ("Post".equals(event.getButton().getLabel())) {
            listingController.countListingsByMemberId(user.getId());
            //
            // System.out.println(listingController.getTempListingByMemberId(user.getId()).size());
            //     MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();

            // Pulls the listing information from MongoDB and then builds/sets the embed
            //         List<MessageEmbed> embedToPost =
            //
            // Objects.requireNonNull(userController.getCurrentListing(user.getId()));
            //         messageCreateBuilder.setEmbeds(embedToPost);

            //     Send the listing to the "trading-channel"
            //         textChannel
            //                 .sendMessage(messageCreateBuilder.build())
            //                 .queue(
            //                         (message) -> {
            //                             // Store the listing and messageId in the
            // ListingControllerDB
            //                             listingController.setListing(
            //                                     embedToPost, message.getIdLong(), user.getId());
            //                         });

            //     // Replace the temp embed with a success message
            //     buttonEvent
            //             .setEmbeds(
            //                     new EmbedBuilder()
            //                             .setDescription(
            //                                     "Your listing has been posted to the
            // trading-channel!")
            //                             .setColor(EMBED_COLOR)
            //                             .build())
            //             .queue();
        } else if ("Edit".equals(event.getButton().getLabel())) {
            // Replace temp embed with instructions on how to edit the listing, must resubmit one
            //     buttonEvent
            //             .setEmbeds(
            //                     new EmbedBuilder()
            //                             .setDescription(
            //                                     String.format(
            //                                             "To Edit your listing, COPY & PASTE the
            // following to your message line. This will auto-fill each section BUT will not
            // reattach your images. %n%n%s",
            //                                             userController.getCurrentListingAsString(
            //                                                     user.getId())))
            //                             .setColor(EMBED_COLOR)
            //                             .build())
            //             .queue();
        } else {
            // Cancels the /createListing event
            buttonEvent
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            "The creation of you lisitng has been canceled.")
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
        }
        // Set all the temp embed variables back to null
        // userController.setCurrentListing(user.getId(), null);
        // userController.setCurrentListingAsString(user.getId(), null);
    }
}
