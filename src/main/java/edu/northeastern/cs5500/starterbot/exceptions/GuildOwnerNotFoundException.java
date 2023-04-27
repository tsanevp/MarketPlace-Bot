package edu.northeastern.cs5500.starterbot.exceptions;

/** If a guild owner is not found in JDA, an exception is thrown. */
public class GuildOwnerNotFoundException extends Exception {
    public GuildOwnerNotFoundException(String e) {
        super(e);
    }
}
