/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import java.lang.management.ManagementFactory
import java.math.BigDecimal
import java.math.RoundingMode

class StatsCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val totalMem = Runtime.getRuntime().totalMemory()
        val usedMem = totalMem - Runtime.getRuntime().freeMemory()
        val usedMemBD: BigDecimal
        val totalMemBD: BigDecimal
        usedMemBD = BigDecimal(usedMem)
        totalMemBD = BigDecimal(totalMem)
        val memPercent = BigDecimal(100)
        val builder = MessageBuilder()
        val trueRamPercent = usedMemBD.divide(totalMemBD, 4, RoundingMode.HALF_DOWN).multiply(memPercent)
        val duration: Long = ManagementFactory.getRuntimeMXBean().uptime

        val years = duration / 31104000000L
        val months = duration / 2592000000L % 12
        val days = duration / 86400000L % 30
        val hours = duration / 3600000L % 24
        val minutes = duration / 60000L % 60
        val seconds = duration / 1000L % 60

        val uptime: String? = ((if (years == 0L) "" else "**$years**y ") + (if (months == 0L) "" else "**$months**mo ") + (if (days == 0L) "" else "**$days**d ") + (if (hours == 0L) "" else "**$hours**h ")
                + (if (minutes == 0L) "" else "**$minutes**m ") + if (seconds == 0L) "" else "**$seconds**s ") /* + (milliseconds == 0 ? "" : milliseconds + " Milliseconds, ") */
        val voiceConnections = event.jda.guilds.stream().filter { g: Guild -> g.selfMember.voiceState!!.inVoiceChannel() }.count()
        val actualTrueRamPercent = trueRamPercent.setScale(2, BigDecimal.ROUND_HALF_UP)
        var owner: User? = event.jda.getUserById(event.client.ownerId)
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setTitle("${event.selfUser.name}'s Stats:")
//                .setThumbnail("https://i.imgur.com/4shAsfd.png")
                .addField("Owner", "${owner?.asMention}", true)
                .addField("Ram Usage", "$actualTrueRamPercent%", true)
                .addField("Ping", "**${event.jda.gatewayPing}**ms", true)
                .addField("Uptime", "$uptime", true)
                .addField("Users", "${event.jda.guilds.stream().mapToInt { g: Guild -> g.members.size }.sum()}", true)
                .addField("Guilds", "${event.jda.guilds.size}", true)
                .addField("Streams", "$voiceConnections Active", true)
                .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    init {
        name = "stats"
        help = "displays Siren's varius stats"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }

}