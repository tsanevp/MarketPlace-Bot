package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "MONGODB_URI", matches = ".+")
class ListingControllerTest {
    static final String USER_ID = "631666734125987209";
    static final String GUILD_ID = "294764645159495548";
    static final List<String> IMAGES_URL = new ArrayList<>(Arrays.asList("Test url", "test"));
    static final long MESSAGEID = 123455677;
    static final String COST = "123";
    static final String TITLE = "test";
    static final String URL = "test url";
    static final String DESCRIPTION = "test description";
    static final String DATE = "test date";
    static final String CONDITION = "Good";
    static final boolean SHIPPINGINCLUDED = false;
    static final ListingFields listingFields =
            ListingFields.builder()
                    .cost(COST)
                    .description(DESCRIPTION)
                    .shippingIncluded(SHIPPINGINCLUDED)
                    .condition(CONDITION)
                    .datePosted(DATE)
                    .build();
    static final Listing testListing =
            Listing.builder()
                    .id(new ObjectId())
                    .messageId(MESSAGEID)
                    .discordUserId(USER_ID)
                    .guildId(GUILD_ID)
                    .title(TITLE)
                    .url(URL)
                    .images(IMAGES_URL)
                    .fields(listingFields)
                    .build();

    private ListingController getListingController() {
        ListingController listingController = new ListingController(new InMemoryRepository<>());
        return listingController;
    }

    @Test
    void testAddListingActuallyAddsListing() {
        // setup
        ListingController listingController = getListingController();
        Collection<Listing> testCollection = Arrays.asList(testListing);

        // precondition
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isNotEqualTo(testCollection);

        // mutation
        listingController.addListing(testListing);

        // postcondition
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isEqualTo(testCollection);

        // cleans database
        listingController.deleteListingById(testListing.getId(), USER_ID);
    }

    @Test
    void testDeleteListingByMemberIdActuallyDeletesListing() {
        ListingController listingController = getListingController();

        // precondition
        listingController.addListing(testListing);
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();

        // mutation
        assertTrue(listingController.deleteListingsForUser(USER_ID, GUILD_ID));

        // post
        assertFalse(listingController.deleteListingsForUser(USER_ID, GUILD_ID));
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isEmpty();
    }

    @Test
    void testDeleteListingByIdActuallyDeletesListing() {
        // setup
        ListingController listingController = getListingController();

        // precondition
        listingController.addListing(testListing);
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();

        // mutation
        assertTrue(
                listingController.deleteListingById(
                        testListing.getId(), testListing.getDiscordUserId()));

        // post
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isEmpty();
        assertFalse(
                listingController.deleteListingById(
                        testListing.getId(), testListing.getDiscordUserId()));
    }

    @Test
    void testCountListingsByMemberId() {
        // setup
        ListingController listingController = getListingController();

        // precondition
        assertThat(listingController.countListingsByMemberId(USER_ID, GUILD_ID)).isEqualTo(0);

        // mutation
        listingController.addListing(testListing);

        // post
        assertThat(listingController.countListingsByMemberId(USER_ID, GUILD_ID)).isEqualTo(1);

        // clean
        listingController.deleteListingsForUser(USER_ID, GUILD_ID);
    }

    @Test
    void getListingsWithKeyword() {
        // setup
        ListingController listingController = getListingController();

        // precondition
        assertThat(listingController.getListingsWithKeyword("test", GUILD_ID)).isNotNull();

        // mutation
        listingController.addListing(testListing);
        Collection<Listing> testCollection = Arrays.asList(testListing);

        // post
        assertThat(listingController.getListingsWithKeyword("test", GUILD_ID))
                .isEqualTo(testCollection);

        // clean
        listingController.deleteListingsForUser(USER_ID, GUILD_ID);
    }

    @Test
    void testGetListingsByMemberId() {
        // setup
        ListingController listingController = getListingController();
        Collection<Listing> testCollection = Arrays.asList(testListing);

        // precondition
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotNull();
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isEmpty();

        // mutation
        listingController.addListing(testListing);

        // post
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isEqualTo(testCollection);
        ;
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();

        // clean
        listingController.deleteListingsForUser(USER_ID, GUILD_ID);
    }

    @Test
    void testGetListingById() {
        // setup
        ListingController listingController = getListingController();

        // precondition
        assertThat(listingController.getListingById(testListing.getId())).isNull();

        // mutation
        listingController.addListing(testListing);

        // post
        assertThat(listingController.getListingById(testListing.getId())).isEqualTo(testListing);
        ;
        assertThat(listingController.getListingById(testListing.getId())).isNotNull();

        // clean
        listingController.deleteListingsForUser(USER_ID, GUILD_ID);
    }

    @Test
    void testGetAllListingsInGuild() {
        // setup
        ListingController listingController = getListingController();
        Collection<Listing> testCollection = Arrays.asList(testListing);

        // precondition
        assertThat(listingController.getAllListingsInGuild(GUILD_ID)).isNotNull();
        assertThat(listingController.getAllListingsInGuild(GUILD_ID)).isEmpty();

        // mutation
        listingController.addListing(testListing);

        // post
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isEqualTo(testCollection);
        ;
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();

        // clean
        listingController.deleteListingsForUser(USER_ID, GUILD_ID);
    }
}
