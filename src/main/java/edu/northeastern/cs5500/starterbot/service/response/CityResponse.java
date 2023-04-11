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

    public CityResponse(List<String> cityData) {
        @Nonnull String cityName = Objects.requireNonNull(cityData.get(0));
        int lastComma = cityName.lastIndexOf(",");
        if (lastComma == -1) {
            throw new IllegalArgumentException(
                    "Invalid city/town name (expected a ',' somewhere): " + cityName);
        }
        this.name = Objects.requireNonNull(cityName.substring(0, lastComma - 5));

        this.population = Integer.parseInt(cityData.get(1));
        this.stateCode = Integer.parseInt(cityData.get(2));
        this.zipCode = Objects.requireNonNull(cityData.get(3));
    }
}
