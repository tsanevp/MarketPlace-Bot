package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Ask the bot to reply with the provided text")
                .addOption(
                        OptionType.STRING,
                        "title",
                        "Please provide the title of your listing",
                        true)
                .addOption(
                        OptionType.STRING,
                        "description",
                        "Please provide a decription of the item you wish to sell",
                        true)
                .addOption(
                        OptionType.INTEGER,
                        "cost",
                        "How much do you wish to sell your item for?",
                        true)
                .addOption(
                        OptionType.ATTACHMENT,
                        "image1",
                        "The bot will reply to your command with the provided text",
                        true);
                // .addOption(
                //         OptionType.ATTACHMENT,
                //         "image2",
                //         "The bot will reply to your command with the provided text",
                //         true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /createlisting");
        var title = event.getOption("title");
        var description = event.getOption("description");
        var cost = event.getOption("cost");
        var image1 = event.getOption("image1");
        // var image2 = event.getOption("image2");
        if (title == null
                || description == null
                || cost == null
                || image1 == null) {
                // || image2 == null) {
            log.error("Received null value for mandatory parameter 'content'");
            return;
        }

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(title.getAsString())
                        .addField("Item Description:", description.getAsString(), false)
                        // .setDescription(description.getAsString())
                        .addField("Item Cost:", cost.getAsString(), true)
                        .setImage(image1.getAsAttachment().getUrl())
                        .setColor(312);
        // EmbedBuilder embedBuilder2 =
        //         new EmbedBuilder()
        //                 .setAuthor("peter", "https://example.org/")
        //                 .setThumbnail(option2.getAsAttachment().getUrl());

        event.replyEmbeds(embedBuilder.build()).queue();
        // event.reply(option.getAsString()).queue();
    }
}
