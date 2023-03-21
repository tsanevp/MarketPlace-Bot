package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.User;
import edu.northeastern.cs5500.starterbot.model.UserPreference;
import edu.northeastern.cs5500.starterbot.model.Listing;

@Module
public class RepositoryModule {
    // NOTE: You can use the following lines if you'd like to store objects in memory.
    // NOTE: The presence of commented-out code in your project *will* result in a lowered grade.
    // @Provides
    // public GenericRepository<UserPreference> provideUserPreferencesRepository(
    //         InMemoryRepository<UserPreference> repository) {
    //     return repository;
    // }

    // @Provides
    // public GenericRepository<User> provideUserRepository(InMemoryRepository<User> repository) {
    //     return repository;
    // }

    @Provides
    public GenericRepository<Listing> providelistingRepository(MongoDBRepository<Listing> repository) {
        return repository;
    }

    @Provides
    public GenericRepository<User> provideUserRepository(MongoDBRepository<User> repository) {
        return repository;
    }

    @Provides
    public Class<Listing> provideListing() {
        return Listing.class;
    }

    @Provides
    public Class<User> provideUser() {
        return User.class;
    }
}
