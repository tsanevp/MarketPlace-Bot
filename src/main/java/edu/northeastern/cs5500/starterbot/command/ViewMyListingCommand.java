package edu.northeastern.cs5500.starterbot.command;

import com.mongodb.client.FindIterable;
import edu.northeastern.cs5500.starterbot.controller.ListingController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.model.Listing;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bson.types.ObjectId;

@Singleton
@Slf4j
public class ViewMyListingCommand implements SlashCommandHandler, ButtonHandler {

    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject ListingController listingController;
    @Inject UserController userController;

    @Inject
    public ViewMyListingCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "viewmylisting";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "View all the listings that you have posted");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /viewmylisting");

        User user = event.getUser();

        FindIterable<Listing> listing = listingController.filterListingsByMembersId(user.getId());

        if (this.listingController.countListingsByMemberId(user.getId()) != 0) {
            for (Listing list : listing) {
                MessageCreateBuilder messageCreateBuilder =
                        new MessageCreateBuilder()
                                .addActionRow(
                                        Button.danger(
                                                this.getName()
                                                        + ":"
                                                        + list.getMessageId().toString()
                                                        + ":"
                                                        + list.getId().toHexString()
                                                        + ":delete",
                                                "Delete"))
                                .setEmbeds(this.listingController.toMessageEmbed(list));
                event.getUser()
                        .openPrivateChannel()
                        .complete()
                        .sendMessage(messageCreateBuilder.build())
                        .queue();
                // event.reply(messageCreateBuilder.build()).setEphemeral(true).queue();
            }
            event.reply("Your postings has been sent to your DM").setEphemeral(true).complete();
        } else {
            event.reply("No postings available").setEphemeral(true).complete();
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        User user = event.getUser();
        Guild guild =
                Objects.requireNonNull(
                        event.getJDA()
                                .getGuildById((userController.getGuildIdForUser(user.getId()))));
        MessageChannel channel =
                guild.getTextChannelsByName(userController.getTradingChannel(user.getId()), true)
                        .get(0);
        MessageEditCallbackAction buttonEvent = event.deferEdit().setComponents();
        String[] buttonIds = event.getButton().getId().split(":");
        this.listingController.deleteListingById(new ObjectId(buttonIds[2]));
        channel.deleteMessageById(buttonIds[1]).queue();
        buttonEvent
                .setEmbeds(
                        new EmbedBuilder()
                                .setDescription("Your post has been successfully deleted")
                                .setColor(EMBED_COLOR)
                                .build())
                .queue();
    }
}
