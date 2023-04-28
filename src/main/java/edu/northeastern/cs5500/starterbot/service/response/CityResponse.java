package edu.northeastern.cs5500.starterbot.service.response;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class CityResponse {
    @Nonnull final String name;
    final int population;
    final int stateCode;
    @Nonnull final String zipCode;

    /**
     * Constructs a City Response object. Below are a number of Objects.requireNonNull checks.
     * Sonarlint would gives null check errors until we had the code set up as it is below. Multiple
     * methods were attempted to prevent this approach, but they did not work.
     *
     * @param cityData - The data regarding a city.
     */
    public CityResponse(List<String> cityData) {
        String cityNameFull = cityData.get(0);
        Objects.requireNonNull(cityNameFull);

        int lastComma = cityNameFull.lastIndexOf(",");
        if (lastComma == -1) {
            throw new IllegalArgumentException(
                    "Invalid city/town name (expected a ',' somewhere): " + cityNameFull);
        }

        var cityName = cityNameFull.substring(0, lastComma - 5);
        Objects.requireNonNull(cityName);
        this.name = cityName;

        this.population = Integer.parseInt(cityData.get(1));
        this.stateCode = Integer.parseInt(cityData.get(2));

        var cityDataZipCode = cityData.get(3);
        Objects.requireNonNull(cityDataZipCode);
        this.zipCode = cityDataZipCode;
    }
}
