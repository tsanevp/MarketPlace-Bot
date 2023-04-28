package edu.northeastern.cs5500.starterbot.discord;

import edu.northeastern.cs5500.starterbot.model.Listing;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

@Singleton
public class MessageBuilderHelper {
    private static final Integer EMBED_COLOR = 0x00FFFF;

    @Inject
    public MessageBuilderHelper() {
        // Defined public and empty for Dagger injection
    }

    /**
     * Recreates the Listing Object as a message embed.
     *
     * @param listing - The listing to create a message embed from.
     * @param discordDisplayName - The display name of the user who posted the listing.
     * @return A List of message embeds that make up the listing.
     */
    @Nonnull
    public List<MessageEmbed> toMessageEmbed(Listing listing, @Nonnull String discordDisplayName) {
        List<MessageEmbed> listingsMessage = new ArrayList<>();
        var fields = listing.getFields();

        // Create parent embed that includes all fields & content
        var parentEmbed =
                new EmbedBuilder()
                        .setColor(EMBED_COLOR)
                        .setTitle(listing.getTitle(), listing.getUrl())
                        .setImage(listing.getImages().get(0))
                        .addField(
                                Boolean.TRUE.equals(fields.getShippingIncluded())
                                        ? "Cost + Shipping:"
                                        : "Cost:",
                                fields.getCost(),
                                true)
                        .addField("Condition:", fields.getCondition(), true)
                        .addField("Description:", fields.getDescription(), false)
                        .addField("Posted By:", discordDisplayName, true)
                        .addField("Date Posted:", fields.getDatePosted(), true)
                        .build();

        listingsMessage.add(parentEmbed);

        // Create child embeds that "append" additional images to parent embed
        var isFirstImage = true;
        for (String imageUrl : listing.getImages()) {
            if (isFirstImage) {
                isFirstImage = false;
                continue;
            }

            var additionalImageEmbeds =
                    new EmbedBuilder()
                            .setColor(parentEmbed.getColorRaw())
                            .setTitle(parentEmbed.getTitle(), parentEmbed.getUrl())
                            .setImage(imageUrl)
                            .build();
            listingsMessage.add(additionalImageEmbeds);
        }

        return listingsMessage;
    }

    /**
     * Opens a private channel with the user provided and send the given message.
     *
     * @param user - The user to send the private message to.
     * @param messageToSend - The message to send the user. String Type.
     */
    public void sendPrivateMessage(User user, @Nonnull String messageToSend) {
        user.openPrivateChannel().complete().sendMessage(messageToSend).queue();
    }

    /**
     * Opens a private channel with the user provided and send the given message.
     *
     * @param user - The user to send the private message to.
     * @param messageToSend - The message to send the user. MessageCreateData Type.
     */
    public void sendPrivateMessage(User user, @Nonnull MessageCreateData messageToSend) {
        user.openPrivateChannel().complete().sendMessage(messageToSend).queue();
    }
}
