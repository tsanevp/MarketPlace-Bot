package edu.northeastern.cs5500.starterbot.exceptions;

/** If a error occurs while building a Listing object, an exception is thrown. */
public class BuildListingFailedException extends Exception {
    public BuildListingFailedException(String e) {
        super(e);
    }
}
