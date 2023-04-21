package edu.northeastern.cs5500.starterbot.exceptions;

/** If text channel is not found in JDA, an exception is thrown. */
public class ChannelNotFoundException extends Exception {
    public ChannelNotFoundException(String e) {
        super(e);
    }
}
