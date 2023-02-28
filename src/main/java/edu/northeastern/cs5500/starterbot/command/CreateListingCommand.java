package edu.northeastern.cs5500.starterbot.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

        StringBuilder costReformated = new StringBuilder("Cost:");
        if ("true".equals(shippingCost.getAsString())) {
            costReformated.insert(4, " + Shipping");
        }

        StringBuilder shipsInternationally = new StringBuilder();
        if ("true".equals(shipping.getAsString())) {
            shipsInternationally.append("Yes");
        } else {
            shipsInternationally.append("No");
        }

        ArrayList<MessageEmbed> embedBuilderlist = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if (i == 0) {
                embedBuilder
                        .addField(costReformated.toString(), cost.getAsString(), true)
                        .addField("Ships International:", shipsInternationally.toString(), true)
                        .addField("Condition:", condition.getAsString(), true)
                        .addField("Description:", description.getAsString(), false)
                        .setColor(100)
                        .addField("Posted By:", event.getMember().getEffectiveName(), true)
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
                        .setContent("Result of Button Selection")
                        .setEmbeds(embedBuilderlist);
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        if ("Post".equals(event.getButton().getLabel())) {
            event.reply("Your listing has been posted on trading-channel!").queue();
        } else if ("Edit".equals(event.getButton().getLabel())) {
            event.reply(
                            "To Edit your listing, select your UP Arrow Key. This will auto-fill each section BUT will not reattach your images.")
                    .queue();
            ;
        } else {
            event.reply("The creation of you lisitng has been canceled.").queue();
        }
    }
}

// MTA3NjYzNDczODQwNTY5MTUyMw.G2nuVY.0Aa7eWsEukbK240tiD8K5v9jp3jDiz-4TF6HGs

// **how to set up sending a message to another channel**
// TextChannel textChannel = event.getGuild().getTextChannelsByName("trading-channel",
// true).get(0);
// textChannel.sendMessage(event.getComponentId()).queue();
