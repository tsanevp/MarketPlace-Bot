package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.UserPreference;

@Module
public class RepositoryModule {
    // NOTE: You can use the following lines if you'd like to store objects in memory.
    // NOTE: The presence of commented-out code in your project *will* result in a lowered grade.
    @Provides
    public GenericRepository<UserPreference> provideUserPreferencesRepository(
            InMemoryRepository<UserPreference> repository) {
        return repository;
    }

    // @Provides
    // public GenericRepository<UserPreference> provideUserPreferencesRepository(
    //         MongoDBRepository<UserPreference> repository) {
    //     return repository;
    // }

    // @Provides
    // public Class<UserPreference> provideUserPreference() {
    //     return UserPreference.class;
    // }
}
