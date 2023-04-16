package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.command.handlers.NewMemberHandler;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

@Singleton
@Slf4j
public class NewMember implements NewMemberHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject Location location;
    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;

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

        var user = event.getUser();
        var guild = event.getGuild();

        // Assigns the Guild ID to the user object when a user first joins
        userController.setGuildIdForUser(user.getId(), guild.getId());

        var newUserWelcomeMessage =
                new EmbedBuilder()
                        .setTitle(String.format("Welcome to %s!", guild.getName()))
                        .setDescription(
                                String.format(
                                        "Hello %s! For potential future sales and purchases, please select the State & City you are located in below. If you do not see your city, please select the one nearest to you.",
                                        user.getName()))
                        .setColor(EMBED_COLOR)
                        .build();

        // Create the message to send the new user. Includes state dropdown menus
        var newMemberIntroMsg =
                location.createStatesMessageBuilder()
                        .mention(user)
                        .addEmbeds(newUserWelcomeMessage)
                        .build();

        messageBuilder.sendPrivateMessage(user, newMemberIntroMsg);
    }
}
