package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
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
     * @param guild - The guild in which the listing is contained in.
     * @returns Whether listing is successfully deleted.
     */
    public boolean deleteListingsForUser(String discordMemberId, String guildId) {
        if (countListingsByMemberId(discordMemberId, guildId) == 0) {
            return false;
        }
        for (Listing listing : getListingsByMemberId(discordMemberId, guildId)) {
            listingRepository.delete(listing.getId());
        }
        return true;
    }

    /**
     * Deletes the listings with a specified objectId.
     *
     * @param objectId - The objectId of the listing in the database.
     * @param guild - The guild in which the listing is contained in.
     * @returns Whether listing is successfully deleted.
     */
    public boolean deleteListingById(@Nonnull ObjectId objectId, @Nonnull String discordMemberId) {
        if (listingRepository.get(objectId).getDiscordUserId().equals(discordMemberId)) {
            listingRepository.delete(objectId);
            return true;
        }
        return false;
    }

    /**
     * Counts the number of listings that a specific discord user has.
     *
     * @param discordUserId - The userId of a discord user.
     * @param guild - The guild in which the listing is contained in.
     * @return Number of listings.
     */
    public int countListingsByMemberId(String discordUserId, String guildId) {
        return getListingsByMemberId(discordUserId, guildId).size();
    }

    /**
     * Retrieves all listings where the title contains a keyword.
     *
     * @param keyword - The keyword the user would like to search.
     * @param guild - The guild in which the listing is contained in.
     * @return A collection of listings.
     */
    public Collection<Listing> getListingsWithKeyword(String keyword, String guildId) {
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
    public Collection<Listing> getListingsByMemberId(String discordMemberId, String guildId) {
        return getAllListingsInGuild(guildId).stream()
                .filter(listing -> listing.getDiscordUserId().equals(discordMemberId))
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

    /**
     * Retrieves all listings in a specific guild.
     *
     * @param guild - The guild that the listings contain in.
     * @return A collection of listings.
     */
    public Collection<Listing> getAllListingsInGuild(String guildId) {
        return listingRepository.getAll().stream()
                .filter(listing -> listing.getGuildId().equals(guildId))
                .toList();
    }
}
