package edu.northeastern.cs5500.starterbot.model;

import java.util.List;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

/*
 * Represents the additional fields of the listing message
 */
@Data
@Builder
public class ListingFields {

    // Cost of item
    @Nonnull List<String> cost;

    // Whether shipping is included in the cost of item
    boolean shippingIncluded;

    // Condition of the item
    @Nonnull String condition;

    // Description of item listed
    @Nonnull String description;

    // Original date posted of the listing
    @Nonnull String datePosted;
}
