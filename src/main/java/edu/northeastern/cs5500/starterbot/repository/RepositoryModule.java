package edu.northeastern.cs5500.starterbot.repository;

import dagger.Module;
import dagger.Provides;
import edu.northeastern.cs5500.starterbot.model.Listing;
import edu.northeastern.cs5500.starterbot.model.User;

@Module
public class RepositoryModule {
    @Provides
    public GenericRepository<Listing> providelistingRepository(
            MongoDBRepository<Listing> repository) {
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
