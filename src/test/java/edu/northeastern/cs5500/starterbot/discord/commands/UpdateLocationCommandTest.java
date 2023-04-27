package edu.northeastern.cs5500.starterbot.discord.commands;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.discord.SettingLocationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class UpdateLocationCommandTest {

    UpdateLocationCommand updateLocationCommand;

    @BeforeEach
    void initializeUpdateLocationCommand() {
        updateLocationCommand = new UpdateLocationCommand();
    }

    @Test
    void testGetCommandName() {
        assertThat(updateLocationCommand.getName()).isEqualTo("updatelocation");
    }

    @Test
    void testCreateUpdateLocationMessageCreatesTheCorrectInstructionMessageToSendUser() {
        // Initialize and create the string select menus for the state selection process
        SettingLocationHelper settingLocationHelper = new SettingLocationHelper();
        var statesSelectMessageBuilder = settingLocationHelper.createStatesMessageBuilder();

        // Call the method
        var updateLocationMessage =
                updateLocationCommand.createUpdateLocationMessage(statesSelectMessageBuilder);
        var getEmbed = updateLocationMessage.getEmbeds().get(0);

        // Test that embed has attributes we expect
        assertThat(getEmbed.getDescription())
                .isEqualTo(
                        "To set your State and City, plese select the correct values from the drop-down menus below.");
        assertThat(getEmbed.getColorRaw()).isEqualTo(0x00FFFF);

        // Test that the method also has two string select menus returned
        assertThat(updateLocationMessage.getComponents()).hasSize(2);
    }
}
