package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Represents the additional fields of the listing message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingFields {

    // Cost of item
    @Nonnull String cost;

    // Whether shipping is included in the cost of item
    @Nonnull Boolean shippingIncluded;

    // Condition of the item
    @Nonnull String condition;

    // Description of item listed
    @Nonnull String description;

    // Original date posted of the listing
    @Nonnull String datePosted;
}
