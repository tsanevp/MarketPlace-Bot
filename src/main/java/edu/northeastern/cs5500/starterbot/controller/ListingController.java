package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.client.FindIterable;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import org.bson.Document;
import org.bson.types.ObjectId;

public class ListingController {

    GenericRepository<Listing> listingRepository;

    @Inject
    ListingController(GenericRepository<Listing> listingRepository) {
        this.listingRepository = listingRepository;
    }

    public void setListing(
            List<MessageEmbed> currentListings, Long messageId, String discordUserId) {
        Listing listing = new Listing();
        MessageEmbed currentListingAsBuilder = currentListings.get(0);

        ArrayList<String> images = new ArrayList<>();
        for (MessageEmbed messageEmbed : currentListings) {
            images.add(messageEmbed.getImage().getUrl());
        }
        listing.setImages(images);
        listing.setMessageId(messageId);
        listing.setTitle(currentListingAsBuilder.getTitle());
        listing.setUrl(currentListingAsBuilder.getUrl());
        listing.setColor(currentListingAsBuilder.getColorRaw());
        listing.setDiscordUserId(discordUserId);
        listing.setDescription(currentListingAsBuilder.getDescription());
        Document fieldsDocument = new Document();
        for (Field field : currentListingAsBuilder.getFields()) {
            fieldsDocument.append(field.getName(), field.getValue());
        }
        listing.setFields(fieldsDocument);
        this.listingRepository.add(listing);
    }

    // Deletes the listing of a specific user in the collection.
    public void deleteListingsForUser(String discordMemberId) {
        FindIterable<Listing> listingsToDelete = filterListingsByMembersId(discordMemberId);
        for (Listing l : listingsToDelete) {
            this.listingRepository.delete(l.getId());
        }
    }

    // Deletes a specific listing in the collection.
    public void deleteListingById(@Nonnull ObjectId ObjectId) {
        this.listingRepository.delete(ObjectId);
    }
}
