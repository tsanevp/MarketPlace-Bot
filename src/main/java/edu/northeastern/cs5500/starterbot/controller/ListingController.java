package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import org.bson.Document;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class ListingController {

    GenericRepository<Listing> listingRepository;

    @Inject
    ListingController(GenericRepository<Listing> listingRepository) {
        this.listingRepository = listingRepository;
    }

    public void setListing(List<MessageEmbed> currentListings, Long messageId, String discordUserId) {
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

    // Returns a list of MessageEmbed listings of a specifc user
    public List<List<MessageEmbed>> getListingsMessagesForMemberId(String discordUserId) {
        FindIterable<Listing> listings = filterListingsByMembersId(discordUserId);
        if (listings != null) {
            return findIterableToMessageEmbedList(listings);
        } else {
            return Collections.emptyList();
        }
    }

    // Helper method for getListingMessagesForMemberId and deleteListingsForUser. It searches for
    // the listings
    // of the discordUserId and returns the results as a FindIterable of the Listings object.
    public FindIterable<Listing> filterListingsByMembersId(String discordUserId) {
        return this.listingRepository.filter(getBsonFilterByMembersId(discordUserId));
    }

    public Long countListingsByMemberId(String discordUserId) {
        return this.listingRepository.countDocuments(getBsonFilterByMembersId(discordUserId));
    }

    public Bson getBsonFilterByMembersId(String discordUserId) {
        return Filters.eq("discordUserId", discordUserId);
    }

    // Returns a list of MessageEmbed listings of a specific keyword
    public List<List<MessageEmbed>> getListingsForKeyword(String keyword) {
        Bson filter = Filters.text(keyword);
        FindIterable<Listing> listingForKeyword = this.listingRepository.filter(filter);
        if (listingForKeyword != null) {
            return findIterableToMessageEmbedList(listingForKeyword);
        } else {
            return Collections.emptyList();
        }
    }

    // Returns all listings in the collection.
    public List<List<MessageEmbed>> getAllListings() {
        Collection<Listing> lists = this.listingRepository.getAll();
        List<List<MessageEmbed>> allListingMessages = new ArrayList<>(new ArrayList<>());
        for (Listing l : lists) {
            allListingMessages.add(toMessageEmbed(l));
        }
        return allListingMessages;
    }

    // Returns the listing MessageEmbed when given the object id.
    public List<MessageEmbed> getListingById(ObjectId id) {
        return toMessageEmbed(this.listingRepository.get(id));
    }

    // Helper function that converts the result of the filter into a MessageEmbed
    public List<List<MessageEmbed>> findIterableToMessageEmbedList(FindIterable<Listing> listing) {
        List<List<MessageEmbed>> allListingMessages = new ArrayList<>(new ArrayList<>());
        for (Listing l : listing) {
            allListingMessages.add(toMessageEmbed(l));
        }
        return allListingMessages;
    }

    public Listing toListingObject(List<MessageEmbed> currentListings) {
        MessageEmbed currentListingAsBuilder = currentListings.get(0);
        ArrayList<String> imagesString = new ArrayList<>();
        for (MessageEmbed messageEmbed : currentListings) {
            imagesString.add(messageEmbed.getImage().getUrl());
        }
        Listing listing = new Listing();
        listing.setImages(imagesString);
        listing.setMessageId(Long.valueOf(currentListingAsBuilder.getFooter().toString()));
        listing.setTitle(currentListingAsBuilder.getTitle());
        listing.setUrl(currentListingAsBuilder.getUrl());
        listing.setColor(currentListingAsBuilder.getColorRaw());
        listing.setDescription(currentListingAsBuilder.getDescription());
        List<Field> listingFields = currentListingAsBuilder.getFields();
        Document fieldsDocument = new Document();
        for (Field field : listingFields) {
            fieldsDocument.append(field.getName(), field.getValue());
        }
        listing.setFields(fieldsDocument);
        return listing;
    }

    // Helper function that turns Listing object to a MessageEmbed and returns it.
    public List<MessageEmbed> toMessageEmbed(Listing listing) {
        List<MessageEmbed> listingsMessage = new ArrayList<>();
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setColor(listing.getColor())
                        .setTitle(listing.getTitle(), listing.getUrl())
                        .setImage(listing.getImages().get(0));
        for (String key : listing.getFields().keySet()) {
            if ("Description".equals(key)) {
                embedBuilder.addField(key, (String) listing.getFields().get(key), false);
                continue;
            }
            embedBuilder.addField(key, (String) listing.getFields().get(key), true);
        }

        MessageEmbed messageEmbed = embedBuilder.build();
        listingsMessage.add(messageEmbed);
        if (listing.getImages().size() > 1) {
            for (int i = 1; i < listing.getImages().size(); i++) {
                EmbedBuilder embedBuilders =
                        new EmbedBuilder()
                                .setColor(messageEmbed.getColorRaw())
                                .setTitle(messageEmbed.getTitle(), messageEmbed.getUrl())
                                .setImage(listing.getImages().get(i));
                listingsMessage.add(embedBuilders.build());
            }
        }
        return listingsMessage;
    }
}
