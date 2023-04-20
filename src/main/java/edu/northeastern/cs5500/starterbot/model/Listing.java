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
    @Nonnull ObjectId id;

    // MessageEmbed id
    long messageId;

    // The userid of the account who created the listing
    @Nonnull String discordUserId;

    // Title of the listing
    @Nonnull String title;

    // Url of the listing
    @Nonnull String url;

    // List of images url
    @Nonnull List<String> images;

    // The guild that the listing is contained in
    @Nonnull String guildId;

    // Additional fields of message
    ListingFields fields;
}
