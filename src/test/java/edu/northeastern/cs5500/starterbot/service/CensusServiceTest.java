package edu.northeastern.cs5500.starterbot.service;

import static com.google.common.truth.Truth.assertThat;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

class CensusServiceTest {

    CensusService censusService;

    @BeforeClass
    void setup() {
        this.censusService = new CensusService();
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
}
