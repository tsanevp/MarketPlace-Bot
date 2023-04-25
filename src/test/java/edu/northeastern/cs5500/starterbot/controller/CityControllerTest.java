package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import edu.northeastern.cs5500.starterbot.service.CensusService;
import org.junit.jupiter.api.Test;

@SuppressWarnings("null")
class CityControllerTest {

    CityController cityController;

    public CityControllerTest() {
        cityController = new CityController(new CensusService());
    }

    @Test
    void testCanGetWashington() {
        assertThat(cityController.getCitiesByState("wa", 25)).isNotEmpty();
    }

    @Test
    void testCaseInsensitive() {
        var wa = cityController.getCitiesByState("wa", 25);
        var waCapitalized = cityController.getCitiesByState("Wa", 25);
        var waAllCaps = cityController.getCitiesByState("WA", 25);
        assertThat(wa).isNotEmpty();
        assertThat(waCapitalized).isNotEmpty();
        assertThat(waAllCaps).isNotEmpty();
        assertThat(wa).containsExactlyElementsIn(waCapitalized);
        assertThat(wa).containsExactlyElementsIn(waAllCaps);
    }

    @Test
    void testCaliforniaContainsLosAngeles() {
        var ca = cityController.getCitiesByState("ca", 1);
        assertThat(ca).isNotEmpty();
        for (var city : ca) {
            if (city.equalsIgnoreCase("Los Angeles")) {
                return;
            }
        }
        fail("Los Angeles not found in CA");
    }

    @Test
    void testInvalidState() {
        assertThat(cityController.getCitiesByState("invalid", 25)).isEmpty();
    }
}
