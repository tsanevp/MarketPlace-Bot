package edu.northeastern.cs5500.starterbot.model;

import java.util.HashMap;
import java.util.Map;

// Credit of this enums class: https://gist.github.com/webdevwilson/5271984
public enum States {
    // The addition of state codes is implemented by us
    ALABAMA("Alabama", "AL", "01"),
    ALASKA("Alaska", "AK", "02"),
    ARIZONA("Arizona", "AZ", "04"),
    ARKANSAS("Arkansas", "AR", "05"),
    CALIFORNIA("California", "CA", "06"),
    COLORADO("Colorado", "CO", "08"),
    CONNECTICUT("Connecticut", "CT", "09"),
    DELAWARE("Delaware", "DE", "10"),
    FLORIDA("Florida", "FL", "12"),
    GEORGIA("Georgia", "GA", "13"),
    HAWAII("Hawaii", "HI", "15"),
    IDAHO("Idaho", "ID", "16"),
    ILLINOIS("Illinois", "IL", "17"),
    INDIANA("Indiana", "IN", "18"),
    IOWA("Iowa", "IA", "19"),
    KANSAS("Kansas", "KS", "20"),
    KENTUCKY("Kentucky", "KY", "21"),
    LOUISIANA("Louisiana", "LA", "22"),
    MAINE("Maine", "ME", "23"),
    MARYLAND("Maryland", "MD", "24"),
    MASSACHUSETTS("Massachusetts", "MA", "25"),
    MICHIGAN("Michigan", "MI", "26"),
    MINNESOTA("Minnesota", "MN", "27"),
    MISSISSIPPI("Mississippi", "MS", "28"),
    MISSOURI("Missouri", "MO", "29"),
    MONTANA("Montana", "MT", "30"),
    NEBRASKA("Nebraska", "NE", "31"),
    NEVADA("Nevada", "NV", "32"),
    NEW_HAMPSHIRE("New Hampshire", "NH", "33"),
    NEW_JERSEY("New Jersey", "NJ", "34"),
    NEW_MEXICO("New Mexico", "NM", "35"),
    NEW_YORK("New York", "NY", "36"),
    NORTH_CAROLINA("North Carolina", "NC", "37"),
    NORTH_DAKOTA("North Dakota", "ND", "38"),
    OHIO("Ohio", "OH", "39"),
    OKLAHOMA("Oklahoma", "OK", "40"),
    OREGON("Oregon", "OR", "41"),
    PENNSYLVANIA("Pennsylvania", "PA", "42"),
    RHODE_ISLAND("Rhode Island", "RI", "44"),
    SOUTH_CAROLINA("South Carolina", "SC", "45"),
    SOUTH_DAKOTA("South Dakota", "SD", "46"),
    TENNESSEE("Tennessee", "TN", "47"),
    TEXAS("Texas", "TX", "48"),
    UTAH("Utah", "UT", "49"),
    VERMONT("Vermont", "VT", "50"),
    VIRGINIA("Virginia", "VA", "51"),
    WASHINGTON("Washington", "WA", "53"),
    WEST_VIRGINIA("West Virginia", "WV", "54"),
    WISCONSIN("Wisconsin", "WI", "55"),
    WYOMING("Wyoming", "WY", "56"),
    UNKNOWN("Unknown", "", "");

    /** The state's name. */
    private String name;

    /** The state's abbreviation. */
    private String abbreviation;

    /** The state's FIPS code */
    private String stateCode;

    /** The set of states addressed by abbreviations. */
    private static final Map<String, States> STATES_BY_ABBR = new HashMap<>();

    /* static initializer */
    static {
        for (States state : values()) {
            STATES_BY_ABBR.put(state.getAbbreviation(), state);
        }
    }

    /**
     * Constructs a new state.
     *
     * @param name the state's name.
     * @param abbreviation the state's abbreviation.
     * @param stateCode the state's FIPS code.
     */
    States(String name, String abbreviation, String stateCode) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.stateCode = stateCode;
    }

    /**
     * Returns the state's abbreviation.
     *
     * @return the state's abbreviation.
     */
    public String getAbbreviation() {
        return this.abbreviation;
    }

    /**
     * Returns the state's code.
     *
     * @return the state's state code.
     */
    public String getStateCode() {
        return this.stateCode;
    }

    /**
     * Gets the enum constant with the specified abbreviation.
     *
     * @param abbr the state's abbreviation.
     * @return the enum constant with the specified abbreviation.
     * @throws SunlightException if the abbreviation is invalid.
     */
    public static States valueOfAbbreviation(final String abbr) {
        final States state = STATES_BY_ABBR.get(abbr);
        if (state != null) {
            return state;
        } else {
            return UNKNOWN;
        }
    }

    public static States valueOfName(final String name) {
        final String enumName = name.toUpperCase().replace(" ", "_");
        try {
            return valueOf(enumName);
        } catch (final IllegalArgumentException e) {
            return States.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
