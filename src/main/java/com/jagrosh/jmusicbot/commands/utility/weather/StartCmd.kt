package com.jagrosh.jmusicbot.commands.utility.weather

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message

class StartCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setTitle("**Weather Location Setup**")
                .setDescription("**Please click [here](https://www.siren.fun/new-weather/setup#${event.author.id}) then follow the instructions.**")
        if (event.channel.type.isGuild) {
            event.channel.sendMessage("Please check your DMs to continue setting up Siren weather.").queue()
        }

        event.replyInDm(
                ebuilder.build(),
                { unused: Message? -> if (event.isFromType(ChannelType.TEXT)) event.reactSuccess() }) { t: Throwable? ->
            event.replyWarning(
                    "Weather verification message cannot be sent because you are blocking Direct Messages."
            )
        }
    }

    init {
        name = "weather"
        help = "posts an announcement **(BETA)**"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}

