package edu.northeastern.cs5500.starterbot.discord.events;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.discord.SettingLocationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class NewMemberEventTest {

    NewMemberEvent newMemberEvent;

    @BeforeEach
    void initializeNewMemberEvent() {
        newMemberEvent = new NewMemberEvent();
    }

    @Test
    void testGetEventName() {
        assertThat(newMemberEvent.getName()).isEqualTo("newmember");
    }

    @Test
    void testCreateUpdateLocationMessageCreatesTheCorrectInstructionMessageToSendUser() {
        // Initialize and create the string select menus for the state selection process
        SettingLocationHelper settingLocationHelper = new SettingLocationHelper();
        var statesSelectMessageBuilder = settingLocationHelper.createStatesMessageBuilder();

        // Define constants
        var userName = "testUser";
        var guildName = "12345";
        var embedDescription =
                String.format(
                        "Hello %s! For potential future sales and purchases, please select the State & City you are located in below. If you do not see your city, please select the one nearest to you.",
                        userName);
        var embedTitle = String.format("Welcome to %s!", guildName);

        // Call the method
        var newUserIntroMsg =
                newMemberEvent.createIntroMessageForNewUser(
                        userName, guildName, statesSelectMessageBuilder);

        // Get embed
        var getEmbed = newUserIntroMsg.getEmbeds().get(0);

        // Test that embed has attributes we expect
        assertThat(getEmbed.getDescription()).isEqualTo(embedDescription);
        assertThat(getEmbed.getTitle()).isEqualTo(embedTitle);
        assertThat(getEmbed.getColorRaw()).isEqualTo(0x00FFFF);

        // Test that the method also has two string select menus returned
        assertThat(newUserIntroMsg.getComponents()).hasSize(2);
    }
}
