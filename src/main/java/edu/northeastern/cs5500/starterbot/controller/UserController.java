package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.User;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserController {
    GenericRepository<User> userRepository;

    @Inject
    UserController(GenericRepository<User> userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Sets the Guild Id for the given user.
     *
     * @param discordMemberId - The user to set the Guild Id for.
     * @param guildId - The Guild Id to set for the user.
     */
    public void setGuildIdForUser(String discordMemberId, String guildId) {
        User user = getUserForMemberId(discordMemberId);

        user.setGuildId(guildId);
        userRepository.update(user);
    }

    /**
     * Get the GuildId for the guild the user is in.
     *
     * @param discordMemberId - The discord user to get the guild Id for.
     * @return the guild Id as a string.
     */
    @Nonnull
    public String getGuildIdForUser(String discordMemberId) {
        return Objects.requireNonNull(getUserForMemberId(discordMemberId).getGuildId());
    }

    /**
     * Retrieves the Trading Channel Id for the given user.
     *
     * @param discordMemberId - The user to get the trading channel Id for.
     * @return the trading channel Id as a string.
     */
    @Nullable
    public String getTradingChannelId(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getTradingChannelId();
    }

    /**
     * Sets the Trading Channel Id for the given user.
     *
     * @param user - The user to set the trading channel Id for.
     * @param tradingChannelId - The trading channel Id to set for the user.
     */
    public void setTradingChannelId(String discordMemberId, String tradingChannelId) {
        User user = getUserForMemberId(discordMemberId);

        user.setTradingChannelId(tradingChannelId);
        userRepository.update(user);
    }

    /**
     * Set the state that the user lives in.
     *
     * @param discordMemberId - The discord user to set the state of residence for.
     * @param stateOfResidence - The state that the user lives in.
     */
    public void setStateOfResidence(String discordMemberId, String stateOfResidence) {
        User user = getUserForMemberId(discordMemberId);

        user.setStateOfResidence(stateOfResidence);
        userRepository.update(user);
    }

    /**
     * Set the state that the user lives in.
     *
     * @param discordMemberId - The discord user to get the state of residence for.
     * @return the state the user lives in.
     */
    @Nonnull
    public String getStateOfResidence(String discordMemberId) {
        return Objects.requireNonNull(getUserForMemberId(discordMemberId).getStateOfResidence());
    }

    /**
     * Set the city that the user lives in.
     *
     * @param discordMemberId - The discord user to set the city of residence for.
     * @param cityOfResidence - The city that the user lives in.
     */
    public void setCityOfResidence(String discordMemberId, String cityOfResidence) {
        User user = getUserForMemberId(discordMemberId);

        user.setCityOfResidence(cityOfResidence);
        userRepository.update(user);
    }

    /**
     * Set the state that the user lives in.
     *
     * @param discordMemberId - The discord user to get the city of residence for.
     * @return the city the user lives in.
     */
    @Nonnull
    public String getCityOfResidence(String discordMemberId) {
        return Objects.requireNonNull(getUserForMemberId(discordMemberId).getCityOfResidence());
    }

    /**
     * Set the current listing the user is working on.
     *
     * @param discordMemberId - The discord user to set the current listing for.
     */
    public void setCurrentListing(String discordMemberId, Listing currentListing) {
        User user = getUserForMemberId(discordMemberId);
        user.setCurrentListing(null);
        if (currentListing != null) {
            user.setCurrentListing(currentListing);
        }
        userRepository.update(user);
    }

    /**
     * Gets the current listing the user is working on. There will only ever be one current listing,
     * if it is not null.
     *
     * @param discordMemberId - The discord user to get the current listing for.
     * @return the current listing object.
     */
    @Nullable
    public Listing getCurrentListing(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getCurrentListing();
    }

    /**
     * Gets the user associated with the discord member Id given. If there is no user with that Id,
     * a new user is created and added to the collection.
     *
     * @param discordMemberId - The discord user to get from the repository.
     * @return the user obtained from the repository OR the user just created.
     */
    @Nonnull
    public User getUserForMemberId(String discordMemberId) {
        Collection<User> users = userRepository.getAll();
        for (User currentUser : users) {
            if (currentUser.getDiscordUserId().equals(discordMemberId)) {
                return currentUser;
            }
        }

        User user = new User();
        user.setDiscordUserId(discordMemberId);
        userRepository.add(user);
        return user;
    }

    /**
     * Removes the user associated with the discordMemberId and guildId passed from the user
     * collection.
     *
     * @param discordMemberId - The discord user to remove from the collection.
     * @param guildId - The guild id of guiid the user was removed or left from.
     */
    public void removeUserByMemberAndGuildId(String discordMemberId, String guildId) {
        Collection<User> users = userRepository.getAll();
        for (User currentUser : users) {
            if (currentUser.getDiscordUserId().equals(discordMemberId)
                    && currentUser.getGuildId().equals(guildId)) {
                userRepository.delete(Objects.requireNonNull(currentUser.getId()));
                return;
            }
        }
    }

    /**
     * Method to get the size of the user collection. Used mainly for test purposes.
     *
     * @return the size of the user collection.
     */
    public long getSizeUserCollection() {
        return userRepository.count();
    }
}
