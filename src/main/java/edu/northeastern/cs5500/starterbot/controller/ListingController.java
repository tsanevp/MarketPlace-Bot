package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.Listing.ListingBuilder;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
import edu.northeastern.cs5500.starterbot.model.ListingFields.ListingFieldsBuilder;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    public Listing createListing(
            long messageId,
            @Nonnull String discordUserId,
            @Nonnull String title,
            @Nonnull String url,
            @Nonnull List<String> imagesUrl,
            ListingFields fields,
            boolean posted) {

        ListingBuilder listingBuilder = Listing.builder();
        listingBuilder
                .messageId(messageId)
                .discordUserId(discordUserId)
                .title(title)
                .url(url)
                .images(imagesUrl)
                .fields(fields)
                .posted(posted);

        return listingBuilder.build();
    }

    public ListingFields createListingFields(
            @Nonnull List<String> cost,
            boolean shippingIncluded,
            @Nonnull String condition,
            @Nonnull String description,
            @Nonnull String datePosted) {
        ListingFieldsBuilder listingFieldsBuilder = ListingFields.builder();
        listingFieldsBuilder
                .cost(cost)
                .description(description)
                .shippingIncluded(shippingIncluded)
                .condition(condition)
                .datePosted(datePosted);

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

    public Collection<Listing> getTempListingByMemberId(String discordMemberId) {
        return getAllListings().stream()
                .filter(listing -> listing.getDiscordUserId().equals(discordMemberId))
                // .filter(listing -> listing.getPosted().equals(discordMemberId))
                .toList();
    }
}
