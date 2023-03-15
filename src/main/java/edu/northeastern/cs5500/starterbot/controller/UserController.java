package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.User;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class UserController {
    private static final String TITLE_FIELD_NAME = "title";
    private static final String TITLE_URL_FIELD_NAME = "titleUrl";
    private static final String DESCRIPTION_FIELD_NAME = "Description:";
    private static final String IMAGE_FIELD_NAME = "image";
    private static final String COLOR_FIELD_NAME = "color";

    GenericRepository<User> userRepository;

    @Inject
    UserController(GenericRepository<User> userRepository) {
        this.userRepository = userRepository;

        if (userRepository.count() == 0) {
            User user = new User();
            user.setDiscordUserId("1234");
            user.setGuildId("2349024");
            user.setLocationOfResidence(null);
            user.setCurrentListingAsString(null);
            user.setCurrentListing(null);
            userRepository.add(user);
        }
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
            MessageEmbed currentListingAsBuilder = currentListings.get(0);
            JsonObjectBuilder images = Json.createObjectBuilder();
            int i = 0;
            for (MessageEmbed messageEmbed : currentListings) {
                images.add(IMAGE_FIELD_NAME + i, messageEmbed.getImage().getUrl());
                i++;
            }
            String imagesSaved = images.build().toString();
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add(TITLE_FIELD_NAME, currentListingAsBuilder.getTitle());
            builder.add(TITLE_URL_FIELD_NAME, currentListingAsBuilder.getUrl());

            List<Field> listingFields = currentListingAsBuilder.getFields();
            for (Field field : listingFields) {
                builder.add(field.getName(), field.getValue());
            }
            builder.add(IMAGE_FIELD_NAME, imagesSaved);
            builder.add(COLOR_FIELD_NAME, currentListingAsBuilder.getColorRaw());
            JsonObject object = builder.build();
            user.setCurrentListing(object.toString());
        }
        userRepository.update(user);
    }

    @Nullable
    public List<MessageEmbed> getCurrentListing(String discordMemberId) {
        JsonReader reader =
                Json.createReader(
                        new StringReader(getUserForMemberId(discordMemberId).getCurrentListing()));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        JsonReader reader2 =
                Json.createReader(new StringReader(jsonObject.getString(IMAGE_FIELD_NAME)));
        JsonObject jsonObject2 = reader2.readObject();
        reader2.close();

        List<MessageEmbed> currentListings = new ArrayList<>();
        for (int i = 0; i < jsonObject2.keySet().size(); i++) {
            EmbedBuilder embedBuilder =
                    new EmbedBuilder()
                            .setColor(jsonObject.getInt(COLOR_FIELD_NAME))
                            .setTitle(
                                    jsonObject.getString(TITLE_FIELD_NAME),
                                    jsonObject.getString(TITLE_URL_FIELD_NAME))
                            .setImage(jsonObject2.getString(IMAGE_FIELD_NAME + i));
            for (String key : jsonObject.keySet()) {
                if (DESCRIPTION_FIELD_NAME.equals(key)) {
                    embedBuilder.addField(key, jsonObject.getString(key), false);
                } else if (!TITLE_FIELD_NAME.equals(key)
                        && !TITLE_URL_FIELD_NAME.equals(key)
                        && !IMAGE_FIELD_NAME.equals(key)
                        && !COLOR_FIELD_NAME.equals(key)) {
                    embedBuilder.addField(key, jsonObject.getString(key), true);
                }
            }
            currentListings.add(embedBuilder.build());
        }
        return currentListings;
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
