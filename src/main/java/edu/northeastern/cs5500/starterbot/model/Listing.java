package edu.northeastern.cs5500.starterbot.model;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
@Builder
public class Listing implements Model {
    ObjectId id;

    final long messageId;

    @Nonnull final String discordUserId;

    @Nonnull String title;

    @Nonnull String url;

    @Nonnull String description;

    @Nonnull ArrayList<String> images;

    int color;

    ListingFields fields;
}

//create tostring