package edu.northeastern.cs5500.starterbot.discord;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.northeastern.cs5500.starterbot.discord.commands.CreateListingCommand;
import edu.northeastern.cs5500.starterbot.discord.commands.CreateTradingChannelCommand;
import edu.northeastern.cs5500.starterbot.discord.commands.MyListingsCommand;
import edu.northeastern.cs5500.starterbot.discord.commands.SearchListingsCommand;
import edu.northeastern.cs5500.starterbot.discord.commands.UpdateLocationCommand;
import edu.northeastern.cs5500.starterbot.discord.events.LeaveGuildEvent;
import edu.northeastern.cs5500.starterbot.discord.events.NewGuildJoinedEvent;
import edu.northeastern.cs5500.starterbot.discord.events.NewMemberEvent;
import edu.northeastern.cs5500.starterbot.discord.events.RemoveMemberEvent;
import edu.northeastern.cs5500.starterbot.discord.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.LeaveGuildEventHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewGuildJoinedHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.NewMemberHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.RemoveMemberHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.discord.handlers.StringSelectHandler;

@Module
public class CommandModule {

    @Provides
    public NewMemberHandler provideNewMember(NewMemberEvent newMember) {
        return newMember;
    }

    @Provides
    public NewGuildJoinedHandler provideNewGuildJoin(NewGuildJoinedEvent newGuildJoined) {
        return newGuildJoined;
    }

    @Provides
    public LeaveGuildEventHandler provideLeaveGuildevent(LeaveGuildEvent guildLeaveEvent) {
        return guildLeaveEvent;
    }

    @Provides
    public RemoveMemberHandler provideRemoveMember(RemoveMemberEvent removeMember) {
        return removeMember;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideButtonNewGuildJoinedHandler(NewGuildJoinedEvent buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoSet
    public StringSelectHandler provideLocation(SettingLocationHelper stringSelectLocation) {
        return stringSelectLocation;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideCreateListingCommand(
            CreateListingCommand createListingCommand) {
        return createListingCommand;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideButtonCreateListingClickHandler(
            CreateListingCommand buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideCreateTradingChannelCommand(
            CreateTradingChannelCommand createTradingChannelCommand) {
        return createTradingChannelCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideMyListingCommand(MyListingsCommand myListingCommand) {
        return myListingCommand;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideMyListingCommandButton(MyListingsCommand myListingCommand) {
        return myListingCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideUpdateLocationCommand(
            UpdateLocationCommand updateLocationCommand) {
        return updateLocationCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideSearchListingsCommand(
            SearchListingsCommand searchListingsCommand) {
        return searchListingsCommand;
    }

    @Provides
    @IntoSet
    public StringSelectHandler provideSortingOption(SearchListingsCommand searchListingsCommand) {
        return searchListingsCommand;
    }
}
