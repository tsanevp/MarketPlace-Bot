package edu.northeastern.cs5500.starterbot.discord;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
class SettingLocationHelperTest {

    SettingLocationHelper location;

    @BeforeEach
    void createLocationInstance() {
        location = new SettingLocationHelper();
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
        assertThat(statesMessageBuilderAsString).contains("Idaho");
        assertThat(statesMessageBuilderAsString).contains("New York");
        assertThat(statesMessageBuilderAsString).contains("Washington");
        assertThat(statesMessageBuilderAsString).contains("Alabama");
    }
}
