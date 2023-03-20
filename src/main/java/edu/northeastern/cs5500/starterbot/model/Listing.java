package edu.northeastern.cs5500.starterbot.model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

@Data
public class Listing {
    private static final String TITLE_FIELD_NAME = "title";
    private static final String TITLE_URL_FIELD_NAME = "titleUrl";
    private static final String DESCRIPTION_FIELD_NAME = "Description:";
    private static final String IMAGE_FIELD_NAME = "image";
    private static final String COLOR_FIELD_NAME = "color";
    private static final String FIELDS_NAME = "fields";

    public void listing() {
        // empty
    }

    public DBObject toDbObject(List<MessageEmbed> currentListings) {
        MessageEmbed currentListingAsBuilder = currentListings.get(0);
        int i = 0;
        DBObject imagesDbObject = new BasicDBObject();
        for (MessageEmbed messageEmbed : currentListings) {
            imagesDbObject.put(IMAGE_FIELD_NAME + i, messageEmbed.getImage().getUrl());
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
        return embedDbObject;
    }

    public List<MessageEmbed> toMessageEmbed(DBObject currentObject) {
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
            if (DESCRIPTION_FIELD_NAME.equals(key)) {
                embedBuilder.addField(key, (String) fieldsDbObject.get(key), false);
                continue;
            }
            embedBuilder.addField(key, (String) fieldsDbObject.get(key), true);
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
}
