package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserControllerTest {

    static final String DISCORD_ID_1 = "testUser1";
    static final String DISCORD_ID_2 = "testUser2";
    static final String GUILD_ID_1 = "987654321";
    static final String GUILD_ID_2 = "123456789";
    static final String TRADING_CHANNEL_ID_1 = "trading-channel";
    static final String STATE_OF_RESIDENCE_1 = "WA";
    static final String CITY_OF_RESIDENCE_1 = "Seattle";

    UserController userController;

    private UserController getUserController() {
        UserController userController = new UserController(new InMemoryRepository<>());
        return userController;
    }

    @BeforeEach
    void initializeUserController() {
        userController = getUserController();
    }

    @Test
    void testSetAndGetGuildIdForUserActuallySetsAndGetsGuildId() {
        // Need to first set Guild Id since it is @NonNull
        userController.setGuildIdForUser(DISCORD_ID_1, GUILD_ID_1);

        // Check to see the correct Guild id is returned
        assertThat(userController.getGuildIdForUser(DISCORD_ID_1)).isNotNull();
        assertThat(userController.getGuildIdForUser(DISCORD_ID_1)).isEqualTo(GUILD_ID_1);
    }

    @Test
    void testSetAndGetTradingChannelIdForUserActuallySetsAndGetsTradingChannelId() {
        // Check that trading channel id is initially null
        assertThat(userController.getTradingChannelId(DISCORD_ID_1)).isNull();

        // Set trading channel id
        userController.setTradingChannelId(DISCORD_ID_1, TRADING_CHANNEL_ID_1);

        // Check to see the correct Guild id is returned
        assertThat(userController.getTradingChannelId(DISCORD_ID_1)).isNotNull();
        assertThat(userController.getTradingChannelId(DISCORD_ID_1))
                .isEqualTo(TRADING_CHANNEL_ID_1);
    }

    @Test
    void testSetAndGetStateOfResidenceForUserActuallySetsAndGetsStateOfResidence() {
        // Need to first set state of residence since get() is @NonNull
        userController.setStateOfResidence(DISCORD_ID_1, STATE_OF_RESIDENCE_1);

        // Check to see the correct state is returned
        assertThat(userController.getStateOfResidence(DISCORD_ID_1)).isNotNull();
        assertThat(userController.getStateOfResidence(DISCORD_ID_1))
                .isEqualTo(STATE_OF_RESIDENCE_1);
    }

    @Test
    void testSetAndGetCityOfResidenceForUserActuallySetsAndGetsCityOfResidence() {
        // Need to first set city of residence since get() is @NonNull
        userController.setCityOfResidence(DISCORD_ID_1, CITY_OF_RESIDENCE_1);

        // Check to see the correct city is returned
        assertThat(userController.getCityOfResidence(DISCORD_ID_1)).isNotNull();
        assertThat(userController.getCityOfResidence(DISCORD_ID_1)).isEqualTo(CITY_OF_RESIDENCE_1);
    }

    @Test
    void testSetCurrentListing() {
        ListingController listingController = new ListingController(new InMemoryRepository<>());

        // Define ListingFields parameters
        var cost = "50";
        var shippingIncluded = false;
        var condition = "very good";
        var description = "test description";
        var datePosted = "4/15/23";

        // Create ListingFields object
        var listingFields =
                listingController.createListingFields(
                        cost, shippingIncluded, condition, description, datePosted);

        // Define Listing parameters
        var title = "test title";
        var url = "test url";
        List<String> imageUrl = new ArrayList<>();
        imageUrl.add(url);

        // Create Listing object
        var listing =
                listingController.createListing(
                        0, DISCORD_ID_1, title, url, imageUrl, listingFields);

        // Check to see if listing is initially null
        assertThat(userController.getCurrentListing(DISCORD_ID_1)).isNull();

        // Set Listing
        userController.setCurrentListing(DISCORD_ID_1, listing);

        // Check to see if listing has been set and if the same listing is returned
        assertThat(userController.getCurrentListing(DISCORD_ID_1)).isNotNull();
        assertThat(userController.getCurrentListing(DISCORD_ID_1)).isEqualTo(listing);

        // Check we can set the listing back to null
        userController.setCurrentListing(DISCORD_ID_1, null);
        assertThat(userController.getCurrentListing(DISCORD_ID_1)).isNull();
    }

    @Test
    void testGetUserForMemberIdCreatesAndReturnsANewUserCorrectly() {
        // First check user collection is empty to start
        assertThat(userController.getSizeUserCollection()).isEqualTo(0);

        // Create and add a new user to the collection
        var testUser1 = userController.getUserForMemberId(DISCORD_ID_1);

        // Check that passing the same discord id returns the same user
        var userIdTestUser1 = testUser1.getDiscordUserId();
        assertThat(userController.getUserForMemberId(userIdTestUser1)).isEqualTo(testUser1);

        // Makes sure that when a different id is passed a different user is returned
        assertThat(userController.getUserForMemberId(GUILD_ID_2)).isNotEqualTo(testUser1);
    }

    @Test
    void testRemoveUserByMemberAndGuildIdRemovesUserCorrectly() {
        // First check user collection is empty to start
        assertThat(userController.getSizeUserCollection()).isEqualTo(0);

        // Create and add a new user to the collection
        var testUser1 = userController.getUserForMemberId(DISCORD_ID_1);
        userController.setGuildIdForUser(testUser1.getDiscordUserId(), GUILD_ID_1);

        // Check that size of collection increased to 1
        assertThat(userController.getSizeUserCollection()).isEqualTo(1);

        // Remove the user and check if collection reduced size back to 0
        userController.removeUserByMemberAndGuildId(DISCORD_ID_1, GUILD_ID_1);
        assertThat(userController.getSizeUserCollection()).isEqualTo(0);
    }

    @Test
    void testRemoveUserByMemberAndGuildIdDoesNotRemoveIfPassedBadValues() {
        // First check user collection is empty to start
        assertThat(userController.getSizeUserCollection()).isEqualTo(0);

        // Create and add a new user to the collection
        var testUser1 = userController.getUserForMemberId(DISCORD_ID_1);
        userController.setGuildIdForUser(testUser1.getDiscordUserId(), GUILD_ID_1);

        // Check that size of collection increased to 1
        assertThat(userController.getSizeUserCollection()).isEqualTo(1);

        // Passed different guild id than the one used to create the user
        userController.removeUserByMemberAndGuildId(DISCORD_ID_1, GUILD_ID_2);
        assertThat(userController.getSizeUserCollection()).isEqualTo(1);

        // Passed different user id than one used to create the user
        userController.removeUserByMemberAndGuildId(DISCORD_ID_2, GUILD_ID_1);
        assertThat(userController.getSizeUserCollection()).isEqualTo(1);
    }
}