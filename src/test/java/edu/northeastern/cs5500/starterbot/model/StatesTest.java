package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class StatesTest {

    @Test
    void testGetUnabbreviatedNameReturnsTheCorrectName() {
        assertThat(States.WASHINGTON.getFullName()).isEqualTo("Washington");
        assertThat(States.OREGON.getFullName()).isEqualTo("Oregon");
        assertThat(States.WASHINGTON.getFullName()).isNotEqualTo("OR");
    }

    @Test
    void testGetAbbreviationReturnsTheCorrectAbbreviation() {
        assertThat(States.WASHINGTON.getAbbreviatedName()).isEqualTo("WA");
        assertThat(States.OREGON.getAbbreviatedName()).isEqualTo("OR");
        assertThat(States.WASHINGTON.getAbbreviatedName()).isNotEqualTo("OR");
    }

    @Test
    void testGetStateCodeReturnsTheCorrectStateCode() {
        assertThat(States.WASHINGTON.getStateCode()).isEqualTo("53");
        assertThat(States.ALABAMA.getStateCode()).isEqualTo("01");
        assertThat(States.WASHINGTON.getStateCode()).isNotEqualTo("01");
    }

    @Test
    void testValueOfAbbreviationReturnsTheCorrectValueOfAbbreviation() {
        assertThat(States.valueOfAbbreviatedName("WA")).isEqualTo(States.WASHINGTON);
        assertThat(States.valueOfAbbreviatedName(null)).isEqualTo(States.UNKNOWN);
    }

    @Test
    void testValueOfNameReturnsTheCorrectValueOfName() {
        assertThat(States.valueOfFullName("washington")).isEqualTo(States.WASHINGTON);
        assertThat(States.valueOfFullName("fakename")).isEqualTo(States.UNKNOWN);
    }
}
