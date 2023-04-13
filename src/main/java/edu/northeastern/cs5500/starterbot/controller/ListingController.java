package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.Listing.ListingBuilder;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import edu.northeastern.cs5500.starterbot.model.ListingFields.ListingFieldsBuilder;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class ListingController {

    GenericRepository<Listing> listingRepository;

    @Inject
    ListingController(GenericRepository<Listing> listingRepository) {
        this.listingRepository = listingRepository;
    }

    public void addListing(Listing listing) {
        listingRepository.add(Objects.requireNonNull(listing));
    }

    public Listing createListing(@Nonnull List<String> imagesUrl, long messageId, @Nonnull String title, @Nonnull String url, @Nonnull String discordUserId, 
    ListingFields fields) {

        ListingBuilder listingBuilder = Listing.builder();
        listingBuilder
                .images(imagesUrl)
                .messageId(messageId)
                .title(title)
                .url(url)
                .discordUserId(discordUserId)
                .fields(fields);

        return listingBuilder.build();
    }

    public ListingFields createListingFields(@Nonnegative int cost, boolean shippingIncluded, @Nonnull String condition, @Nonnull String description) {
        ListingFieldsBuilder listingFieldsBuilder = ListingFields.builder();
        listingFieldsBuilder
            .cost(cost)
            .description(description)
            .shippingIncluded(shippingIncluded)
            .condition(condition);
        
        return listingFieldsBuilder.build();
    }

    public void deleteListingsForUser(String discordMemberId) {
        for (Listing listing : getListingsByMemberId(discordMemberId)) {
            listingRepository.delete(Objects.requireNonNull(listing.getId()));
        }
    }

    public void deleteListingById(@Nonnull ObjectId objectId) {
        listingRepository.delete(objectId);
    }

    public int countListingsByMemberId(String discordUserId) {
        return getListingsByMemberId(discordUserId).size();
    }

    public Collection<Listing> getListingsWithKeyword(String keyword) {
        return getAllListings().stream()
                .filter(listing -> listing.getTitle().contains(keyword))
                .toList();
    }

    public Collection<Listing> getAllListings() {
        return listingRepository.getAll();
    }

    public Collection<Listing> getListingsByMemberId(String discordMemberId) {
        return getAllListings().stream()
                .filter(listing -> listing.getDiscordUserId().equals(discordMemberId))
                .toList();
    }
}