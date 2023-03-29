package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
                        "shipping_cost_included",
                        "Is the price of shipping included in your cost?",
                        true)
                .addOption(
                        OptionType.BOOLEAN,
                        "will_ship_internationally",
                        "Are you willing to ship your item internationally?",
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
        // need to delete after testing
        userController.setTradingChannel(
                Objects.requireNonNull(event.getGuild()).getOwnerId(), "trading-channel");

        log.info("event: /createlisting");
        var title = Objects.requireNonNull(event.getOption("title"));
        var cost = Objects.requireNonNull(event.getOption("item_cost"));
        var shippingCost = Objects.requireNonNull(event.getOption("shipping_cost_included"));
        var shipping = Objects.requireNonNull(event.getOption("will_ship_internationally"));
        var condition = Objects.requireNonNull(event.getOption("condition"));
        var description = Objects.requireNonNull(event.getOption("description"));

        // Stores the user input as a string to the user object, which is saved in the DB
        userController.setCurrentListingAsString(event.getUser().getId(), event.getCommandString());

        // Build the embed that represents the user's listing
        List<MessageEmbed> embedBuilderlist =
                Objects.requireNonNull(
                        buildListingEmbed(
                                event,
                                title,
                                cost,
                                shippingCost,
                                shipping,
                                condition,
                                description));

        // Temp save embed to mongoDB
        userController.setCurrentListing(event.getUser().getId(), embedBuilderlist);

        // Create a message builder to add embeds and action buttons user must interact with
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder
                        .addActionRow(
                                Button.success(this.getName() + ":ok", "Post"),
                                Button.primary(this.getName() + ":edit", "Edit"),
                                Button.danger(this.getName() + ":cancel", "Cancel"))
                        .setEmbeds(embedBuilderlist);

        // Responds with Ephemeral message (message visible only to user who called /createlisting)
        event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
    }

    // Method to build the listing embed
    private List<MessageEmbed> buildListingEmbed(
            SlashCommandInteractionEvent event,
            @Nonnull OptionMapping title,
            @Nonnull OptionMapping cost,
            @Nonnull OptionMapping shippingCost,
            @Nonnull OptionMapping shipping,
            @Nonnull OptionMapping condition,
            @Nonnull OptionMapping description) {

        // Stores the Guild ID to user object, which is saved in the DB - remove soon
        if (userController.getGuildIdForUser(event.getUser().getId()) == null) {
            userController.setGuildIdForUser(
                    event.getUser().getId(), Objects.requireNonNull(event.getGuild()).getId());
        }

        // Create a List of the image variables
        ArrayList<OptionMapping> images = new ArrayList<>();
        for (int i = 1; i < MAX_NUM_IMAGES + 1; i++) {
            if (event.getOption(Objects.requireNonNull(String.format("image%s", i))) != null) {
                images.add(event.getOption(Objects.requireNonNull(String.format("image%s", i))));
            }
        }

        // Reformat the date & time of when the listing was posted
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime currentdateTime = LocalDateTime.now();

        // Reformat the title to include the location of the user
        StringBuilder titleReformatted = new StringBuilder(title.getAsString());
        titleReformatted.insert(
                0,
                String.format(
                        "[%s, %s]",
                        userController.getCityOfResidence(event.getUser().getId()),
                        userController.getStateOfResidence(event.getUser().getId())));

        // Reformat the cost title to include + Shipping if shipping is included in the cost
        StringBuilder costTitleReformatted = new StringBuilder("Cost:");
        if ("true".equals(shippingCost.getAsString())) {
            costTitleReformatted.insert(4, " + Shipping");
        }

        // Reformat the price to include the currency being used
        StringBuilder costReformatted = new StringBuilder(CURRENCY_USED);
        costReformatted.append(cost.getAsString());

        // Replace true/false with yes/no when it comes to shipping internationally
        StringBuilder shipsInternationally = new StringBuilder();
        if ("true".equals(shipping.getAsString())) {
            shipsInternationally.append("Yes");
        } else {
            shipsInternationally.append("No");
        }

        // Create the list of MessageEmbeds that gets merged & displayed as one MessageEmbed
        List<MessageEmbed> embedBuilderlist = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if (i == 0) {
                embedBuilder
                        .setColor(EMBED_COLOR)
                        .addField(
                                Objects.requireNonNull(costTitleReformatted.toString()),
                                Objects.requireNonNull(costReformatted.toString()),
                                true)
                        .addField(
                                "Ships International:",
                                Objects.requireNonNull(shipsInternationally.toString()),
                                true)
                        .addField("Condition:", condition.getAsString(), true)
                        .addField("Description:", description.getAsString(), false)
                        .addField("Posted By:", event.getUser().getName(), true)
                        .addField(
                                "Date Posted:",
                                Objects.requireNonNull(dateTimeFormatter.format(currentdateTime)),
                                true);
            }
            embedBuilder
                    .setTitle(titleReformatted.toString(), images.get(0).getAsAttachment().getUrl())
                    .setImage(images.get(i).getAsAttachment().getUrl());
            embedBuilderlist.add(embedBuilder.build());
        }
        return embedBuilderlist;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        User user = event.getUser();
        Guild guild = Objects.requireNonNull(event.getGuild());
        TextChannel textChannel =
                guild.getTextChannelsByName(
                                Objects.requireNonNull(
                                        userController.getTradingChannel(guild.getOwnerId())),
                                true)
                        .get(0);

        // Remove the buttons so they are no longer clickable
        MessageEditCallbackAction buttonEvent = event.deferEdit().setComponents();

        if ("Post".equals(event.getButton().getLabel())) {
            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();

            // Pulls the listing information from MongoDB and then builds/sets the embed
            List<MessageEmbed> embedToPost =
                    Objects.requireNonNull(userController.getCurrentListing(user.getId()));
            messageCreateBuilder.setEmbeds(embedToPost);

            // Send the listing to the "trading-channel"
            textChannel.sendMessage(messageCreateBuilder.build()).queue();

            // Store the listing in the ListingControllerDB
            listingController.setListing(embedToPost, user.getId());

            // Replace the temp embed with a success message
            buttonEvent
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            "Your listing has been posted to the trading-channel!")
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
        } else if ("Edit".equals(event.getButton().getLabel())) {
            // Replace temp embed with instructions on how to edit the listing, must resubmit one
            buttonEvent
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            String.format(
                                                    "To Edit your listing, COPY & PASTE the following to your message line. This will auto-fill each section BUT will not reattach your images. %n%n%s",
                                                    userController.getCurrentListingAsString(
                                                            user.getId())))
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
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
        userController.setCurrentListing(user.getId(), null);
        userController.setCurrentListingAsString(user.getId(), null);
    }
}
