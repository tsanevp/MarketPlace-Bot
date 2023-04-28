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

    /**
     * Method that calls CensusService and gets the cities for a given State. It then sorts the
     * cities by population in descending order. It returns a list of cities the size of maxResults
     *
     * @param stateAbbreviation - The state to retrieve the cities for
     * @param maxResults - The number of cities to add to the returned list
     * @return A list of cities. The number of cities in the list depends on maxResults
     */
    public List<String> getCitiesByState(String stateAbbreviation, int maxResults) {
        var cities = censusService.getCitiesByState(stateAbbreviation);
        if (cities == null) {
            return Collections.emptyList();
        }

        // Sort the cities in descending order, more populated cities appear first
        cities.sort((left, right) -> Integer.compare(right.getPopulation(), left.getPopulation()));

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
