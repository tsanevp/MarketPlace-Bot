package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.CityController;
import edu.northeastern.cs5500.starterbot.model.States;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu.Builder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Singleton
@Slf4j
public class ButtonCommand implements SlashCommandHandler, ButtonHandler {

    @Inject
    public ButtonCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "button";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Demonstrate a button interaction");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /button");

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder.addActionRow(
                        Button.primary(this.getName() + ":ok", States.TEXAS.toString()),
                        Button.danger(this.getName() + ":cancel", "Cancel"));
        messageCreateBuilder = messageCreateBuilder.setContent("Example buttons");
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        try {
            CityController cityController = new CityController();
            List<String> cities =
                    cityController.getCitiesByState(
                            States.valueOfName(event.getButton().getLabel()).getStateCode());
            Builder menu =
                    StringSelectMenu.create(getName())
                            .setPlaceholder("Select The City You Live In");
            for (String city : cities) {
                menu.addOption(city, city);
            }
            MessageCreateBuilder messageCreateBuilder =
                    new MessageCreateBuilder().addActionRow(menu.build());
            event.reply(messageCreateBuilder.build()).queue();

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
