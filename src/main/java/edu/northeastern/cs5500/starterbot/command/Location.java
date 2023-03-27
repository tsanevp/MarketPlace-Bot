package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.CityController;
import edu.northeastern.cs5500.starterbot.controller.UserController;
import edu.northeastern.cs5500.starterbot.model.States;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.Builder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class Location implements StringSelectHandler {
    private static final Integer EMBED_COLOR = 0x00FFFF;
    @Inject UserController userController;

    @Inject
    public Location() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "location";
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        String id = event.getComponentId();
        Objects.requireNonNull(id);
        String handlerName = id.split(":", 2)[1];
        if ("cities".equals(handlerName)) {
            userController.setCityOfResidence(
                    event.getUser().getId(), event.getInteraction().getValues().get(0));
            event.deferEdit()
                    .setComponents()    
                    .setEmbeds(
                            new EmbedBuilder()
                                    .setDescription(
                                            "The selected city and state have been set as your general location.")
                                    .setColor(EMBED_COLOR)
                                    .build())
                    .queue();
        } else {
            try {
                CityController cityController = new CityController();
                userController.setStateOfResidence(
                        event.getUser().getId(),
                        States.valueOfName(event.getInteraction().getValues().get(0))
                                .getAbbreviation());
                event.deferEdit()
                        .setComponents()
                        .queue();

                List<String> cities =
                        cityController.getCitiesByState(
                                States.valueOfName(event.getInteraction().getValues().get(0))
                                        .getStateCode());
                Builder menu =
                        StringSelectMenu.create(this.getName() + ":cities")
                                .setPlaceholder("Select The City You Live In");
                for (String city : cities) {
                    Objects.requireNonNull(city);    
                    menu.addOption(city, city);
                }
                MessageCreateBuilder messageCreateBuilder =
                        new MessageCreateBuilder().addActionRow(menu.build());
                event.getUser()
                        .openPrivateChannel()
                        .complete()
                        .sendMessage(messageCreateBuilder.build())
                        .queue();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public MessageCreateBuilder createStatesMessageBuilder() {
        Builder statesFirstHalf =
                StringSelectMenu.create(this.getName() + ":stateselect1")
                        .setPlaceholder("Select what State you live in (1-25):");
        Builder statesSecondHalf =
                StringSelectMenu.create(this.getName() + ":stateselect2")
                        .setPlaceholder("Select what State you live in (26-50):");
        int count = 1;
        for (States state : States.values()) {
            if (!state.equals(States.UNKNOWN)) {
                String stateName = Objects.requireNonNull(state.name());
                if (count <= 25) {
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
}
