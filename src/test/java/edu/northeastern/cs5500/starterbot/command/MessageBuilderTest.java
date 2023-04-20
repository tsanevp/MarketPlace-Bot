package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MessageBuilderTest {

    MessageBuilder messageBuilder;

    @Test
    void testMessageBuilderToMessageEmbedReturnListIsOfCorrectSize() {
        // If the method returns a list of size 1 or greater, we expect that the method works as
        // intended

        messageBuilder = new MessageBuilder();

        // Define ListingFields parameters
        var cost = "50";
        var shippingIncludedFalse = false;
        var condition = "very good";
        var description = "test description";
        var datePosted = "4/15/23";

        // Define Listing parameters
        var discordUserId = "DISCORD_ID_1";
        var title = "test title";
        List<String> imageUrl = new ArrayList<>();
        var url =
                "https://cdn.discordapp.com/ephemeral-attachments/1077737981869297745/1096913399180435537/Screenshot_2023-04-07_at_12.31.41_PM.png";
        imageUrl.add(url);

        // Create ListingFields & Listing objects
        var listingFields1 =
                ListingFields.builder()
                        .cost(cost)
                        .shippingIncluded(shippingIncludedFalse)
                        .condition(condition)
                        .description(description)
                        .datePosted(datePosted)
                        .build();
        var listing1 =
                Listing.builder()
                        .messageId(0)
                        .discordUserId(discordUserId)
                        .title(title)
                        .url(url)
                        .images(imageUrl)
                        .fields(listingFields1)
                        .build();

        // Call toMessageEmbed on listing object
        var listMessageEmbeds1 = messageBuilder.toMessageEmbed(listing1, discordUserId);

        // Since only one url was added, we expect the List<MessageEmbed> to be size 1
        assertThat(listMessageEmbeds1.size()).isEqualTo(1);

        // Add another url link to the list of urls. This should make size List<MessageEmbed> 2
        imageUrl.add(url);

        // Change shipping included to true
        var shippingIncludedTrue = true;

        // Create new ListingFields & Listing objects
        var listingFields2 =
                ListingFields.builder()
                        .cost(cost)
                        .shippingIncluded(shippingIncludedTrue)
                        .condition(condition)
                        .description(description)
                        .datePosted(datePosted)
                        .build();

        var listing2 =
                Listing.builder()
                        .messageId(0)
                        .discordUserId(discordUserId)
                        .title(title)
                        .url(url)
                        .images(imageUrl)
                        .fields(listingFields2)
                        .build();

        // Call toMessageEmbed on new listing objects
        var listMessageEmbeds2 = messageBuilder.toMessageEmbed(listing2, discordUserId);

        // Check that the size of the list returned is 2
        assertThat(listMessageEmbeds2.size()).isEqualTo(2);

        // Check that the two listMessageEmbeds are different
        assertThat(listMessageEmbeds1).isEqualTo(listMessageEmbeds1);
        assertThat(listMessageEmbeds1).isNotEqualTo(listMessageEmbeds2);
    }
}
