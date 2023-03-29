package edu.northeastern.cs5500.starterbot.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.DBObject;

@Data
public class Listing implements Model {
    ObjectId id;

    Long messageId;

    String discordUserId;

    String title;

    String url;

    String description;

    ArrayList<String> images;

    Integer color;

    Document fields;
}
