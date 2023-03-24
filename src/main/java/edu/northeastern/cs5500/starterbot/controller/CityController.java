package edu.northeastern.cs5500.starterbot.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

public class CityController {
    private final HttpClient client = HttpClient.newHttpClient();
    private static final int MAX_MENU_OPTIONS = 25;

    public CityController() {
        // Defined public and empty to call API
    }

    public List<String> getCitiesByState(String stateCode)
            throws IOException, InterruptedException {
        String url =
                String.format(
                        "https://api.census.gov/data/2019/pep/population?get=NAME,POP&for=place:*&in=state:%s",
                        stateCode);
        Optional<List<List<String>>> citiesNested = this.apiCallHelper(url);

        List<String> citiesCleanedUp = new ArrayList<>();

        if (citiesNested.isPresent()) {
            List<List<String>> cities = citiesNested.get();
            cities.remove(0);
            cities.sort(
                    (pop1, pop2) ->
                            Integer.parseInt(pop2.get(1)) - (Integer.parseInt(pop1.get(1))));
            for (int i = 0; i < MAX_MENU_OPTIONS; i++) {
                String city = cities.get(i).get(0);
                int index = city.indexOf(",");
                citiesCleanedUp.add(city.substring(0, index - 5));
            }
        }
        return citiesCleanedUp;
    }

    private Optional<List<List<String>>> apiCallHelper(String url)
            throws IOException, InterruptedException {
        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .setHeader("Accept", "application/html")
                        .build();
        HttpResponse<String> res = client.send(req, BodyHandlers.ofString());

        if (res.statusCode() != HttpServletResponse.SC_OK) {
            return Optional.empty();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<List<String>>>() {}.getType();

        return Optional.ofNullable(gson.fromJson(res.body(), type));
    }
}
