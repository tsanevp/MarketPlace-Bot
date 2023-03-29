package edu.northeastern.cs5500.starterbot.model;

import java.util.ArrayList;
import lombok.Data;
import org.bson.Document;
import org.bson.types.ObjectId;

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
