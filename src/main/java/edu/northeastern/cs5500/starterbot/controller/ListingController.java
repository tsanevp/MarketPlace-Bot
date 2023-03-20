package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.DBObject;
import com.mongodb.lang.Nullable;

import edu.northeastern.cs5500.starterbot.model.ListingCollection;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;

import java.util.Collection;
import java.util.function.DoubleBinaryOperator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;

public class ListingController {

    InMemoryRepository<ListingCollection> listingRepository;

    @Inject
    ListingController(InMemoryRepository<ListingCollection> listingRepository) {
        this.listingRepository = listingRepository;

        if (listingRepository.count() == 0) {
            ListingCollection listingCollection = new ListingCollection();
            listingCollection.setDiscordUserId("1234");
        }
    }

    // Should it return a DB or ... ?
    // public ListingCollection getListingsByUser(String discordUserId) {
    // }


    // public void addListingToUser(String discordUserId, DBObject listing) {
    // }

    // public Collection<ListingCollection> getAllListings() {
    //     return this.listingRepository.getAll();
    // }
}
