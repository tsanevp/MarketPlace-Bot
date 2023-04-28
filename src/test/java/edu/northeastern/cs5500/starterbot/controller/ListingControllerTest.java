package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
@SuppressWarnings("null")
class ListingControllerTest {
    static final String USER_ID = "631666734125987209";
    static final String GUILD_ID = "294764645159495548";
    static final String TITLE = "test";
    static final Long MESSAGE_ID = 1234567L;
    static final List<String> IMAGES = new ArrayList<>(Arrays.asList("Test url", "test"));
    static final String URL = "test_url";
    ListingFields LISTING_FIELDS;
    Listing TEST_LISTING;
    ListingController listingController;

    @BeforeAll
    void createListing() {
        LISTING_FIELDS =
                ListingFields.builder()
                        .cost("123")
                        .description("test description")
                        .shippingIncluded(false)
                        .condition("Good")
                        .datePosted("test date")
                        .build();

        Objects.requireNonNull(LISTING_FIELDS);

        TEST_LISTING =
                Listing.builder()
                        .id(new ObjectId())
                        .messageId(MESSAGE_ID)
                        .discordUserId(USER_ID)
                        .guildId(GUILD_ID)
                        .title(TITLE)
                        .url(URL)
                        .images(IMAGES)
                        .fields(LISTING_FIELDS)
                        .build();
    }

    @BeforeEach
    void getListingController() {
        // setup
        listingController = new ListingController(new InMemoryRepository<>());
    }

    @Test
    void testAddListingActuallyAddsListing() {
        // setup
        Collection<Listing> testCollection = Arrays.asList(TEST_LISTING);

        // precondition
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isNotEqualTo(testCollection);

        // mutation
        listingController.addListing(TEST_LISTING);

        // postcondition
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isEqualTo(testCollection);
    }

    @Test
    void testDeleteCollectionOfListings() {
        // precondition
        var testCollection = new ArrayList<Listing>();
        assertThat(listingController.deleteCollectionOfListings(testCollection)).isFalse();

        Listing ListingWithNullObjectId = TEST_LISTING;
        ListingWithNullObjectId.setId(null);
        // mutation
        testCollection.add(TEST_LISTING);
        testCollection.add(ListingWithNullObjectId);

        // post
        assertThat(listingController.deleteCollectionOfListings(testCollection)).isTrue();
    }

    @Test
    void testDeleteListingByIdActuallyDeletesListing() {
        // precondition
        listingController.addListing(TEST_LISTING);
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();

        // mutation
        assertThat(listingController.deleteListingById(TEST_LISTING.getId(), USER_ID)).isTrue();

        // post
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isEmpty();
        assertThat(listingController.deleteListingById(TEST_LISTING.getId(), USER_ID)).isFalse();

        Listing listingNotMatch =
                Listing.builder()
                        .id(new ObjectId())
                        .messageId(MESSAGE_ID)
                        .discordUserId("different user")
                        .guildId(GUILD_ID)
                        .title(TITLE)
                        .url(URL)
                        .images(IMAGES)
                        .fields(LISTING_FIELDS)
                        .build();

        listingController.addListing(listingNotMatch);

        assertThat(listingController.deleteListingById(listingNotMatch.getId(), USER_ID)).isFalse();
    }

    @Test
    void testCountListingsByMemberId() {
        // precondition
        assertThat(listingController.countListingsByMemberId(USER_ID, GUILD_ID)).isEqualTo(0);

        // mutation
        listingController.addListing(TEST_LISTING);

        // post
        assertThat(listingController.countListingsByMemberId(USER_ID, GUILD_ID)).isEqualTo(1);
    }

    @Test
    void getListingsWithKeyword() {
        // precondition
        assertThat(listingController.getListingsWithKeyword(TITLE, GUILD_ID)).isNotNull();

        // mutation
        listingController.addListing(TEST_LISTING);
        Collection<Listing> testCollection = Arrays.asList(TEST_LISTING);

        // post
        assertThat(listingController.getListingsWithKeyword(TITLE, GUILD_ID))
                .isEqualTo(testCollection);
    }

    @Test
    void testGetListingsByMemberId() {
        // setup
        Collection<Listing> testCollection = Arrays.asList(TEST_LISTING);

        // precondition
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotNull();
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isEmpty();

        // mutation
        listingController.addListing(TEST_LISTING);

        // post
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isEqualTo(testCollection);
        ;
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();
    }

    @Test
    void testGetListingById() {
        // precondition
        assertThat(listingController.getListingById(TEST_LISTING.getId())).isNull();

        // mutation
        listingController.addListing(TEST_LISTING);

        // post
        assertThat(listingController.getListingById(TEST_LISTING.getId())).isEqualTo(TEST_LISTING);
        ;
        assertThat(listingController.getListingById(TEST_LISTING.getId())).isNotNull();
    }

    @Test
    void testGetAllListingsInGuild() {
        Collection<Listing> testCollection = Arrays.asList(TEST_LISTING);

        // precondition
        assertThat(listingController.getListingsInGuild(GUILD_ID)).isNotNull();
        assertThat(listingController.getListingsInGuild(GUILD_ID)).isEmpty();

        // mutation
        listingController.addListing(TEST_LISTING);

        // post
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID))
                .isEqualTo(testCollection);
        ;
        assertThat(listingController.getListingsByMemberId(USER_ID, GUILD_ID)).isNotEmpty();
    }
}
