package edu.northeastern.cs5500.starterbot.listener;

import edu.northeastern.cs5500.starterbot.command.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.command.StringSelectHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Slf4j
public class MessageListener extends ListenerAdapter {

    @Inject Set<SlashCommandHandler> commands;
    @Inject Set<ButtonHandler> buttons;
    @Inject Set<StringSelectHandler> stringSelects;

    @Inject
    public MessageListener() {
        super();
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        for (SlashCommandHandler command : commands) {
            if (command.getName().equals(event.getName())) {
                command.onSlashCommandInteraction(event);
                return;
            }
        }
    }

    public @Nonnull Collection<CommandData> allCommandData() {
        Collection<CommandData> commandData =
                commands.stream()
                        .map(SlashCommandHandler::getCommandData)
                        .collect(Collectors.toList());
        if (commandData == null) {
            return new ArrayList<>();
        }
        return commandData;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        log.info("onButtonInteraction: {}", event.getButton().getId());
        String id = event.getButton().getId();
        Objects.requireNonNull(id);
        String handlerName = id.split(":", 2)[0];

        for (ButtonHandler buttonHandler : buttons) {
            if (buttonHandler.getName().equals(handlerName)) {
                buttonHandler.onButtonInteraction(event);
                return;
            }
        }

        log.error("Unknown button handler: {}", handlerName);
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        log.info("onStringSelectInteraction: {}", event.getComponent().getId());
        String handlerName = event.getComponent().getId();

        for (StringSelectHandler stringSelectHandler : stringSelects) {
            if (stringSelectHandler.getName().equals(handlerName)) {
                stringSelectHandler.onStringSelectInteraction(event);
                return;
            }
        }

        log.error("Unknown button handler: {}", handlerName);
    }
}
