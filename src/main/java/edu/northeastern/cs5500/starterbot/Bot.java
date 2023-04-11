package edu.northeastern.cs5500.starterbot;

import dagger.Component;
import edu.northeastern.cs5500.starterbot.command.CommandModule;
import edu.northeastern.cs5500.starterbot.listener.MessageListener;
import edu.northeastern.cs5500.starterbot.repository.RepositoryModule;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

@Component(modules = {CommandModule.class, RepositoryModule.class})
@Singleton
interface BotComponent {
    public Bot bot();
}

public class Bot {

    @Inject
    Bot() {}

    @Inject MessageListener messageListener;

    static String getBotToken() {
        return new ProcessBuilder().environment().get("BOT_TOKEN");
    }

    void start() {
        String token = getBotToken();
        if (token == null) {
            throw new IllegalArgumentException(
                    "The BOT_TOKEN environment variable is not defined.");
        }
        @SuppressWarnings("null")
        @Nonnull
        JDA jda =
                JDABuilder.createLight(token, GatewayIntent.GUILD_MEMBERS)
                        .addEventListeners(messageListener)
                        .setChunkingFilter(ChunkingFilter.ALL)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .build();

        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(messageListener.allCommandData());
        commands.queue();
    }
}
