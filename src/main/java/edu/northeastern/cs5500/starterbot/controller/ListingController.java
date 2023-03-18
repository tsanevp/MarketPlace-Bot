package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;

import edu.northeastern.cs5500.starterbot.model.ListingCollection;
import edu.northeastern.cs5500.starterbot.model.UserPreference;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;

public class ListingController {

    InMemoryRepository<ListingCollection> listingRepository;

    @Inject
    listingController(InMemoryRepository<ListingCollection> listingRepository) {
        this.listingRepository = listingRepository;

        if (listingRepository.count() == 0) {
            ListingCollection listingCollection = new ListingCollection();
            listingCollection.setDiscordUserId("1234");
        }
    }

    //addListingbyUser
    //getListingsByUser
    
}
