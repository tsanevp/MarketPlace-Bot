package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "MONGODB_URI", matches = ".+")
class ListingControllerTest {
    static final String USER_ID_1 = "631666734125987209";
    static final String USER_ID_2 = "294764645159495548";
    static final List<String> IMAGES_URL = new ArrayList<>(Arrays.asList("Test url", "test"));
    static final long MESSAGEID = 123455677;
    static final String COST = "123";
    static final String TITLE = "test";
    static final String URL = "test url";
    static final String DESCRIPTION = "test description";
    static final String DATE = "test date";
    static final String CONDITION = "Good";
    static final boolean SHIPPINGINCLUDED = false;
    static final ListingFields listingFields = ListingFields.builder().cost(COST).description(DESCRIPTION).shippingIncluded(SHIPPINGINCLUDED).condition(CONDITION).datePosted(DATE).build();
    static final Listing testListing = Listing.builder().messageId(MESSAGEID).discordUserId(USER_ID_1).title(TITLE).url(URL).images(IMAGES_URL).fields(listingFields).build();

    private ListingController getListingController() {
        ListingController listingController =
                new ListingController(new InMemoryRepository<>());
        return listingController;
    }

    @Test
    void testSetListingActuallySetsListing() {
        
        // setup
        ListingController listingController = getListingController();
        ListingFields listingField = listingController.createListingFields(USER_ID_1, SHIPPINGINCLUDED, CONDITION, DESCRIPTION, DATE);
        Listing listing = listingController.createListing(MESSAGEID, USER_ID_1, TITLE, URL, IMAGES_URL, listingField);
        Collection<Listing> testListing = new ArrayList<Listing>();
        testListing.add(listing);

        // precondition
        assertThat(listingController.getListingsByMemberId(USER_ID_1))
                .isNotEqualTo(testListing);

        // mutation
        listingController.addListing(listing);

        // postcondition
        assertThat(listingController.getListingsByMemberId(USER_ID_1))
                .isEqualTo(testListing);
    }

    @Test
    void testDeleteListingByMemberIdActuallyDeletesListing() {
        ListingController listingController = getListingController();

        assertThat(listingController.getAllListings()).isNotEmpty();
        assertThat(listingController.getListingsByMemberId(USER_ID_1))
                .isNotEmpty();

        listingController.deleteListingsForUser(USER_ID_1);

        listingController.getListingsByMemberId(USER_ID_1);
        assertThat(listingController.getListingsByMemberId(USER_ID_1))
                .isEmpty();
    }

    @Test
    void testGetListings() {
        ListingController listingController = getListingController();

        assertThat(listingController.getListingsByMemberId(USER_ID_2)).isNull();
    }