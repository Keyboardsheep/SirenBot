
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
package com.jagrosh.jmusicbot.commands.`fun`

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.*

class BreadCmd(bot: Bot) : BaseCatCmd() {
    var log: Logger = LoggerFactory.getLogger("BreadCmd")
    override fun execute(event: CommandEvent) {
        val breadNumber = Random().nextInt(71) + 1
        val now = System.currentTimeMillis()
        val channelId = event.channel.id
        val lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channelId, 0L)
        if (now > lastExecutionMillis + BaseCatCmd.Companion.QUIET_MILLIS) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setImage("https://media.siren.fun/bread/images%20($breadNumber).jpeg")
                .setDescription(":cat: **I found bread!**")
                .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
            lastExecutionMillisByChannelMap[channelId] = now
        } else {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("**Please slow down between commands!**")
                .setDescription("Please wait ** " + ((BaseCatCmd.Companion.QUIET_MILLIS - (now - lastExecutionMillis)) / 1000 + 1) + " ** more seconds.")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        }
    }

    init {
        this.category = Category("Fun")
        name = "bread"
        help = "shows some bread pictures"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}