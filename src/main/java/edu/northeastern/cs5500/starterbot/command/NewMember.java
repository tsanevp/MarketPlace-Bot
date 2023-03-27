package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class NewMember implements NewMemberHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject Location location;
    @Inject UserController userController;

    @Inject
    public NewMember() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "newmember";
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        log.info("event: newmember");

        // Assigns the Guild ID to the user object when a user first joins
        userController.setGuildIdForUser(event.getUser().getId(), event.getGuild().getId());

        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setTitle(String.format("Welcome to %s!", event.getGuild().getName()))
                        .setDescription(
                                String.format(
                                        "Hello %s! For potential future sales and purchases, please select the State & City you are located in below. If you do not see your city, please select the one nearest to you.",
                                        event.getUser().getName()))
                        .setColor(EMBED_COLOR);

        // Create the new user Message to send. Includes the built State location selection menus
        MessageCreateBuilder newMemberIntroMsg =
                location.createStatesMessageBuilder()
                        .mention(event.getUser())
                        .addEmbeds(embedBuilder.build());

        // Send the message to the user
        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessage(newMemberIntroMsg.build())
                .queue();
    }
}
