package edu.northeastern.cs5500.starterbot.discord.events;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class NewGuildJoinedEventTest {

    NewGuildJoinedEvent newGuildJoinedEvent;

    @BeforeEach
    void initializeNewGuildJoinedEvent() {
        newGuildJoinedEvent = new NewGuildJoinedEvent();
    }

    @Test
    void testGetEventName() {
        assertThat(newGuildJoinedEvent.getName()).isEqualTo("newguildjoined");
    }

    @Test
    void testCreateIntroMessageForOwnerCreatesTheCorrectButtonsAndEmbed() {
        var guildId = "12345";
        var ownerIntroMessage = newGuildJoinedEvent.createIntroMessageForOwner(guildId);
        var ownerIntroMessageCopy = newGuildJoinedEvent.createIntroMessageForOwner(guildId);

        // Test calling the same method twice has same embed contents
        assertThat(ownerIntroMessage.getEmbeds()).isEqualTo(ownerIntroMessageCopy.getEmbeds());

        // Get buttons
        var buttonsOriginal = ownerIntroMessage.getComponents().get(0).getButtons();
        var buttonsCopy = ownerIntroMessageCopy.getComponents().get(0).getButtons();

        // Test buttons for each intro msg have the same size
        assertThat(buttonsOriginal.size()).isEqualTo(buttonsCopy.size());

        // Test individual intro msg buttons to confirm they contain what we expect
        assertThat(buttonsOriginal.size()).isEqualTo(2);
        ;
        assertThat(buttonsOriginal.get(0).getLabel()).isEqualTo("Bot Can Create The Channel");
    }
}
