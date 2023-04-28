package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.lang.Nullable;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

/*
 * Represents the model of the listing that is stored in MongoDB.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing implements Model {

    // MongoDB id
    ObjectId id;

    // MessageEmbed id. This is null until a listing is posted
    @Nullable Long messageId;

    // The user id of the account who created the listing
    @Nonnull String discordUserId;

    // The guild that the listing is contained in
    @Nonnull String guildId;

    // Title of the listing
    @Nonnull String title;

    // Url of the listing
    @Nonnull String url;

    // List of images url
    @Nonnull List<String> images;

    // Additional fields of message
    @Nonnull ListingFields fields;
}
