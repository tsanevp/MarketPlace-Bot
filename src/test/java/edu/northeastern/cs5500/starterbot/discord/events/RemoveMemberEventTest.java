package edu.northeastern.cs5500.starterbot.discord.events;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class RemoveMemberEventTest {

    RemoveMemberEvent removeMemberEvent;

    @BeforeEach
    void initializeNewMemberEvent() {
        removeMemberEvent = new RemoveMemberEvent();
    }

    @Test
    void testGetEventName() {
        assertThat(removeMemberEvent.getName()).isEqualTo("removemember");
    }
}
