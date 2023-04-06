package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.service.CensusService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CityController {
    CensusService censusService;

    @Inject
    public CityController(CensusService censusService) {
        this.censusService = censusService;
    }

    public List<String> getCitiesByState(String stateAbbreviation, int maxResults) {
        var cities = censusService.getCitiesByState(stateAbbreviation);

        if (cities == null) {
            return Collections.emptyList();
        }

        cities.sort((left, right) -> Integer.compare(left.getPopulation(), right.getPopulation()));

        List<String> result = new ArrayList<>();

        for (var city : cities) {
            if (result.size() >= maxResults) {
                break;
            }

            result.add(city.getName());
        }

        return result;
    }
}
