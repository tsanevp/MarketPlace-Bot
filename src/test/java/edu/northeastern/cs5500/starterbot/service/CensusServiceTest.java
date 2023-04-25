package edu.northeastern.cs5500.starterbot.service;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

@SuppressWarnings("null")
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class CensusServiceTest {

    CensusService censusService;

    public CensusServiceTest() {
        censusService = new CensusService();
    }

    @Test
    void testCanGetWashington() {
        assertThat(censusService.getCitiesByState("wa")).isNotNull();
    }

    @Test
    void testCaseInsensitive() {
        var wa = censusService.getCitiesByState("wa");
        var waCapitalized = censusService.getCitiesByState("Wa");
        var waAllCaps = censusService.getCitiesByState("WA");
        assertThat(wa).isNotNull();
        assertThat(waCapitalized).isNotNull();
        assertThat(waAllCaps).isNotNull();
        assertThat(wa).containsExactlyElementsIn(waCapitalized);
        assertThat(wa).containsExactlyElementsIn(waAllCaps);
    }

    @Test
    void testCaliforniaContainsLosAngeles() {
        var ca = censusService.getCitiesByState("ca");
        Objects.requireNonNull(ca);

        assertThat(ca).isNotNull();
        for (var city : ca) {
            if (city.getName().equalsIgnoreCase("Los Angeles")) {
                return;
            }
        }
        fail("Los Angeles not found in CA");
    }

    @Test
    void testInvalidState() {
        assertThat(censusService.getCitiesByState("invalid")).isNull();
    }
}
