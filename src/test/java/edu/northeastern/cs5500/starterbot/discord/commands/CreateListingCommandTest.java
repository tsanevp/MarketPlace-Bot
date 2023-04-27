package edu.northeastern.cs5500.starterbot.discord.commands;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.discord.MessageBuilderHelper;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class CreateListingCommandTest {
    static final String COST = "50";
    static final boolean SHIPPING_INCLUDED = false;
    static final String CONDITION = "very good";
    static final String DESCRIPTION = "test description";
    static final String TITLE = "[Seattle, WA]test title";
    static final String GUILD_ID = "12345";
    static final String USER_ID = "11223344";
    static final String URL =
            "https://cdn.discordapp.com/ephemeral-attachments/1077737981869297745/1096913399180435537/Screenshot_2023-04-07_at_12.31.41_PM.png";
    static final List<String> LIST_IMAGE_URLS = new ArrayList<>(Arrays.asList(URL));

    CreateListingCommand createListingCommand;
    MessageBuilderHelper messageBuilderHelper;
    ListingFields listingFieldsObjectOne;
    Listing listingObjectOne;

    @BeforeEach
    void initializeCreateListingCommand() {
        createListingCommand = new CreateListingCommand();

        listingFieldsObjectOne =
                createListingCommand.buildListingFields(
                        COST, SHIPPING_INCLUDED, CONDITION, DESCRIPTION);

        listingObjectOne =
                createListingCommand.buildListing(
                        TITLE, GUILD_ID, USER_ID, LIST_IMAGE_URLS, listingFieldsObjectOne);
    }

    @Test
    void testGetCommandName() {
        assertThat(createListingCommand.getName()).isEqualTo("createlisting");
    }

    @Test
    void testBuildListingFieldsIsBuiltCorrectly() {
        // Create another listing fields object with same values
        var listingFieldsObjectTwo =
                createListingCommand.buildListingFields(
                        COST, SHIPPING_INCLUDED, CONDITION, DESCRIPTION);

        // Check both objects are not null
        assertThat(listingFieldsObjectOne).isNotNull();
        assertThat(listingFieldsObjectOne).isNotNull();

        // Check both equal each other and the build process is consistent
        assertThat(listingFieldsObjectOne).isEqualTo(listingFieldsObjectTwo);

        // Check shipping included and condition return what we expect, assume rest do also
        assertThat(listingFieldsObjectOne.getShippingIncluded()).isEqualTo(SHIPPING_INCLUDED);
        assertThat(listingFieldsObjectOne.getCondition()).isEqualTo(CONDITION);
    }

    @Test
    void testBuildListingIsBuiltCorrectly() {
        // Create a listing fields object
        var listingFields =
                createListingCommand.buildListingFields(
                        COST, SHIPPING_INCLUDED, CONDITION, DESCRIPTION);

        // Build duplicate copy of listing object one
        var listingObjectTwo =
                createListingCommand.buildListing(
                        TITLE, GUILD_ID, USER_ID, LIST_IMAGE_URLS, listingFields);

        // Check both objects are not null
        assertThat(listingObjectOne).isNotNull();
        assertThat(listingObjectTwo).isNotNull();

        // Check both equal each other and the build process is consistent
        assertThat(listingObjectOne).isEqualTo(listingObjectTwo);

        // Check shipping included and condition return what we expect, assume rest do also
        assertThat(listingObjectOne.getFields()).isEqualTo(listingFields);
        assertThat(listingObjectOne.getDiscordUserId()).isEqualTo(USER_ID);
    }

    @Test
    void testCreateListingConfirmationMessageCreatesTheMessageWeExpect() {
        // Initialize messageBuilderHelper and create message embed from listing
        messageBuilderHelper = new MessageBuilderHelper();
        var listingAsEmbed = messageBuilderHelper.toMessageEmbed(listingObjectOne, "testUser");

        // Create listing confirmation message
        var listingConfirmationMessage =
                createListingCommand.createListingConfirmationMessage(listingAsEmbed);

        // Check if non null
        assertThat(listingConfirmationMessage).isNotNull();

        // Get buttons and check the ones we expect exist
        var buttonsCreated = listingConfirmationMessage.getComponents().get(0).getButtons();
        assertThat(buttonsCreated.size()).isEqualTo(3);
        assertThat(buttonsCreated.get(0).getLabel()).isEqualTo("Post");

        // Get embed and check it is the one we expect to be set
        assertThat(listingConfirmationMessage.getEmbeds()).isEqualTo(listingAsEmbed);
    }

    @Test
    void testGetDatePostedReturnsTheCorrectDateTime() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        var currentdateTime = LocalDateTime.now();
        assertThat(createListingCommand.getDatePosted())
                .isEqualTo(dateTimeFormatter.format(currentdateTime));
    }

    @Test
    void testReformatCostValueReturnsCurrencyExchangePlusCostValue() {
        assertThat(createListingCommand.reformatCostValue("10")).isEqualTo("USD 10");
    }

    @Test
    void testCreateListingCommandAsString() {
        var costReformatted = listingFieldsObjectOne.getCost().replace("USD", "");
        var titleReformatted = listingObjectOne.getTitle();

        if (titleReformatted.contains("]")) {
            titleReformatted = titleReformatted.split("]")[1];
        }

        assertThat(titleReformatted).isEqualTo("test title");

        var listingAsString =
                Objects.requireNonNull(
                        String.format(
                                "/createlisting title: %s item_cost: %s shipping_included: %s description: %s condition: %s",
                                titleReformatted,
                                costReformatted,
                                listingFieldsObjectOne.getShippingIncluded(),
                                listingFieldsObjectOne.getDescription(),
                                listingFieldsObjectOne.getCondition()));
        assertThat(createListingCommand.createListingCommandAsString(listingObjectOne))
                .isEqualTo(listingAsString);

        var listingObjectTwo =
                createListingCommand.buildListing(
                        "test title", GUILD_ID, USER_ID, LIST_IMAGE_URLS, listingFieldsObjectOne);
        titleReformatted = listingObjectTwo.getTitle();

        if (titleReformatted.contains("]")) {
            titleReformatted = titleReformatted.split("]")[1];
        }
        assertThat(titleReformatted).isEqualTo("test title");
    }
}
