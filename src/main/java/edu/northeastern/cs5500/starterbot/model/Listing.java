package edu.northeastern.cs5500.starterbot.model;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

/*
 * Represents the model of the listing that is stored in MongoDB
 */
@Data
@Builder
public class Listing implements Model {

    // MongoDB id
    ObjectId id;

    // MessageEmbed id
    final long messageId;

    // the userid of the account who created the listing
    @Nonnull final String discordUserId;

    // title of the listing
    @Nonnull String title;

    // url of the listing
    @Nonnull String url;

    // description of item listed
    @Nonnull String description;

    // list of images url
    @Nonnull List<String> images;

    // additional fields of message
    ListingFields fields;
}
