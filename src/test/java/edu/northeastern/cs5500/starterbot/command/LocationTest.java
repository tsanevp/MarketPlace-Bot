package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;
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
        MessageCreateData statesMessageData = location.createStatesMessageBuilder().build();
        assertThat(statesMessageData.getComponents().size()).isEqualTo(2);
    }

    @Test
    void testStatesMessageBuilderStringSelectMenuContents() {
        MessageCreateData statesMessageData = location.createStatesMessageBuilder().build();

        var statesMessageBuilderAsString = statesMessageData.toData().toString();
        assertThat(statesMessageBuilderAsString).isNotNull();
        assertThat(statesMessageBuilderAsString).contains("IDAHO");
        assertThat(statesMessageBuilderAsString).contains("NEW_YORK");
    }
}
