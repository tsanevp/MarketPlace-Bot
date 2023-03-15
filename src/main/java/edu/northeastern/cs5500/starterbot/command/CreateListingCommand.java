package edu.northeastern.cs5500.starterbot.command;

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
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class CreateListingCommand implements SlashCommandHandler, ButtonHandler {
    private static final int MAX_NUM_IMAGES = 6;
    private static final String CURRENCY_USED = "USD ";

    @Inject UserController userController;

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
                        "location",
                        "What Country and State/Region are you in? Format:{Country, State/region}",
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
        var title = Objects.requireNonNull(event.getOption("title"));
        var cost = Objects.requireNonNull(event.getOption("item_cost"));
        var shippingCost = Objects.requireNonNull(event.getOption("shipping_cost_included"));
        var shipping = Objects.requireNonNull(event.getOption("will_ship_internationally"));
        var location = Objects.requireNonNull(event.getOption("location"));
        var condition = Objects.requireNonNull(event.getOption("condition"));
        var description = Objects.requireNonNull(event.getOption("description"));

        // Stores the user input as a string to the user object, which is saved in the DB
        userController.setCurrentListingAsString(
                event.getUser().getName(), event.getCommandString());

        // Stores the Guild ID to user object, which is saved in the DB
        userController.setGuildIdForUser(event.getUser().getName(), event.getGuild().getId());

        ArrayList<OptionMapping> images = new ArrayList<>();
        for (int i = 1; i < MAX_NUM_IMAGES + 1; i++) {
            if (event.getOption(String.format("image%s", i)) != null) {
                images.add(event.getOption(String.format("image%s", i)));
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        StringBuilder titleReformatted = new StringBuilder(title.getAsString());
        if (!location.getAsString().isEmpty()) {
            titleReformatted.insert(0, String.format("[%s]", location.getAsString()));
        }

        StringBuilder costTitleReformatted = new StringBuilder("Cost:");
        if ("true".equals(shippingCost.getAsString())) {
            costTitleReformatted.insert(4, " + Shipping");
        }

        StringBuilder costReformatted = new StringBuilder(CURRENCY_USED);
        costReformatted.append(cost.getAsString());

        StringBuilder shipsInternationally = new StringBuilder();
        if ("true".equals(shipping.getAsString())) {
            shipsInternationally.append("Yes");
        } else {
            shipsInternationally.append("No");
        }

        List<MessageEmbed> embedBuilderlist = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if (i == 0) {
                embedBuilder
                        .setColor(0x00FFFF)
                        .addField(costTitleReformatted.toString(), costReformatted.toString(), true)
                        .addField("Ships International:", shipsInternationally.toString(), true)
                        .addField("Condition:", condition.getAsString(), true)
                        .addField("Description:", description.getAsString(), false)
                        .addField("Posted By:", event.getUser().getName(), true)
                        .addField("Date Posted:", dtf.format(now), true);
            }
            embedBuilder
                    .setTitle(titleReformatted.toString(), images.get(0).getAsAttachment().getUrl())
                    .setImage(images.get(i).getAsAttachment().getUrl());
            embedBuilderlist.add(embedBuilder.build());
        }

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder
                        .addActionRow(
                                Button.success(this.getName() + ":ok", "Post"),
                                Button.primary(this.getName() + ":edit", "Edit"),
                                Button.danger(this.getName() + ":cancel", "Cancel"))
                        .setEmbeds(embedBuilderlist);
        User user = event.getUser();

        userController.setCurrentListing(event.getUser().getName(), embedBuilderlist);

        // Sends DM to user who called /createlisting with their listing information
        user.openPrivateChannel().complete().sendMessage(messageCreateBuilder.build()).queue();
        event.reply(
                        String.format(
                                "Hello %s! Please check the DM you just received from BOT to complete your listing.",
                                user.getName()))
                .queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        User user = event.getUser();

        // Pulls the Guild ID from the user object
        Guild guild = event.getJDA().getGuildById(userController.getGuildIdForUser(user.getName()));
        TextChannel textChannel = guild.getTextChannelsByName("trading-channel", true).get(0);
        if ("Post".equals(event.getButton().getLabel())) {
            event.reply("Your listing has been posted on trading-channel!").queue();

            // If Post is pressed, pulls the embedbuilder list from the user object
            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
            messageCreateBuilder.setEmbeds(userController.getCurrentListing(user.getName()));
            textChannel.sendMessage(messageCreateBuilder.build()).queue();

        } else if ("Edit".equals(event.getButton().getLabel())) {
            // If Edit is pressed, pulls the saved user inputs from the user object
            event.reply(
                            String.format(
                                    "To Edit your listing, COPY & PASTE the following to your message line. This will auto-fill each section BUT will not reattach your images. \n\n%s",
                                    userController.getCurrentListingAsString(user.getName())))
                    .queue();
        } else {
            event.reply("The creation of you lisitng has been canceled.").queue();
        }
        // Uncomment once buttons are made to be clickable only once during the listing creation
        // process
        // userController.setCurrentListing(user.getName(), null);
        // userController.setCurrentListingAsString(user.getName(), null);
    }
}
