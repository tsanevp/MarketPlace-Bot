package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class SayCommandTest {
    @Test
    void testNameMatchesData() {
        SayCommand sayCommand = new SayCommand();
        String name = sayCommand.getName();
        CommandData commandData = sayCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
