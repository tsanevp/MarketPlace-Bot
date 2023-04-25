package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class StatesTest {

    @Test
    void testGetNameReturnsTheCorrectName() {
        assertThat(States.WASHINGTON.getName()).isEqualTo("Washington");
        assertThat(States.OREGON.getName()).isEqualTo("Oregon");
        assertThat(States.WASHINGTON.getName()).isNotEqualTo("OR");
    }

    @Test
    void testGetAbbreviationReturnsTheCorrectAbbreviation() {
        assertThat(States.WASHINGTON.getAbbreviation()).isEqualTo("WA");
        assertThat(States.OREGON.getAbbreviation()).isEqualTo("OR");
        assertThat(States.WASHINGTON.getAbbreviation()).isNotEqualTo("OR");
    }

    @Test
    void testGetStateCodeReturnsTheCorrectStateCode() {
        assertThat(States.WASHINGTON.getStateCode()).isEqualTo("53");
        assertThat(States.ALABAMA.getStateCode()).isEqualTo("01");
        assertThat(States.WASHINGTON.getStateCode()).isNotEqualTo("01");
    }

    @Test
    void testValueOfAbbreviationReturnsTheCorrectValueOfAbbreviation() {
        assertThat(States.valueOfAbbreviation("WA")).isEqualTo(States.WASHINGTON);
        assertThat(States.valueOfAbbreviation(null)).isEqualTo(States.UNKNOWN);
    }

    @Test
    void testValueOfNameReturnsTheCorrectValueOfName() {
        assertThat(States.valueOfName("washington")).isEqualTo(States.WASHINGTON);
        assertThat(States.valueOfName("fakename")).isEqualTo(States.UNKNOWN);
    }
}
