package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

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
    public StringSelectHandler provideNewGuildJoinedHandler(
            NewGuildJoined stringSelectGuildJoined) {
        return stringSelectGuildJoined;
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
    public SlashCommandHandler provideViewMyListingCommand(
            ViewMyListingCommand viewMyListingCommand) {
        return viewMyListingCommand;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideViewMyListingCommandButton(
            ViewMyListingCommand viewMyListingCommand) {
        return viewMyListingCommand;
    }

    @Provides
    @IntoSet
    public ButtonHandler provideButtonCreateListingClickHandler(
            CreateListingCommand buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoSet
    public SlashCommandHandler provideUpdateLocationCommand(
            UpdateLocationCommand updateLocationCommand) {
        return updateLocationCommand;
    }
}
