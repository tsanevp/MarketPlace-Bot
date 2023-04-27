package edu.northeastern.cs5500.starterbot.exceptions;

/** If a error occurs while building a ListingFields object, an exception is thrown. */
public class BuildListingFieldsFailedException extends Exception {
    public BuildListingFieldsFailedException(String e) {
        super(e);
    }
}
