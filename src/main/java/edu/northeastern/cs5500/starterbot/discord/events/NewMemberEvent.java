package edu.northeastern.cs5500.starterbot.discord.events;

import edu.northeastern.cs5500.starterbot.controller.GuildController;
import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.discord.SettingLocationHelper;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewMemberHandler;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

@Singleton
@Slf4j
public class NewMemberEvent implements NewMemberHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject SettingLocationHelper location;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject GuildController guildController;

    @Inject
    public NewMemberEvent() {
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

        // Add user to the List of guild members when they first join
        guildController.addUserToServer(guild.getId(), user.getId());

        var introMsg =
                String.format(
                        "Hello %s! For potential future sales and purchases, please select the State & City you are located in below. If you do not see your city, please select the one nearest to you.",
                        user.getName());

        var newUserWelcomeEmbed =
                new EmbedBuilder()
                        .setTitle(String.format("Welcome to %s!", guild.getName()))
                        .setDescription(introMsg)
                        .setColor(EMBED_COLOR)
                        .build();

        // Create the message to send the new user. Includes state dropdown menus
        var newUserIntroMsg =
                location.createStatesMessageBuilder()
                        .mention(user)
                        .addEmbeds(newUserWelcomeEmbed)
                        .build();

        messageBuilder.sendPrivateMessage(user, newUserIntroMsg);
    }
}
