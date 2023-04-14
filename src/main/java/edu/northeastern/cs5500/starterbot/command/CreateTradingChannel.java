package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserController;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@Singleton
public class CreateTradingChannel {

    @Inject UserController userController;
    @Inject MessageBuilder messageBuilder;

    @Inject
    public CreateTradingChannel() {
        // Defined public and empty for Dagger injection
    }

    /**
     * Creates a trading channel with specific permissions and adds it to "Text Channels" grouping.
     *
     * @param owner - The owner of the Discord Guild.
     * @param guild - The guild to add the text channel to.
     * @param channelName - The name to give the channel.
     */
    public void createNewTradingChannel(User owner, Guild guild, @Nonnull String channelName) {
        var category = guild.getCategoriesByName("text channels", true).get(0);

        // Permissions that should be applied to the channel
        EnumSet<Permission> deny =
                EnumSet.of(
                        Permission.MESSAGE_SEND,
                        Permission.CREATE_PRIVATE_THREADS,
                        Permission.MESSAGE_MANAGE,
                        Permission.MANAGE_THREADS);

        // Creation of the new channel
        var textChannel =
                category.createTextChannel(channelName)
                        .addPermissionOverride(guild.getPublicRole(), null, deny)
                        .complete();
        textChannel.getManager().setParent(category);

        // Send success message that the channel was created
        var successMessage =
                Objects.requireNonNull(
                        String.format(
                                "A new channel named %s has been created in your server %s.",
                                channelName, guild.getName()));
        messageBuilder.sendPrivateMessage(owner, successMessage);

        // Set this channel as the trading channel for the Discord server
        userController.setTradingChannelId(owner.getId(), textChannel.getId());
    }
}
