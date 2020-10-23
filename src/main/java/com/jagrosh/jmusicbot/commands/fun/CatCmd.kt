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
import com.jagrosh.jmusicbot.utils.OtherUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class CatCmd(bot: Bot) : BaseCatCmd() {
    var log = LoggerFactory.getLogger("CatCmd")
    private var path: Path? = null
    fun createKittyListIfNeeded() {
        val kittyUrls = ArrayList<String>()
        kittyUrls.add("https://i.imgur.com/ntoWhyl.jpg")
        kittyUrls.add("https://i.imgur.com/PjlFbn4.jpg")
        kittyUrls.add("https://i.imgur.com/yPLXCc8.jpg")
        kittyUrls.add("https://i.imgur.com/oTRY5Of.jpg")
        kittyUrls.add("https://i.imgur.com/oj9zJyP.jpg")
        kittyUrls.add("https://i.imgur.com/RyAS0CC.jpg")
        kittyUrls.add("https://i.imgur.com/fDgItE6.jpg")
        kittyUrls.add("https://i.imgur.com/bLg792h.jpg")
        kittyUrls.add("https://i.imgur.com/1TqhWEu.jpg")
        kittyUrls.add("https://i.imgur.com/rQsbTtC.jpg")

        // get the path to the kitty config, default kitties.txt
        path = OtherUtil.getPath(System.getProperty("kittyList", "kitties.txt"))
        if (!(path as Path).toFile().exists()) {
            try {
                val urlsString = java.lang.String.join("\n", kittyUrls)
                Files.write(path, urlsString.toByteArray())
            } catch (e: IOException) {
                startupLog.error("Unable to create kittyList: $path", e)
            }
        }
        startupLog.info("Loaded kittyList from $path")
    }

    override fun execute(event: CommandEvent) {
        val now = System.currentTimeMillis()
        val channelId = event.channel.id
        val lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channelId, 0L)
        if (now > lastExecutionMillis + BaseCatCmd.Companion.QUIET_MILLIS) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setImage(kittyUrl)
                    .setDescription("**I found a kitty!** :cat:")
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

    private val kittyUrl: String
        private get() {
            val urls: List<String>
            urls = try {
                Files.readAllLines(path)
            } catch (e: IOException) {
                throw RuntimeException("Unable to load kittyList: $path", e)
            }
            val url = urls[Random().nextInt(urls.size)]
            log.info("Loading kitty url: $url")
            return url
        }

    init {
        this.category = Category("Fun")
        name = "mycat"
        help = "shows some of my kitties"
        aliases = bot.config.getAliases(name)
        guildOnly = false
        createKittyListIfNeeded()
    }
}