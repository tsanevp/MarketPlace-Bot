package edu.northeastern.cs5500.starterbot.discord.commands;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
public class CreateTradingChannelCommandTest {

    CreateTradingChannelCommand createTradingChannelCommand;

    @BeforeEach
    void initializeCreateTradingChannelCommand() {
        createTradingChannelCommand = new CreateTradingChannelCommand();
    }

    @Test
    void testGetCommandName() {
        assertThat(createTradingChannelCommand.getName()).isEqualTo("createtradingchannel");
    }
}
