package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.UserPreference;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;

public class UserPreferenceController {

    GenericRepository<UserPreference> userPreferenceRepository;

    @Inject
    UserPreferenceController(GenericRepository<UserPreference> userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;

        if (userPreferenceRepository.count() == 0) {
            UserPreference userPreference = new UserPreference();
            userPreference.setDiscordUserId("1234");
            userPreference.setPreferredName("Alex");
            userPreferenceRepository.add(userPreference);
        }
    }

    public void setPreferredNameForUser(String discordMemberId, String preferredName) {
        UserPreference userPreference = getUserPreferenceForMemberId(discordMemberId);

        userPreference.setPreferredName(preferredName);
        userPreferenceRepository.update(userPreference);
    }

    @Nullable
    public String getPreferredNameForUser(String discordMemberId) {
        return getUserPreferenceForMemberId(discordMemberId).getPreferredName();
    }

    @Nonnull
    public UserPreference getUserPreferenceForMemberId(String discordMemberId) {
        Collection<UserPreference> userPreferences = userPreferenceRepository.getAll();
        for (UserPreference currentUserPreference : userPreferences) {
            if (currentUserPreference.getDiscordUserId().equals(discordMemberId)) {
                return currentUserPreference;
            }
        }

        UserPreference userPreference = new UserPreference();
        userPreference.setDiscordUserId(discordMemberId);
        userPreferenceRepository.add(userPreference);
        return userPreference;
    }
}
