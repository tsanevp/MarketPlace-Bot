package edu.northeastern.cs5500.starterbot.service;

import dagger.Module;
import dagger.Provides;
import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Module
public abstract class ServiceModule { // NOSONAR

    static String getBotToken() {
        return new ProcessBuilder().environment().get("BOT_TOKEN");
    }

    @Provides
    @Singleton
    static JDA provideJDA() {
        String token = getBotToken();
        if (token == null) {
            throw new IllegalArgumentException(
                    "The BOT_TOKEN environment variable is not defined.");
        }
        @SuppressWarnings("null")
        @Nonnull
        Collection<GatewayIntent> intents = EnumSet.of(GatewayIntent.GUILD_MEMBERS);
        return JDABuilder.createLight(token, intents)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
    }
}
