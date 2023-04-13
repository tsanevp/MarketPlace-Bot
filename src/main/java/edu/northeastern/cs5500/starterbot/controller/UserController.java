package edu.northeastern.cs5500.starterbot.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.lang.Nullable;
import edu.northeastern.cs5500.starterbot.model.User;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class UserController {
    private static final String TITLE_FIELD_NAME = "title";
    private static final String TITLE_URL_FIELD_NAME = "titleUrl";
    private static final String DESCRIPTION_FIELD_NAME = "Description:";
    private static final String IMAGE_FIELD_NAME = "image";
    private static final String COLOR_FIELD_NAME = "color";
    private static final String FIELDS_NAME = "fields";

    GenericRepository<User> userRepository;

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
    public String getTradingChannelId(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getTradingChannelId();
    }

    public void setTradingChannel(String discordMemberId, String tradingChannel) {
        User user = getUserForMemberId(discordMemberId);

        user.setTradingChannelId(tradingChannel);
        userRepository.update(user);
    }

    @Nullable
    public String getGuildIdForUser(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getGuildId();
    }

    public void setStateOfResidence(String discordMemberId, String stateOfResidence) {
        User user = getUserForMemberId(discordMemberId);

        user.setStateOfResidence(stateOfResidence);
        userRepository.update(user);
    }

    @Nullable
    public String getStateOfResidence(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getStateOfResidence();
    }

    public void setCityOfResidence(String discordMemberId, String cityOfResidence) {
        User user = getUserForMemberId(discordMemberId);

        user.setCityOfResidence(cityOfResidence);
        userRepository.update(user);
    }

    @Nullable
    public String getCityOfResidence(String discordMemberId) {
        return getUserForMemberId(discordMemberId).getCityOfResidence();
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
            int i = 0;
            DBObject imagesDbObject = new BasicDBObject();
            for (MessageEmbed messageEmbed : currentListings) {
                imagesDbObject.put(
                        IMAGE_FIELD_NAME + i,
                        Objects.requireNonNull(messageEmbed.getImage()).getUrl());
                i++;
            }
            DBObject embedDbObject =
                    new BasicDBObject()
                            .append(TITLE_FIELD_NAME, currentListingAsBuilder.getTitle())
                            .append(TITLE_URL_FIELD_NAME, currentListingAsBuilder.getUrl())
                            .append(IMAGE_FIELD_NAME, imagesDbObject)
                            .append(COLOR_FIELD_NAME, currentListingAsBuilder.getColorRaw());
            List<Field> listingFields = currentListingAsBuilder.getFields();
            DBObject fieldsDbObject = new BasicDBObject();
            for (Field field : listingFields) {
                fieldsDbObject.put(field.getName(), field.getValue());
            }
            embedDbObject.put(FIELDS_NAME, fieldsDbObject);
            user.setCurrentListing(embedDbObject);
        }
        userRepository.update(user);
    }

    @Nullable
    public List<MessageEmbed> getCurrentListing(String discordMemberId) {
        DBObject currentObject = getUserForMemberId(discordMemberId).getCurrentListing();
        DBObject imagesDbObject = (DBObject) currentObject.get(IMAGE_FIELD_NAME);
        DBObject fieldsDbObject = (DBObject) currentObject.get(FIELDS_NAME);
        List<MessageEmbed> currentListings = new ArrayList<>();
        EmbedBuilder embedBuilder =
                new EmbedBuilder()
                        .setColor((int) currentObject.get(COLOR_FIELD_NAME))
                        .setTitle(
                                (String) currentObject.get(TITLE_FIELD_NAME),
                                (String) currentObject.get(TITLE_URL_FIELD_NAME))
                        .setImage((String) imagesDbObject.get(IMAGE_FIELD_NAME + 0));
        for (String key : fieldsDbObject.keySet()) {
            Objects.requireNonNull(key);
            String fieldsDbObjectKey = Objects.requireNonNull((String) fieldsDbObject.get(key));
            if (DESCRIPTION_FIELD_NAME.equals(key)) {
                embedBuilder.addField(key, fieldsDbObjectKey, false);
                continue;
            }
            embedBuilder.addField(key, fieldsDbObjectKey, true);
        }

        MessageEmbed messageEmbed = embedBuilder.build();
        currentListings.add(messageEmbed);
        if (imagesDbObject.keySet().size() > 1) {
            for (int i = 1; i < imagesDbObject.keySet().size(); i++) {
                EmbedBuilder embedBuilders =
                        new EmbedBuilder()
                                .setColor(messageEmbed.getColorRaw())
                                .setTitle(messageEmbed.getTitle(), messageEmbed.getUrl())
                                .setImage((String) imagesDbObject.get(IMAGE_FIELD_NAME + i));
                currentListings.add(embedBuilders.build());
            }
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
