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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.slf4j.LoggerFactory
import java.awt.Color
import java.io.IOException

class DogApiDogCmd(bot: Bot) : BaseCatCmd() {
    var log = LoggerFactory.getLogger("DogCmd")
    override fun execute(event: CommandEvent) {
        val now = System.currentTimeMillis()
        val channelId = event.channel.id
        val lastExecutionMillis = lastExecutionMillisByChannelMap.getOrDefault(channelId, 0L)
        if (now > lastExecutionMillis + BaseCatCmd.Companion.QUIET_MILLIS) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setImage(kittyUrl)
                    .setDescription(":pouting_cat: **I found a doggo, next time look for a cat!**")
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
    }// When HttpClient instance is no longer needed,

    // shut down the connection manager to ensure
    // immediate deallocation of all system resources
    // Create a response handler
    private val kittyUrl: String
        // Body contains your json stirng
        private get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://www.siren.fun/api/dog")
                println("executing request " + httpget.uri)

                // Create a response handler
                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                // Body contains your json stirng
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch dog.", e)
                    return "https://http.cat/500"
                }
                try {
                    ((ObjectMapper().readValue(responseBody, MutableList::class.java) as List<*>).get(0) as Map<*, *>)["url"] as String
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read dog response.", e)
                    "https://http.cat/400"
                }
            } finally {
                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpclient.connectionManager.shutdown()
            }
        }

    init {
        this.category = Category("Fun")
        name = "dog"
        help = "shows some unknown doggos"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}