package edu.northeastern.cs5500.starterbot.model;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/*
 * Represents the model of the listing that is stored in MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing implements Model {

    // MongoDB id
    ObjectId id;

    // MessageEmbed id
    long messageId;

    // the userid of the account who created the listing
    @Nonnull String discordUserId;

    // title of the listing
    @Nonnull String title;

    // url of the listing
    @Nonnull String url;

    // list of images url
    @Nonnull List<String> images;

    // additional fields of message
    ListingFields fields;
}
