/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.PingCommand;
import com.jagrosh.jmusicbot.commands.admin.*;
import com.jagrosh.jmusicbot.commands.dj.*;
import com.jagrosh.jmusicbot.commands.fun.*;
import com.jagrosh.jmusicbot.commands.general.HelpCmd;
import com.jagrosh.jmusicbot.commands.general.SettingsCmd;
import com.jagrosh.jmusicbot.commands.general.SirenAboutCmd;
import com.jagrosh.jmusicbot.commands.general.StatsCmd;
import com.jagrosh.jmusicbot.commands.mod.BanCmd;
import com.jagrosh.jmusicbot.commands.mod.KickCmd;
import com.jagrosh.jmusicbot.commands.mod.WarnCmd;
import com.jagrosh.jmusicbot.commands.music.*;
import com.jagrosh.jmusicbot.commands.owner.*;
import com.jagrosh.jmusicbot.commands.utility.CovidCmd;
import com.jagrosh.jmusicbot.entities.Prompt;
import com.jagrosh.jmusicbot.gui.GUI;
import com.jagrosh.jmusicbot.settings.SettingsManager;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;


public class SirenBot {
    public final static String PLAY_EMOJI = "\u25B6"; // ▶
    public final static String PAUSE_EMOJI = "\u23F8"; // ⏸
    public final static String STOP_EMOJI = "\u23F9"; // ⏹
    public final static Permission[] RECOMMENDED_PERMS = new Permission[]{Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION,
            Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_MANAGE, Permission.MESSAGE_EXT_EMOJI,
            Permission.MANAGE_CHANNEL, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.NICKNAME_CHANGE};

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // startup log
        Logger log = LoggerFactory.getLogger("Startup");

        // create prompt to handle startup
        Prompt prompt = new Prompt("SirenBot", "Switching to nogui mode. You can manually start in nogui mode by including the -Dnogui=true flag.",
                "true".equalsIgnoreCase(System.getProperty("nogui", "false")));

        // get and check latest version
        String version = OtherUtil.checkVersion(prompt);

        // load config
        BotConfig config = new BotConfig(prompt);
        config.load();
        if (!config.isValid())
            return;

        // set up the listener
        EventWaiter waiter = new EventWaiter();
        SettingsManager settings = new SettingsManager();
        Bot bot = new Bot(waiter, config, settings);

        SirenAboutCmd aboutCommand = new SirenAboutCmd(Color.BLUE.brighter(),
                "a music bot that is hosted by **Keybordsheep 82**.",
                new String[]{"Constantly updated", "FairQueue™ Technology", "Easy to control", "No paywalls"},
                RECOMMENDED_PERMS);
        aboutCommand.setIsAuthor(false);
        aboutCommand.setReplacementCharacter("\uD83C\uDFB6"); // 🎶

