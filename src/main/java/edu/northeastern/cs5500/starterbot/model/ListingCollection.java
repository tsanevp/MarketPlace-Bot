package edu.northeastern.cs5500.starterbot.model;
import lombok.Data;

import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;

@Data
public class ListingCollection implements Model{
    ObjectId id;
    
    String discordUserId;

    String title;

    String url;

    String description;

    List<String> images;

    String color;

    String fields;
}