package edu.northeastern.cs5500.starterbot.discord.events;

import com.google.common.annotations.VisibleForTesting;
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
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
@Slf4j
public class NewMemberEvent implements NewMemberHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject GuildController guildController;
    @Inject MessageBuilderHelper messageBuilder;
    @Inject SettingLocationHelper settingLocationHelper;

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

        var statesSelectMessageBuilder = settingLocationHelper.createStatesMessageBuilder();
        var newUserIntroMsg =
                createIntroMessageForNewUser(
                        user.getName(), guild.getName(), statesSelectMessageBuilder);

        messageBuilder.sendPrivateMessage(user, newUserIntroMsg);
    }

    /**
     * Method to create the intro message to send the user when they first join the guild.
     *
     * @param userName - The display name of the user that joined.
     * @param guildName - The name of the guild the user joined.
     * @param statesSelectMessageBuilder - A message create builder containing both the state select
     *     menus.
     * @return The introduction message that is sent to the user that includes the state selection
     *     menus.
     */
    @Nonnull
    @VisibleForTesting
    MessageCreateData createIntroMessageForNewUser(
            @Nonnull String userName,
            @Nonnull String guildName,
            @Nonnull MessageCreateBuilder statesSelectMessageBuilder) {
        var introMsg =
                String.format(
                        "Hello %s! For potential future sales and purchases, please select the State & City you are located in below. If you do not see your city, please select the one nearest to you.",
                        userName);
        var newUserWelcomeEmbed =
                new EmbedBuilder()
                        .setTitle(String.format("Welcome to %s!", guildName))
                        .setDescription(introMsg)
                        .setColor(EMBED_COLOR)
                        .build();

        // Create the message to send the new user. Includes state dropdown menus
        return statesSelectMessageBuilder.addEmbeds(newUserWelcomeEmbed).build();
    }
}
