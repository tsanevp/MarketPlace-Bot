package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListingFields {
    @Nonnegative int cost;

    boolean shipingIncluded;

    @Nonnull String condition;
}