package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;

import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public class ListingController {

    GenericRepository<Listing> listingRepository;

    @Inject
    ListingController(GenericRepository<Listing> listingRepository) {
        this.listingRepository = listingRepository;

        if (listingRepository.count() == 0) {
            Listing listing = new Listing();
            listing.setTitle("Keyboard");
            listing.setUrl(null);
            listing.setDescription(null);;
            listing.setImages(null);;
            listing.setColor(null);
            listing.setDescription(null);
            this.listingRepository.add(listing);
        }
    }

    public void setListing(List<MessageEmbed> currentListings, String discordUserId) {
        Listing listing = new Listing();
        MessageEmbed currentListingAsBuilder = currentListings.get(0);

        ArrayList<String> images = new ArrayList<>();
        for (MessageEmbed messageEmbed : currentListings) {
            images.add(messageEmbed.getImage().getUrl());
        }
        listing.setImages(images);
        listing.setTitle(currentListingAsBuilder.getTitle());
        listing.setUrl(currentListingAsBuilder.getUrl());
        listing.setColor(currentListingAsBuilder.getColorRaw());
        listing.setDiscordUserId(discordUserId);
        listing.setDescription(currentListingAsBuilder.getDescription());
        listing.setFields(currentListingAsBuilder.getFields());
        this.listingRepository.add(listing);
    }

    public List<List<MessageEmbed>> getListingsForMemberId(String discordUserId) {
        Bson filter = Filters.eq("discordUserId", discordUserId);
        FindIterable<Listing> listingsForUser = this.listingRepository.filter(filter);
        return findIterableToMessageEmbedList(listingsForUser);
    }

    public List<List<MessageEmbed>> getListingsForKeyword(String keyword) {
        Bson filter = Filters.text(keyword);
        FindIterable<Listing> listingForKeyword = this.listingRepository.filter(filter);
        return findIterableToMessageEmbedList(listingForKeyword);
    }

    public List<List<MessageEmbed>> getAllListings() {
        Collection<Listing> lists = this.listingRepository.getAll();
        List<List<MessageEmbed>> allListingMessages = new ArrayList<>(new ArrayList<>());
        for (Listing l: lists) {
            allListingMessages.add(listingToMessageEmbed(l));
        }
        return allListingMessages;
    }

    public List<MessageEmbed> getListingById(ObjectId id) {
        return listingToMessageEmbed(this.listingRepository.get(id));
    }

    // Helper function that converts the result of the filter into a MessageEmbed
    public List<List<MessageEmbed>> findIterableToMessageEmbedList(FindIterable<Listing> listing) {
        List<List<MessageEmbed>> allListingMessages = new ArrayList<>(new ArrayList<>());
        for (Listing l: listing) {
            allListingMessages.add(listingToMessageEmbed(l));
        }
        return allListingMessages;
    }

    // Helper function that turns Listing object to a MessageEmbed and returns it.
    public List<MessageEmbed> listingToMessageEmbed(Listing listing) {
        List<MessageEmbed> listingsMessage = new ArrayList<>();
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setColor(listing.getColor())
                        .setTitle(
                                listing.getTitle(),
                                listing.getUrl())
                        .setImage(listing.getImages().get(0));
        for (Field key : listing.getFields()) {
            if ("Description".equals(key.getName())) {
                embedBuilder.addField(key.getName(), key.getValue(), false);
                continue;
            }
            embedBuilder.addField(key.getName(), key.getValue(), true);
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
