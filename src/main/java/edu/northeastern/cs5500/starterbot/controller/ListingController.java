package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
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
    public void addListing(@Nonnull Listing listing) {
        listingRepository.add(listing);
    }

    /**
     * Deletes the all the listings of a specific user.
     *
     * @param discordMemberId - The userId of the discord user.
     * @param guild - The guild in which the listings is contained in.
     * @returns Whether listing is successfully deleted.
     */
    public boolean deleteListingsForUser(@Nonnull String discordMemberId, @Nonnull String guildId) {
        if (countListingsByMemberId(discordMemberId, guildId) == 0) {
            return false;
        }

        for (Listing listing : getListingsByMemberId(discordMemberId, guildId)) {
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
     * @param objectId - The objectId of the listing in the database.
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
     * @param discordUserId - The userId of a discord user.
     * @param guild - The guild in which the listing is contained in.
     * @return Number of listings.
     */
    public int countListingsByMemberId(@Nonnull String discordUserId, @Nonnull String guildId) {
        return getListingsByMemberId(discordUserId, guildId).size();
    }

    /**
     * Retrieves all listings where the title contains a keyword.
     *
     * @param keyword - The keyword the user would like to search.
     * @param guild - The guild in which the listing is contained in.
     * @return A collection of listings.
     */
    public Collection<Listing> getListingsWithKeyword(
            @Nonnull String keyword, @Nonnull String guildId) {
        return getAllListingsInGuild(guildId).stream()
                .filter(listing -> listing.getTitle().contains(keyword))
                .toList();
    }

    /**
     * Retrieves all listings of a specific discord user.
     *
     * @param discordMemberId - The userId of the discord user.
     * @param guild - The guild in which the listing is contained in.
     * @return A collection of listings.
     */
    public Collection<Listing> getListingsByMemberId(
            @Nonnull String discordMemberId, @Nonnull String guildId) {
        return getAllListingsInGuild(guildId).stream()
                .filter(listing -> listing.getDiscordUserId().equals(discordMemberId))
                .toList();
    }

    /**
     * Retrieves all listings in a specific guild.
     *
     * @param guild - The guild that the listings contain in.
     * @return A collection of listings.
     */
    public Collection<Listing> getAllListingsInGuild(@Nonnull String guildId) {
        return listingRepository.getAll().stream()
                .filter(listing -> listing.getGuildId().equals(guildId))
                .toList();
    }

    /**
     * Retrieves listing by object id.
     *
     * @param objectId - The object id of the listing.
     * @return A listing
     */
    public Listing getListingById(@Nonnull ObjectId objectId) {
        return listingRepository.get(objectId);
    }
}