        // set up the command client
        RollCmd rollCmd = new RollCmd(bot);
        DevHelpCmd devHelpCmd = new DevHelpCmd(bot);
        HelpCmd helpCmd = new HelpCmd(bot);
        CommandClientBuilder cb = new CommandClientBuilder()
                .setPrefix(config.getPrefix())
                .setAlternativePrefix(config.getAltPrefix())
                .setOwnerId(Long.toString(config.getOwnerId()))
                .setEmojis(config.getSuccess(), config.getWarning(), config.getError())
                .setHelpWord("oldhelp")
                .setLinkedCacheSize(200)
                .setGuildSettingsManager(settings)
                .addCommands(aboutCommand,
                        new PingCommand(),
                        devHelpCmd,
                        new StatsCmd(bot),
                        new SettingsCmd(bot),
                        helpCmd,

                        new LyricsCmd(bot),
                        new NowplayingCmd(bot),
                        new PlayCmd(bot),
//                        new ErrorPlayCmd(bot),
                        new PlaylistsCmd(bot),
                        new QueueCmd(bot),
                        new RemoveCmd(bot),
                        new SearchCmd(bot),
                        new SCSearchCmd(bot),
                        new SeekCmd(bot),
                        new ShuffleCmd(bot),
                        new SkipCmd(bot),

                        new ForceRemoveCmd(bot),
                        new ForceskipCmd(bot),
                        new MoveTrackCmd(bot),
                        new PauseCmd(bot),
                        new PlaynextCmd(bot),
                        new RepeatCmd(bot),
                        new SkiptoCmd(bot),
                        new StopCmd(bot),
                        new VolumeCmd(bot),

                        new CatApiCatCmd(bot),
                        new CatCmd(bot),
                        new HttpCatCmd(bot),
                        new DogApiDogCmd(bot),
                        new CatFactCmd(bot),
                        new DogFactCmd(bot),
                        new SheepCmd(bot),
                        new BreadCmd(bot),
//                        new TriviaCmd(bot),
                        new ChatBotCmd(bot),
                        new LoveTestCmd(bot),
                        new CovidCmd(bot),
//                        new HypixelSkyblockCmd(bot),
                        rollCmd,

                        new WarnCmd(bot),
                        new KickCmd(bot),
                        new BanCmd(bot),

                        new PrefixCmd(bot),
                        new SetdjCmd(bot),
                        new SettcCmd(bot),
                        new SetvcCmd(bot),
                        new AnnounceEmbedCmd(bot),
//                        new PollCmd(bot),
                        new PruneCmd(bot),
                        new SetEmbedColorCmd(bot),

                        new AutoplaylistCmd(bot),
                        new GuildListCmd(bot.getWaiter()),
                        new UpdateEmbedCmd(bot),
                        new DebugCmd(bot),
                        new PlaylistCmd(bot),
                        new SetavatarCmd(bot),
                        new SetgameCmd(bot),
                        new SetnameCmd(bot),
                        new SetstatusCmd(bot),
                        new ShutdownCmd(bot),

                        new IpLookupCmd(bot)
                );
        if (config.useEval())
            cb.addCommand(new EvalCmd(bot));
        boolean nogame = false;
        if (config.getStatus() != OnlineStatus.UNKNOWN)
            cb.setStatus(config.getStatus());
        if (config.getActivity() == null)
            cb.useDefaultGame();
        else if (config.getActivity().getName().equalsIgnoreCase("none")) {
            cb.setActivity(null);
            nogame = true;
        } else
            cb.setActivity(config.getActivity());

        if (!prompt.isNoGUI()) {
            try {
                GUI gui = new GUI(bot);
                bot.setGUI(gui);
                gui.init();
            } catch (Exception e) {
                log.error("Could not start GUI. If you are "
                        + "running on a server or in a location where you cannot display a "
                        + "window, please run in nogui mode using the -Dnogui=true flag.");
            }
        }

        log.info("Loaded config from " + config.getConfigLocation());

        // attempt to log in and start
        try {
            CommandClient commandClient = cb.build();
            helpCmd.setCommandClient(commandClient);
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(config.getToken())
//                    .setAudioEnabled(true)
                    .setActivity(nogame ? null : Activity.playing("loading..."))
                    .setStatus(config.getStatus() == OnlineStatus.INVISIBLE || config.getStatus() == OnlineStatus.OFFLINE
                            ? OnlineStatus.INVISIBLE : OnlineStatus.DO_NOT_DISTURB)
                    .addEventListeners(commandClient, waiter, new Listener(bot, rollCmd, devHelpCmd))
                    .setBulkDeleteSplittingEnabled(true)
                    .build();
            bot.setJDA(jda);
            if (jda.getSelfUser().getId().equals("754375096734318712")) {
                startTopGgTimer(jda);
            } else {
                log.warn("'" + jda.getSelfUser().getId() + "' is not '754375096734318712', won't update top.gg server count.");
            }
        } catch (LoginException ex) {
            prompt.alert(Prompt.Level.ERROR, "SirenBot", ex + "\nPlease make sure you are "
                    + "editing the correct config.txt file, and that you have used the "
                    + "correct token (not the 'secret'!)\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        } catch (IllegalArgumentException ex) {
            prompt.alert(Prompt.Level.ERROR, "SirenBot", "Some aspect of the configuration is "
                    + "invalid: " + ex + "\nConfig Location: " + config.getConfigLocation());
            System.exit(1);
        }
    }

    private static void startTopGgTimer(JDA jda) {

        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                DiscordBotListAPI api = new DiscordBotListAPI.Builder()
                        .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Ijc1NDM3NTA5NjczNDMxODcxMiIsImJvdCI6dHJ1ZSwiaWF0IjoxNjA5OTA1MDg0fQ.-dABTYyNEsv6DjtQ6xnNvuNE7cQ_1mrjbzqnawjsISE")
                        .botId("754375096734318712")
                        .build();

                int serverCount = jda.getGuilds().size(); // the total amount of servers across all shards

                api.setStats(serverCount);
            }
        };
        Timer timer = new Timer("Timer");

        long delay = 240000L;
        long period = 1000L * 60L * 60L * 6L;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }
}
