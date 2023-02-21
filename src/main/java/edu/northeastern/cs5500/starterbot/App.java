package edu.northeastern.cs5500.starterbot;

import static spark.Spark.get;
import static spark.Spark.port;

public class App {

    public static void main(String[] arg) {

        DaggerBotComponent.create().bot().start();

        port(8080);

        get("/", (request, response) -> "{\"status\": \"OK\"}");
    }
}
