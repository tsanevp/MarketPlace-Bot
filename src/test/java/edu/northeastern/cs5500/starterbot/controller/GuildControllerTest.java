package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class GuildControllerTest {

    GuildController guildController;

    private GuildController getGuildController() {
        GuildController guildController = new GuildController(new InMemoryRepository<>());
        return guildController;
    }

    @BeforeEach
    void initializeGuildController() {
        guildController = getGuildController();
    }

    @Test
    public void testCreationOfGuildObjectWorks() {
        // First check that guild colleciton size is 0
        // assertThat

        // // Set trading channel id
        // var guildId = "12345";
        // var tradingChannelId = "some id";

        // guildController.setTradingChannelId(guildId, tradingChannelId);

    }

    @Test
    public void testSetTradingChannelIdSetsTheExpectedValue() {
        //

        // Set trading channel id
        var guildId = "12345";
        var tradingChannelId = "some id";

        guildController.setTradingChannelId(guildId, tradingChannelId);
    }
}
