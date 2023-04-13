package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

/*
 * Represents the additional fields of the listing message
 */

@Data
@Builder
public class ListingFields {

    // cost of item
    @Nonnegative int cost;

    // whether shipping is included in the cost of item
    boolean shippingIncluded;

    // condition of the item
    @Nonnull String condition;

    // description of item listed
    @Nonnull String description;

    // original date posted of the listing
    @Nonnull String datePosted;
}
