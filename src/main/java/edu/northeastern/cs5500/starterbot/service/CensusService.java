package edu.northeastern.cs5500.starterbot.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.northeastern.cs5500.starterbot.model.States;
import edu.northeastern.cs5500.starterbot.service.response.CityResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class CensusService {
    private static final String API_CENSUS_GOV_GET_NAME_FOR_STATE =
            "https://api.census.gov/data/2019/pep/population?get=NAME,POP&for=place:*&in=state:%s";
    private final HttpClient client;
    private Map<String, List<CityResponse>> cityCache;

    @Inject
    public CensusService() {
        client = HttpClient.newHttpClient();
        cityCache = new HashMap<>();
    }

    /**
     * Return the raw result from HTTP GETting the given URL or null.
     *
     * @param url
     * @return
     */
    @Nullable
    String httpGet(String url) {
        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .setHeader("Accept", "application/html")
                        .build();
        HttpResponse<String> res = null;
        try {
            res = client.send(req, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            log.error("apiCallHelper got an unexpected exception", e);
        }

        if (res == null || res.statusCode() != HttpServletResponse.SC_OK) {
            return null;
        }

        return res.body();
    }

    /**
     * Parses the cities that are passed in json format and creates a List of CityResponse objects.
     *
     * @param json - The cities to be parsed, currently in json format.
     * @return a list of CityResponse objects.
     */
    @Nullable
    List<CityResponse> parseCitiesByState(String json) {
        if (json == null) {
            return null;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<List<List<String>>>() {}.getType();

        ArrayList<CityResponse> cities = new ArrayList<>();

        List<List<String>> response = gson.fromJson(json, type);

        if (response == null) {
            return cities;
        }

        boolean isHeader = true;

        for (List<String> city : response) {
            if (isHeader) {
                // Skip the first row of the response, which is the header
                isHeader = false;
                continue;
            }
            cities.add(new CityResponse(city));
        }

        return cities;
    }

    /**
     * Calls the http url to get the cities for a certain state. After, parses the cities to only
     * return only the data we want.
     *
     * @param stateAbbreviation - The state abbreviation to get the cities for.
     * @return a list of CityResponse objects.
     */
    @Nullable
    public List<CityResponse> getCitiesByState(String stateAbbreviation) {
        return cityCache.computeIfAbsent(
                stateAbbreviation.toUpperCase(),
                key -> {
                    States state = States.valueOfAbbreviatedName(key);

                    String url =
                            String.format(API_CENSUS_GOV_GET_NAME_FOR_STATE, state.getStateCode());
                    String json = httpGet(url);
                    return parseCitiesByState(json);
                });
    }
}
