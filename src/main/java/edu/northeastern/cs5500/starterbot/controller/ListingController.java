package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
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

    /**
     * Adds Listing object to the repository
     *
     * @param listing - The listing object.
     */
    public void addListing(@Nonnull Listing listing) {
        listingRepository.add(listing);
    }

    /**
     * Deletes all the listings in the list.
     *
     * @param listings - The collection of listings that needs to be deleted.
     * @returns Whether listing is successfully deleted.
     */
    public boolean deleteCollectionOfListings(@Nonnull List<Listing> listings) {
        if (listings.isEmpty()) {
            return false;
        }

        for (Listing listing : listings) {
            var listingObjectId = listing.getId();
            if (Objects.nonNull(listingObjectId)) {
                listingRepository.delete(listingObjectId);
            }
        }
        return true;
    }

    /**
     * Deletes the listing with a specified objectId.
     *
     * @param objectId - The id of the listing in the database.
     * @param discordMemberId - The id of the user.
     * @returns Whether listing is successfully deleted.
     */
    public boolean deleteListingById(@Nonnull ObjectId objectId, @Nonnull String discordMemberId) {
        Listing listing = getListingById(objectId);

        if (listing == null || !listing.getDiscordUserId().equals(discordMemberId)) {
            return false;
        }

        listingRepository.delete(objectId);
        return true;
    }

    /**
     * Counts the number of listings that a specific discord user has.
     *
     * @param discordMemberId - The id of a discord user.
     * @param guildId - The id of the guild in which the listing is contained in.
     * @return The number of listings.
     */
    @Nonnegative
    public int countListingsByMemberId(@Nonnull String discordMemberId, @Nonnull String guildId) {
        return getListingsByMemberId(discordMemberId, guildId).size();
    }

    /**
     * Retrieves all listings where the title contains a keyword.
     *
     * @param keyword - The keyword the user would like to search.
     * @param guildId - The id of the guild in which the listing is contained in.
     * @return A list of listings.
     */
    @Nonnull
    public List<Listing> getListingsWithKeyword(@Nonnull String keyword, @Nonnull String guildId) {
        var keywordLower = keyword.toLowerCase();
        List<Listing> lists =
                getListingsInGuild(guildId).stream()
                        .filter(
                                listing ->
                                        (listing.getTitle().toLowerCase()).contains(keywordLower))
                        .toList();

        if (lists == null) {
            return new ArrayList<>();
        }
        return lists;
    }

    /**
     * Retrieves all listings of a specific discord user.
     *
     * @param discordMemberId - The id of the discord user.
     * @param guildId - The id of the guild in which the listing is contained in.
     * @return A list of listings.
     */
    @Nonnull
    public List<Listing> getListingsByMemberId(
            @Nonnull String discordMemberId, @Nonnull String guildId) {
        List<Listing> lists =
                getListingsInGuild(guildId).stream()
                        .filter(listing -> listing.getDiscordUserId().equals(discordMemberId))
                        .toList();

        if (lists == null) {
            return new ArrayList<>();
        }
        return lists;
    }

    /**
     * Retrieves all listings in a specific guild.
     *
     * @param guildId - The id of the guild that the listings contain in.
     * @return A list of listings.
     */
    @Nonnull
    public List<Listing> getListingsInGuild(@Nonnull String guildId) {
        List<Listing> lists =
                listingRepository.getAll().stream()
                        .filter(listing -> listing.getGuildId().equals(guildId))
                        .toList();

        if (lists == null) {
            return new ArrayList<>();
        }
        return lists;
    }

    /**
     * Retrieves listing by id.
     *
     * @param objectId - The id of the listing.
     * @return A listing or null if listing does not exist.
     */
    @Nullable
    public Listing getListingById(@Nonnull ObjectId objectId) {
        return listingRepository.get(objectId);
    }
}
