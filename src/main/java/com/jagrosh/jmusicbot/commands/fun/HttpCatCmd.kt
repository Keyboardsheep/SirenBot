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
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.*

class HttpCatCmd(bot: Bot) : BaseCatCmd() {
    var log = LoggerFactory.getLogger("HttpCatCmd")
    override fun execute(event: CommandEvent) {
        val now = System.currentTimeMillis()
        val channelId = event.channel.id
        val lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channelId, 0L)
        if (now > lastExecutionMillis + BaseCatCmd.Companion.QUIET_MILLIS) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setImage(kittyUrl)
                    .setDescription("**I found a http status kitty!** :cat:")
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

    //// TODO: 10/4/2020 add the option of selecting a http cat (Example: siren httpcat 404)
    private val kittyUrl: String
        private get() {
            //// TODO: 10/4/2020 add the option of selecting a http cat (Example: siren httpcat 404)
            val statuses = arrayOf(100, 101, 200, 201, 202, 204, 206, 206, 300, 301, 302, 304, 305, 307, 401, 402, 403, 404, 405, 406, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 421, 422, 423, 424, 425, 426, 429, 431, 444, 451, 500, 501, 502, 503, 504, 506, 507, 508, 509, 510, 511, 599)
            return "https://http.cat/" + statuses[Random().nextInt(statuses.size)]
        }

    init {
        name = "httpcat"
        help = "shows some http kitties"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}