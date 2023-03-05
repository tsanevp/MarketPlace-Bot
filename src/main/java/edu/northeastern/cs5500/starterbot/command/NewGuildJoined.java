package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.UserController;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

@Singleton
@Slf4j
public class NewGuildJoined implements NewGuildJoinedHandler {

    @Inject UserController userController;

    @Inject
    public NewGuildJoined() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return "newmember";
    }

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        log.info("event: newguildjoined");
        // Gets all the current channels in the Guild and checks if trading-channel already exists
        List<GuildChannel> guildChannels = event.getGuild().getChannels();
        for (GuildChannel guildChannel : guildChannels) {
            if ("trading-channel".equals(guildChannel.getName())) {
                return;
            }
        }
        // Created trading-channel if it does not yet exist
        event.getGuild().createTextChannel("trading-channel").complete();
    }
}
