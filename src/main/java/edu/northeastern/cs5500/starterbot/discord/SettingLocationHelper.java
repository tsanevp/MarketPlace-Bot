package edu.northeastern.cs5500.starterbot.discord;

import com.google.common.annotations.VisibleForTesting;
import edu.northeastern.cs5500.starterbot.controller.CityController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.discord.handlers.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.model.States;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class SettingLocationHelper implements StringSelectHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    private static final Integer MAX_MENU_SELECTIONS = 25;

    @Inject UserController userController;
    @Inject CityController cityController;

    @Inject
    public SettingLocationHelper() {
        // Defined public and empty for Dagger injection
    }

    /**
     * Method to get the name of the command
     *
     * @return the name of the command
     */
    @Override
    @Nonnull
    public String getName() {
        return "location";
    }

    /**
     * This method is called when a user either selects the state OR the city they are located in
     *
     * @param event the JDA onStringSelectInteraction event that we can pull info from
     */
    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        var buttonId = Objects.requireNonNull(event.getComponentId());
        var handlerName = buttonId.split(":", 2)[1];
        var userId = event.getUser().getId();
        var selectedCityOrState = event.getInteraction().getValues().get(0);

        Objects.requireNonNull(selectedCityOrState);

        if ("cities".equals(handlerName)) {
            userController.setCityOfResidence(userId, selectedCityOrState);

            var messageEmbed = cityAndStateSetEmbedMessage(userId, selectedCityOrState);
            event.deferEdit().setComponents().setEmbeds(messageEmbed).queue();

        } else {
            var stateAbbreviation =
                    States.valueOfFullName(selectedCityOrState).getAbbreviatedName();
            var messageCreateBuilder = createCityMessageBuilder(stateAbbreviation);

            userController.setStateOfResidence(userId, stateAbbreviation);
            event.deferEdit().setComponents(messageCreateBuilder.getComponents()).queue();
        }
    }

    /**
     * Creates an embed message to send to the user. Embed lets the user know that the city and
     * state they selected have been properly set and saved.
     *
     * @param userId - The id of the user who the city and state were set for.
     * @param selectedCityOrState - The city the user selected from the list of cities.
     * @return MessageEmbed to send to the user informing them their city and state have been set.
     */
    @Nonnull
    private MessageEmbed cityAndStateSetEmbedMessage(
            @Nonnull String userId, @Nonnull String selectedCityOrState) {
        var description =
                String.format(
                        "You have set %s, %s as your City and State. You can later update these using the /updatelocation bot command.",
                        userController.getCityOfResidence(userId),
                        userController.getStateOfResidence(userId));

        return new EmbedBuilder().setDescription(description).setColor(EMBED_COLOR).build();
    }

    /**
     * This method is called to create two StringSelectMenus, each containing 25 states. From one of
     * these drop-downs, the user must select the state they are located in
     *
     * @return MessageCreateBuilder that has each StringSelectMenu as an action row
     */
    @Nonnull
    public MessageCreateBuilder createStatesMessageBuilder() {
        var statesFirstHalf =
                StringSelectMenu.create(getName() + ":stateselect1")
                        .setPlaceholder("Select what State you live in (1-25):");

        var statesSecondHalf =
                StringSelectMenu.create(getName() + ":stateselect2")
                        .setPlaceholder("Select what State you live in (26-50):");

        var count = 1;
        for (States state : States.values()) {

            if (!state.equals(States.UNKNOWN)) {
                var stateName = state.getFullName();

                if (count <= MAX_MENU_SELECTIONS) {
                    statesFirstHalf.addOption(stateName, stateName);
                } else {
                    statesSecondHalf.addOption(stateName, stateName);
                }
            }
            count++;
        }

        return new MessageCreateBuilder()
                .addActionRow(statesFirstHalf.build())
                .addActionRow(statesSecondHalf.build());
    }

    /**
     * Method to create a StringSelectMenu with the MAX_MENU_SELECTIONS most populated cities for
     * the given State.
     *
     * @param stateAbbreviation - the abbreviation of the State that we need to pull city data on.
     * @return A MessageCreateBuilder with the StringSelectMenu of cities for the given State.
     */
    @Nonnull
    @VisibleForTesting
    MessageCreateBuilder createCityMessageBuilder(@Nonnull String stateAbbreviation) {
        List<String> cities =
                cityController.getCitiesByState(stateAbbreviation, MAX_MENU_SELECTIONS);

        var menu =
                StringSelectMenu.create(getName() + ":cities")
                        .setPlaceholder("Select The City You Live In");

        for (String city : cities) {
            Objects.requireNonNull(city);
            menu.addOption(city, city);
        }

        return new MessageCreateBuilder().addActionRow(menu.build());
    }
}
