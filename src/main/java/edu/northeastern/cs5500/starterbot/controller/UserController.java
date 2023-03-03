package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.User;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;

public class UserController {

    GenericRepository<User> userRepository;

    @Inject
    UserController(GenericRepository<User> userRepository) {
        this.userRepository = userRepository;

        if (userRepository.count() == 0) {
            User user = new User();
            user.setGuildId("2349024");
            user.setDiscordUserId("1234");
            user.setLocationOfResidence(null);
            user.setCurrentListingAsString(null);
            user.setCurrentListingAsBuilder(null);
            userRepository.add(user);
        }
    }

    public void setGuildIdForUser(String discordMemberId, String guildId) {
        User user = getUserForMemberId(discordMemberId);

        user.setGuildId(guildId);
        System.out.println(guildId);
        userRepository.update(user);
    }

    @Nullable
    public String getGuildIdForUser(String discordMemberId) {
        System.out.println(getUserForMemberId(discordMemberId).getGuildId());
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

    public void setCurrentListingAsBuilder(String discordMemberId, List<MessageEmbed> currentListingAsBuilder) {
        User user = getUserForMemberId(discordMemberId);

        user.setCurrentListingAsBuilder(currentListingAsBuilder);
        userRepository.update(user);
    }

    @Nullable
    public List<MessageEmbed> getCurrentListingAsBuilder(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getCurrentListingAsBuilder();
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
