package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.User;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class UserController {

    GenericRepository<User> userRepository;
    Listing listing = new Listing();

    @Inject
    UserController(GenericRepository<User> userRepository) {
        this.userRepository = userRepository;
    }

    public void setGuildIdForUser(String discordMemberId, String guildId) {
        User user = getUserForMemberId(discordMemberId);

        user.setGuildId(guildId);
        userRepository.update(user);
    }

    @Nullable
    public String getGuildIdForUser(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getGuildId();
    }

    public void setLocationOfResidence(String discordMemberId, String locationOfResidence) {
        User user = getUserForMemberId(discordMemberId);

        user.setLocationOfResidence(locationOfResidence);
        userRepository.update(user);
    }

    @Nullable
    public String getLocationOfResidence(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getLocationOfResidence();
    }

    public void setCurrentListingAsString(String discordMemberId, String currentListingAsString) {
        User user = getUserForMemberId(discordMemberId);

        user.setCurrentListingAsString(currentListingAsString);
        userRepository.update(user);
    }

    @Nullable
    public String getCurrentListingAsString(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getCurrentListingAsString();
    }

    public void setCurrentListing(String discordMemberId, List<MessageEmbed> currentListings) {
        User user = getUserForMemberId(discordMemberId);
        user.setCurrentListing(null);
        if (currentListings != null) {
            user.setCurrentListing(new Listing().toDbObject(currentListings));
        }
        userRepository.update(user);
    }

    @Nullable
    public List<MessageEmbed> getCurrentListing(String discordMemberId) {
        return new Listing()
                .toMessageEmbed(getUserForMemberId(discordMemberId).getCurrentListing());
    }

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
}
