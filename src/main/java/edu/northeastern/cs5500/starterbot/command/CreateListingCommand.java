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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
@Slf4j
public class CreateListingCommand implements SlashCommandHandler {

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
    // /createlisting title:SomeTitle description:Super Long Description condition:New cost:54 image: shipping:true
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
        return Commands.slash(getName(), "Please input the following information to complete your listing")
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
                        "preview_image",
                        "Please upload an image of the item you are selling",
                        true)
                .addOption(
                        OptionType.STRING,
                        "additional_photos",
                        "Please input the link to any additional photos you wish to inlcude",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image2",
                        "The bot will reply to your command with the provided text",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image3",
                        "The bot will reply to your command with the provided text",
                        true);
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
        var image = Objects.requireNonNull(event.getOption("preview_image"));
        var image2 = Objects.requireNonNull(event.getOption("image2"));
        var image3 = Objects.requireNonNull(event.getOption("image3"));
        var additionalPhotos = Objects.requireNonNull(event.getOption("additional_photos"));

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

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
                        .addField(costReformated.toString(), cost.getAsString(), true)
                        // .addField("Shipping Costs Included in Price:", shippingCost.getAsString(), false)
                        .addField("Ships International:", shipping.getAsString().substring(0, 1).toUpperCase() + shipping.getAsString().substring(1), true)
                        .addField("Condition:", condition.getAsString(), true)
                        .addField("Description:", description.getAsString(), false)
                        .setImage(image.getAsAttachment().getUrl())
                        .setColor(1752220)
                        .addField("Posted By:", event.getMember().getEffectiveName(), true)
                        .addField("Date Posted:", dtf.format(now), true)
                        .setFooter("Link To Additional Images:\n" + additionalPhotos.getAsString(), image.getAsAttachment().getUrl());

        EmbedBuilder embedBuilder2 =
                new EmbedBuilder()
                        .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
                        .setImage(image2.getAsAttachment().getUrl());
        EmbedBuilder embedBuilder3 =
                new EmbedBuilder()
                        .setTitle(titleReformatted.toString(), image.getAsAttachment().getUrl())
                        .setImage(image3.getAsAttachment().getUrl());
        ArrayList<MessageEmbed> embedBuilderlist = new ArrayList<>();
        embedBuilderlist.add(embedBuilder.build());
        embedBuilderlist.add(embedBuilder2.build());
        embedBuilderlist.add(embedBuilder3.build());
        event.replyEmbeds(embedBuilderlist).queue();

        // TextChannel textChannel = event.getGuild().getTextChannelsByName("trading-channel", true).get(0);
        
        // event.replyEmbeds(embedBuilder.build()).queue();
        // event.reply(option.getAsString()).queue();
    }
}
