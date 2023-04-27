package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
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
    void testSetGuildOwnerIdReturnsTheCorrectId() {
        // First check that guild colleciton size is 0
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(0);

        // Define guild id and create new guild object
        var guildId = "1122334455";
        var guildOwnerId = "1234567890";
        var guild = guildController.getGuildByGuildId(guildId);

        // Set the guild owner id
        guildController.setGuildOwnerId(guildId, guildOwnerId);

        // Check guild has been added to collection and that owner id is correct
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(1);
        assertThat(guild.getGuildOwnerId()).isNotNull();
        assertThat(guild.getGuildOwnerId()).isEqualTo(guildOwnerId);

        // Check that get guild owner id only returns what we expect
        var guildOwnerIdNew = "0987654321";
        assertThat(guild.getGuildOwnerId()).isNotEqualTo(guildOwnerIdNew);

        // Re-set the guild owner id
        guildController.setGuildOwnerId(guildId, guildOwnerIdNew);

        // Check that the new guild owner id is returned
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(1);
        assertThat(guild.getGuildOwnerId()).isNotNull();
        assertThat(guild.getGuildOwnerId()).isEqualTo(guildOwnerIdNew);
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

        // Check to see if the trading channel id is what was set
        assertThat(guild.getTradingChannelId()).isEqualTo(tradingChannelId);
    }

    @Test
    void testGetTradingChannelIdByGuildIdReturnsTheExpectedValue() {
        // Define guild and trading channel ids
        var guildId = "12345";
        var tradingChannelId = "some id";

        // Ensure size of collection is zero at start
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(0);

        // Create a new guild object and set the trading channel id
        guildController.setTradingChannelId(guildId, tradingChannelId);

        // Check to see if size increased to 1
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(1);

        // Check to see if the trading channel id is what was set
        assertThat(guildController.getTradingChannelIdByGuildId(guildId))
                .isEqualTo(tradingChannelId);
    }

    @Test
    void testAddUserToServerWorksAndTheUserIsAddedToSet() {
        // Define guild and trading channel ids
        var guildId = "12345";
        var userToAdd = "testUser1";

        // Create a new guild object
        var guild = guildController.getGuildByGuildId(guildId);

        // Check that List of users is empty when the guild is first created
        assertThat(guild.getUsersOnServer()).isNotNull();
        assertThat(guild.getUsersOnServer().size()).isEqualTo(0);

        // Add a user to the guild and check that size of set increases
        guildController.addUserToServer(guildId, userToAdd);
        assertThat(guild.getUsersOnServer().size()).isEqualTo(1);
        assertThat(guild.getUsersOnServer().contains(userToAdd)).isEqualTo(true);

        // Check that a user cannot be added twice to the set of users
        guildController.addUserToServer(guildId, userToAdd);
        assertThat(guild.getUsersOnServer().size()).isEqualTo(1);
        assertThat(guild.getUsersOnServer().contains(userToAdd)).isEqualTo(true);
    }

    @Test
    void testAddAllCurrentUserToServerWorksAndAllTheUserAreAddedToSet() {
        // Define guild and trading channel ids
        var guildId = "12345";

        List<String> listOfUserIdsToAdd = new ArrayList<>();
        listOfUserIdsToAdd.add("testUser1");
        listOfUserIdsToAdd.add("testUser2");
        listOfUserIdsToAdd.add("testUser3");
        listOfUserIdsToAdd.add("testUser4");

        // Create a new guild object
        var guild = guildController.getGuildByGuildId(guildId);

        // Check that List of users is empty when the guild is first created
        assertThat(guild.getUsersOnServer()).isNotNull();
        assertThat(guild.getUsersOnServer().size()).isEqualTo(0);

        // Add a user to the guild and check that size of set increases
        guildController.addAllCurrentUsersToServer(guildId, listOfUserIdsToAdd);
        assertThat(guild.getUsersOnServer().size()).isEqualTo(listOfUserIdsToAdd.size());
        assertThat(guild.getUsersOnServer().contains(listOfUserIdsToAdd.get(0))).isEqualTo(true);
    }

    @Test
    void testRemoveUserInServerRemovesTheUser() {
        // Define guild and trading channel ids
        var guildId = "12345";
        var userToAddThenRemove = "testUser1";

        // Create a new guild object
        var guild = guildController.getGuildByGuildId(guildId);

        // Check that List of users is empty when the guild is first created
        assertThat(guild.getUsersOnServer()).isNotNull();
        assertThat(guild.getUsersOnServer().size()).isEqualTo(0);

        // Add a user to the guild and check that size of set increases
        guildController.addUserToServer(guildId, userToAddThenRemove);
        assertThat(guild.getUsersOnServer().size()).isEqualTo(1);
        assertThat(guild.getUsersOnServer().contains(userToAddThenRemove)).isEqualTo(true);
        assertThat(guildController.verifyUserInGuild(guildId, userToAddThenRemove)).isTrue();
        ;

        // Remove the user and check that the size of the set decreases
        assertThat(guildController.removeUserInServer(guildId, userToAddThenRemove)).isTrue();
        ;
        assertThat(guild.getUsersOnServer().size()).isEqualTo(0);
        assertThat(guildController.verifyUserInGuild(guildId, userToAddThenRemove)).isFalse();
        ;

        // Attempt to remove a user not in the set
        assertThat(guildController.removeUserInServer(guildId, userToAddThenRemove)).isFalse();
        ;
    }

    @Test
    void testVerifyUserNoLongerExistsInAnyGuild() {
        // Define two guild ids
        var firstGuildId = "12345";
        var secondGuildId = "54321";

        // Define user that will be added and removed
        var userToAddThenRemove = "testUser1";

        // Create the new guild objects
        var guildOne = guildController.getGuildByGuildId(firstGuildId);
        var guildTwo = guildController.getGuildByGuildId(secondGuildId);

        // Confirm that the user does not exist in either guild
        assertThat(guildOne.getUsersOnServer().size()).isEqualTo(0);
        assertThat(guildTwo.getUsersOnServer().size()).isEqualTo(0);

        // Add a user to each guild
        guildController.addUserToServer(firstGuildId, userToAddThenRemove);
        guildController.addUserToServer(secondGuildId, userToAddThenRemove);

        // Confirm user was added to guild one
        assertThat(guildOne.getUsersOnServer().size()).isEqualTo(1);
        assertThat(guildOne.getUsersOnServer().contains(userToAddThenRemove)).isEqualTo(true);

        // Confirm user was added to guild two
        assertThat(guildTwo.getUsersOnServer().size()).isEqualTo(1);
        assertThat(guildTwo.getUsersOnServer().contains(userToAddThenRemove)).isEqualTo(true);

        // Remove the user from guild one and confirm removal
        assertThat(guildController.removeUserInServer(firstGuildId, userToAddThenRemove)).isTrue();
        ;
        assertThat(guildOne.getUsersOnServer().size()).isEqualTo(0);

        // Confirm that the user still exists in guild two
        assertThat(guildController.verifyUserNoLongerExistsInAnyGuild(userToAddThenRemove))
                .isFalse();
        ;
        assertThat(guildTwo.getUsersOnServer().contains(userToAddThenRemove)).isTrue();
        ;

        // Remove the user from guild two and confirm removal
        assertThat(guildController.removeUserInServer(secondGuildId, userToAddThenRemove)).isTrue();
        ;
        assertThat(guildTwo.getUsersOnServer().size()).isEqualTo(0);

        // Confirm user now no longer exists in any guild
        assertThat(guildController.verifyUserNoLongerExistsInAnyGuild(userToAddThenRemove))
                .isTrue();
        ;
    }

    @Test
    void testRemoveGuildByGuildIdRemovesGuild() {
        // Define guild and trading channel ids
        var guildId = "12345";
        var fakeGuildId = "54321";

        // Create a new guild object
        guildController.getGuildByGuildId(guildId);

        // Confirm the size of the guild collection increased to 1
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(1);

        // Check that trying to remove a guild not in the collection returns false
        assertThat(guildController.removeGuildByGuildId(fakeGuildId)).isFalse();

        // Remove the guild from the collection
        assertThat(guildController.removeGuildByGuildId(guildId)).isTrue();
        assertThat(guildController.getSizeGuildCollection()).isEqualTo(0);
    }
}
