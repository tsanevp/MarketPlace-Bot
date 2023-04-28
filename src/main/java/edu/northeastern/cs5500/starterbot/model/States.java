/***************************************************************************************
 *   Title: US.java
 *   Author: AustinC, harlanhaskins
 *   Date: 2017
 *   Code version: 1.1
 *   Availability: https://github.com/AustinC/UnitedStates/blob/master/src/main/java/unitedstates/US.java
 *   License: https://github.com/AustinC/UnitedStates/blob/master/LICENSE.md
 *
 *   The codebase above is based on the following repository: https://gist.github.com/webdevwilson/5271984
 *
 ***************************************************************************************/

package edu.northeastern.cs5500.starterbot.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public enum States {
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

    /** The state's full, unabbreviated name. */
    @Nonnull private String fullName;

    /** The state's abbreviated name. */
    @Nonnull private String abbreviatedName;

    /** The state's FIPS code */
    @Nonnull private String stateCode;

    /** The set of states mapped by abbreviations. */
    @Nonnull private static final Map<String, States> STATES_BY_ABBR = new HashMap<>();

    /* static initializer that maps the state abrv to it's enum's values */
    static {
        for (States state : values()) {
            STATES_BY_ABBR.put(state.getAbbreviatedName(), state);
        }
    }

    /**
     * Constructs a new state.
     *
     * @param fullName - The state's full, unabbreviated name.
     * @param abbreviatedName - The state's abbreviated name.
     * @param stateCode - The state's FIPS code.
     */
    States(@Nonnull String fullName, @Nonnull String abbreviatedName, @Nonnull String stateCode) {
        this.fullName = fullName;
        this.abbreviatedName = abbreviatedName;
        this.stateCode = stateCode;
    }

    /**
     * Returns the state's full, unabbreviated name.
     *
     * @return The state's full, unabbreviated name.
     */
    @Nonnull
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the state's abbreviated name.
     *
     * @return The state's abbreviated name.
     */
    @Nonnull
    public String getAbbreviatedName() {
        return abbreviatedName;
    }

    /**
     * Returns the state's code.
     *
     * @return The state's state code.
     */
    @Nonnull
    public String getStateCode() {
        return stateCode;
    }

    /**
     * Gets the enum constant with the specified abbreviated name.
     *
     * @param abbr - The state's abbreviation.
     * @return The enum constant with the specified abbreviated name.
     */
    @Nonnull
    public static States valueOfAbbreviatedName(final String abbr) {
        final States state = STATES_BY_ABBR.get(abbr);
        if (state != null) {
            return state;
        } else {
            return UNKNOWN;
        }
    }

    /**
     * Gets the enum constant's values for the specified name.
     *
     * @param fullName - The state's full, unabbreviated name.
     * @return The enum constant's values (full name, abrv, state code).
     * @throws IllegalArgumentException If the name does not exist.
     */
    @Nonnull
    public static States valueOfFullName(final @Nonnull String fullName) {
        final String enumName = fullName.toUpperCase().replace(" ", "_");
        try {
            return valueOf(enumName);
        } catch (final IllegalArgumentException e) {
            return States.UNKNOWN;
        }
    }
}
