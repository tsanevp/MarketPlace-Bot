package edu.northeastern.cs5500.starterbot.listener;

import edu.northeastern.cs5500.starterbot.discord.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.LeaveGuildEventHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewGuildJoinedHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewMemberHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.RemoveMemberHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.exceptions.GuildNotFoundException;
import edu.northeastern.cs5500.starterbot.exceptions.GuildOwnerNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
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
    @Inject NewMemberHandler newMemberEvent;
    @Inject NewGuildJoinedHandler newGuildJoined;
    @Inject RemoveMemberHandler removeMember;
    @Inject LeaveGuildEventHandler guildLeaveEvent;

    @Inject
    public MessageListener() {
        super();
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        for (SlashCommandHandler command : commands) {
            if (command.getName().equals(event.getName())) {
                try {
                    command.onSlashCommandInteraction(event);
                } catch (GuildNotFoundException | GuildOwnerNotFoundException e) {
                    log.error("There was an error in the slash command interaction", e);
                }
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
                try {
                    buttonHandler.onButtonInteraction(event);
                } catch (GuildNotFoundException e) {
                    log.error("There was an error in the button interaction", e);
                }
                return;
            }
        }

        log.error("Unknown button handler: {}", handlerName);
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        log.info("onStringSelectInteraction: {}", event.getComponent().getId());
        String handlerName = event.getComponent().getId();
        Objects.requireNonNull(handlerName);
        handlerName = handlerName.split(":", 2)[0];

        for (StringSelectHandler stringSelectHandler : stringSelects) {
            if (stringSelectHandler.getName().equals(handlerName)) {
                stringSelectHandler.onStringSelectInteraction(event);
                return;
            }
        }

        log.error("Unknown string select handler: {}", handlerName);
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        newMemberEvent.onGuildMemberJoin(event);
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        try {
            newGuildJoined.onGuildJoin(event);
        } catch (GuildOwnerNotFoundException e) {
            log.error("The guild owner could not be assigned when the bot joined the guild", e);
        }
    }

    @Override
    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        guildLeaveEvent.onGuildLeaveEvent(event);
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        removeMember.onGuildMemberRemove(event);
    }
}
