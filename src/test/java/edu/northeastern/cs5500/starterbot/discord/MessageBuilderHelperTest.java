package edu.northeastern.cs5500.starterbot.discord;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class MessageBuilderHelperTest {

    MessageBuilderHelper messageBuilder;

    @Test
    void testMessageBuilderToMessageEmbedReturnListIsOfCorrectSize() {
        // If the method returns a list of size 1 or greater, we expect that the method works as
        // intended

        messageBuilder = new MessageBuilderHelper();

        // Define ListingFields parameters
        var cost = "50";
        var shippingIncluded = false;
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
        var guildId = "234257657568";

        // Create ListingFields & Listing objects
        var listingFieldsWithoutShipping =
                ListingFields.builder()
                        .cost(cost)
                        .shippingIncluded(shippingIncluded)
                        .condition(condition)
                        .description(description)
                        .datePosted(datePosted)
                        .build();
        var listingWithoutShipping =
                Listing.builder()
                        .discordUserId(discordUserId)
                        .guildId(guildId)
                        .title(title)
                        .url(url)
                        .images(imageUrl)
                        .fields(listingFieldsWithoutShipping)
                        .build();

        // Call toMessageEmbed on listing object
        var listMessageEmbedWithoutShipping =
                messageBuilder.toMessageEmbed(listingWithoutShipping, discordUserId);

        // Since only one url was added, we expect the List<MessageEmbed> to be size 1
        assertThat(listMessageEmbedWithoutShipping.size()).isEqualTo(1);

        // Add another url link to the list of urls. This should make size List<MessageEmbed> 2
        imageUrl.add(url);

        // Change shipping included to true
        shippingIncluded = true;

        // Create new ListingFields & Listing objects
        var listingFieldsWithShipping =
                ListingFields.builder()
                        .cost(cost)
                        .shippingIncluded(shippingIncluded)
                        .condition(condition)
                        .description(description)
                        .datePosted(datePosted)
                        .build();

        var listingWithShipping =
                Listing.builder()
                        .discordUserId(discordUserId)
                        .guildId(guildId)
                        .title(title)
                        .url(url)
                        .images(imageUrl)
                        .fields(listingFieldsWithShipping)
                        .build();

        // Call toMessageEmbed on new listing objects
        var listMessageEmbedWithShipping =
                messageBuilder.toMessageEmbed(listingWithShipping, discordUserId);

        // Check that the size of the list returned is 2
        assertThat(listMessageEmbedWithShipping.size()).isEqualTo(2);

        // Check that the two listMessageEmbeds are different
        assertThat(listMessageEmbedWithoutShipping).isEqualTo(listMessageEmbedWithoutShipping);
        assertThat(listMessageEmbedWithoutShipping).isNotEqualTo(listMessageEmbedWithShipping);
    }
}
