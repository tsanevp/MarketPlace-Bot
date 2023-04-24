package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GuildControllerTest {

    GuildController guildController;

    private GuildController getGuildController() {
        return new GuildController(new InMemoryRepository<>());
    }

    @BeforeEach
    void initializeGuildController() {
        guildController = getGuildController();
    }

    @Test
    void testCreationOfGuildObjectWorks() {
        // First check that guild colleciton size is 0
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(0);

        // Define guild id and create new guild object
        var guildId1 = "12345";
        var guild1 = guildController.getGuildByGuildId(guildId1);

        // Check guild collection size is now 1 and get for guild by id works
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(1);
        assertThat(guild1).isEqualTo(guildController.getGuildByGuildId(guildId1));

        // Make sure passing different guildId does not return what we expect
        var guildId2 = "54321";
        assertThat(guildController.getGuildByGuildId(guildId2)).isNotEqualTo(guild1);
    }

    @Test
    void testSetTradingChannelIdSetsTheExpectedValue() {
        // Define guild and trading channel ids
        var guildId = "12345";
        var tradingChannelId = "some id";

        // Ensure size of collection is zero at start
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(0);

        // Create a new guild object and set the trading channel id
        guildController.setTradingChannelId(guildId, tradingChannelId);

        // Get guild
        var guild = guildController.getGuildByGuildId(guildId);

        // Check to see if size increased to 1
        assertThat(guild.getTradingChannelId()).isNotNull();
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(1);

        // Xheck to see if the trading channel id is what was set
        assertThat(guild.getTradingChannelId()).isEqualTo(tradingChannelId);
    }

    @Test
    void testAddUserToServerWorksAndTheUserIsAddedToList() {
        // Define guild and trading channel ids
        var guildId = "12345";
        var userToAdd = "testUser1";

        // Create a new guild object
        var guild = guildController.getGuildByGuildId(guildId);

        // Check that List of users is empty when the guild is first created
        assertThat(guild.getUsersOnServer()).isNotNull();
        assertThat(guild.getUsersOnServer().size()).isEqualTo(0);

        // Add a user to the guild and check that size of list increases
        guildController.addUserToServer(guildId, userToAdd);
        assertThat(guild.getUsersOnServer().size()).isEqualTo(1);
        assertThat(guild.getUsersOnServer().contains(userToAdd)).isEqualTo(true);
    }
}
