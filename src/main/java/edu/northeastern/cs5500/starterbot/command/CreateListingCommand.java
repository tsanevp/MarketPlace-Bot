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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class CreateListingCommand implements SlashCommandHandler, ButtonHandler {

    @Inject
    public CreateListingCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "createlisting";
    }

    // This defines what input boxes appear when a user types our command.
    // Country, State/Region
    // /createlisting title:SomeTitle description:Super Long Description condition:New cost:54
    // image: shipping:true
    // [Country, State] Title
    // Item Cost
    // Shipping Cost Included
    // International Shipping
    // Item Condition
    // Item Description
    // Posted By
    // Date Posted
    // Image
    // Link to additional images
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
                        "Please upload an image of the item you are selling",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image2",
                        "Please upload an image of the item you are selling",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image3",
                        "Please upload an image of the item you are selling",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image4",
                        "Please upload an image of the item you are selling",
                        false)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image5",
                        "Please upload an image of the item you are selling",
                        false)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image6",
                        "Please upload an image of the item you are selling",
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
        var image = Objects.requireNonNull(event.getOption("image1"));
        var image2 = Objects.requireNonNull(event.getOption("image2"));
        var image3 = Objects.requireNonNull(event.getOption("image3"));
        // var image4 = Objects.requireNonNull(event.getOption("image4"));
        // var image5 = Objects.requireNonNull(event.getOption("image5"));
        // var image6 = Objects.requireNonNull(event.getOption("image6"));

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

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
                        .addField(costReformated.toString(), cost.getAsString(), true)
                        .addField("Ships International:", shipsInternationally.toString(), true)
                        .addField("Condition:", condition.getAsString(), true)
                        .addField("Description:", description.getAsString(), false)
                        .setImage(image.getAsAttachment().getUrl())
                        .setColor(100)
                        .addField("Posted By:", event.getMember().getEffectiveName(), true)
                        .addField("Date Posted:", dtf.format(now), true);

        EmbedBuilder embedBuilder2 =
                new EmbedBuilder()
                        .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
                        .setImage(image2.getAsAttachment().getUrl());
        EmbedBuilder embedBuilder3 =
                new EmbedBuilder()
                        .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
                        .setImage(image3.getAsAttachment().getUrl());
        // EmbedBuilder embedBuilder4 =
        //         new EmbedBuilder()
        //                 .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
        //                 .setImage(image4.getAsAttachment().getUrl());
        // EmbedBuilder embedBuilder5 =
        //         new EmbedBuilder()
        //                 .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
        //                 .setImage(image5.getAsAttachment().getUrl());
        // EmbedBuilder embedBuilder6 =
        //         new EmbedBuilder()
        //                 .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
        //                 .setImage(image6.getAsAttachment().getUrl());
        ArrayList<MessageEmbed> embedBuilderlist = new ArrayList<>();
        embedBuilderlist.add(embedBuilder.build());
        embedBuilderlist.add(embedBuilder2.build());
        embedBuilderlist.add(embedBuilder3.build());
        // embedBuilderlist.add(embedBuilder4.build());
        // embedBuilderlist.add(embedBuilder5.build());
        // embedBuilderlist.add(embedBuilder6.build());
        // event.replyEmbeds(embedBuilderlist).queue();
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder
                        .addActionRow(
                                Button.success(this.getName() + ":ok", "Post"),
                                Button.danger(this.getName() + ":cancel", "Cancel"))
                        .setContent("Example buttons")
                        .setEmbeds(embedBuilderlist);
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        if ("Post".equals(event.getButton().getLabel())) {
            // TextChannel textChannel = event.getGuild().getTextChannelsByName("trading-channel",
            // true).get(0);
            // textChannel.sendMessage(event.getComponentId()).queue();
            System.out.println(event.getComponentType());
            event.reply("Your listing has been posted on trading-channel!").queue();
        } else {
            event.reply(event.getButton().getLabel()).queue();
        }
    }
}
// MTA3NjYzNDczODQwNTY5MTUyMw.G2nuVY.0Aa7eWsEukbK240tiD8K5v9jp3jDiz-4TF6HGs
