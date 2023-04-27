package edu.northeastern.cs5500.starterbot.exceptions;

/** If a error occurs while trying to get the state or city for a user, an exception is thrown. */
public class StateOrCityNotSetException extends Exception {
    public StateOrCityNotSetException(String e) {
        super(e);
    }
}
