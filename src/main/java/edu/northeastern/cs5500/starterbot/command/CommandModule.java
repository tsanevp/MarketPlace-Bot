package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.northeastern.cs5500.starterbot.command.handlers.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.NewGuildJoinedHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.NewMemberHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.RemoveMemberHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.command.handlers.StringSelectHandler;

@Module
public class CommandModule {

    @Provides
    public NewGuildJoinedHandler provideNewGuildJoin(NewGuildJoined newGuildJoined) {
        return newGuildJoined;
    }

    @Provides
    @IntoSet
    public StringSelectHandler provideLocation(Location stringSelectLocation) {
        return stringSelectLocation;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideButtonNewGuildJoinedHandler(NewGuildJoined buttonCommand) {
        return buttonCommand;
    }

    @Provides
    public NewMemberHandler provideNewMember(NewMember newMember) {
        return newMember;
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
    public SlashCommandHandler provideMyListingCommand(
            MyListingsCommand myListingCommand) {
        return myListingCommand;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideMyListingCommandButton(
            MyListingsCommand myListingCommand) {
        return myListingCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideUpdateLocationCommand(
            UpdateLocationCommand updateLocationCommand) {
        return updateLocationCommand;
    }

    @Provides
    public RemoveMemberHandler provideRemoveMember(RemoveMember removeMember) {
        return removeMember;
    }
}
