package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocationTest {

    Location location;

    @BeforeEach
    void createLocationInstance() {
        location = new Location();
    }

    @Test
    void testStatesMessageBuilderCreatesTwoStringSelectMenus() {
        var statesMessageData = location.createStatesMessageBuilder().build();
        assertThat(statesMessageData.getComponents().size()).isEqualTo(2);
    }

    @Test
    void testStatesMessageBuilderStringSelectMenuContents() {
        var statesMessageData = location.createStatesMessageBuilder().build();

        var statesMessageBuilderAsString = statesMessageData.toData().toString();
        assertThat(statesMessageBuilderAsString).isNotNull();
        assertThat(statesMessageBuilderAsString).contains("IDAHO");
        assertThat(statesMessageBuilderAsString).contains("NEW_YORK");
        assertThat(statesMessageBuilderAsString).contains("WASHINGTON");
        assertThat(statesMessageBuilderAsString).contains("ALABAMA");
    }
}
