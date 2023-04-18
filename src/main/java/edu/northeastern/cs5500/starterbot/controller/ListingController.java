package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.ListingFields;
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

    /**
     * Adds Listing object to the repository
     *
     * @param listing - The listing object.
     */
    public void addListing(Listing listing) {
        listingRepository.add(Objects.requireNonNull(listing));
    }

    /**
     * Creates the listing object.
     *
     * @param messageId - The messageId of the listing in the discord trading channel.
     * @param discordUserId - The userId of the discord user who created the listing.
     * @param title - The title of the listing.
     * @param url - The url of the listing.
     * @param imagesUrl - The url of the images in the listing.
     * @param fields - The fields of the message.
     * @return The listing object.
     */
    public Listing createListing(
            long messageId,
            @Nonnull String discordUserId,
            @Nonnull String title,
            @Nonnull String url,
            @Nonnull List<String> imagesUrl,
            ListingFields fields) {

        return Listing.builder()
                .messageId(messageId)
                .discordUserId(discordUserId)
                .title(title)
                .url(url)
                .images(imagesUrl)
                .fields(fields)
                .build();
    }

    /**
     * Creates the additional fields for the discord message.
     *
     * @param cost - The cost of the item.
     * @param shippingIncluded - Whether shipping is included.
     * @param condition - The condition of the item.
     * @param description - The description of the item being sold.
     * @return The listing fields as an object.
     */
    public ListingFields createListingFields(
            @Nonnull String cost,
            boolean shippingIncluded,
            @Nonnull String condition,
            @Nonnull String description,
            @Nonnull String datePosted) {

        return ListingFields.builder()
                .cost(cost)
                .description(description)
                .shippingIncluded(shippingIncluded)
                .condition(condition)
                .datePosted(datePosted)
                .build();
    }

    /**
     * Deletes the all the listings of a specific user.
     *
     * @param discordMemberId - The userId of the discord user.
     */
    public void deleteListingsForUser(String discordMemberId) {
        for (Listing listing : getListingsByMemberId(discordMemberId)) {
            listingRepository.delete(Objects.requireNonNull(listing.getId()));
        }
    }

    /**
     * Deletes the listings with a specified objectId.
     *
     * @param objectId - The objectId of the listing in the database.
     */
    public void deleteListingById(@Nonnull ObjectId objectId) {
        listingRepository.delete(objectId);
    }

    /**
     * Counts the number of listings that a specific discord user has.
     *
     * @param discordUserId - The userId of a discord user.
     * @return Number of listings.
     */
    public int countListingsByMemberId(String discordUserId) {
        return getListingsByMemberId(discordUserId).size();
    }

    /**
     * Retrieves all listings where the title contains a keyword.
     *
     * @param keyword - The keyword the user would like to search.
     * @return A collection of listings.
     */
    public Collection<Listing> getListingsWithKeyword(String keyword) {
        return getAllListings().stream()
                .filter(listing -> listing.getTitle().contains(keyword))
                .toList();
    }

    /**
     * Retrieves all listings of a specific discord user.
     *
     * @param discordMemberId - The userId of the discord user.
     * @return A collection of listings.
     */
    public Collection<Listing> getListingsByMemberId(String discordMemberId) {
        return getAllListings().stream()
                .filter(listing -> listing.getDiscordUserId().equals(discordMemberId))
                .toList();
    }

    /**
     * Retrieves all listings in the database.
     *
     * @return A collection of listings.
     */
    public Collection<Listing> getAllListings() {
        return listingRepository.getAll();
    }
}
