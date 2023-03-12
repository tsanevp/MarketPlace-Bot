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

    public void setCurrentListingAsBuilder(
            String discordMemberId, List<MessageEmbed> currentListingAsBuilder) {
        User user = getUserForMemberId(discordMemberId);

        user.setCurrentListingAsBuilder(currentListingAsBuilder);
        userRepository.update(user);
    }

    @Nullable
    public List<MessageEmbed> getCurrentListingAsBuilder(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getCurrentListingAsBuilder();
    }

    public void setCurrentListing(String discordMemberId, List<MessageEmbed> currentListings) {
        User user = getUserForMemberId(discordMemberId);
        MessageEmbed currentListingAsBuilder = currentListings.get(0);
        JsonObjectBuilder images = Json.createObjectBuilder();
        int i = 0;
        for (MessageEmbed messageEmbed : currentListings) {
            images.add("image" + i, messageEmbed.getImage().getUrl());
            i++;
        }
        String imagesSaved = images.build().toString();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("title", currentListingAsBuilder.getTitle());
        builder.add("titleUrl", currentListingAsBuilder.getUrl());

        List<Field> listingFields = currentListingAsBuilder.getFields();
        for (Field field : listingFields) {
            builder.add(field.getName(), field.getValue());
        }
        builder.add("image", imagesSaved);
        builder.add("color", currentListingAsBuilder.getColorRaw());
        JsonObject object = builder.build();
        user.setCurrentListing(object.toString());
        userRepository.update(user);
    }

    @Nullable
    public List<MessageEmbed> getCurrentListing(String discordMemberId) {
        JsonReader reader =
                Json.createReader(
                        new StringReader(getUserForMemberId(discordMemberId).getCurrentListing()));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        JsonReader reader2 = Json.createReader(new StringReader(jsonObject.getString("image")));
        JsonObject jsonObject2 = reader2.readObject();
        reader2.close();

        List<MessageEmbed> currentListings = new ArrayList<>();
        for (int i = 0; i < jsonObject2.keySet().size(); i++) {
            EmbedBuilder embedBuilder =
                    new EmbedBuilder()
                            .setColor(jsonObject.getInt("color"))
                            .setTitle(
                                    jsonObject.getString("title"), jsonObject.getString("titleUrl"))
                            .setImage(jsonObject2.getString("image" + i));
            for (String key : jsonObject.keySet()) {
                if ("Description:".equals(key)) {
                    embedBuilder.addField(key, jsonObject.getString(key), false);
                    continue;
                } else if ("title".equals(key)
                        || "titleUrl".equals(key)
                        || "image".equals(key)
                        || "color".equals(key)) {
                    continue;
                }
                embedBuilder.addField(key, jsonObject.getString(key), true);
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
