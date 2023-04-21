package edu.northeastern.cs5500.starterbot.exceptions;

/** If a guild is not found in JDA, an exception is thrown. */
public class GuildNotFoundException extends Exception {
    public GuildNotFoundException(String e) {
        super(e);
    }
}
