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
import com.jagrosh.jmusicbot.commands.FunCommand
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class ChatBotCmd(bot: Bot) : FunCommand() {
    var log: Logger = LoggerFactory.getLogger("ChatBotCmd")
    override fun execute(event: CommandEvent) {
        val now = System.currentTimeMillis()
        val message = java.net.URLEncoder.encode(event.args, "utf-8")
        val channelId: String = event.channel.id
        if (event.args.isNullOrBlank()) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setTitle(":scream_cat: Please say something to me!")
                    .setDescription("**Usage:** ${event.client.prefix}chatbot <message>")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        } else {
            event.channel.sendMessage(
                "> ${
                    event.message.contentDisplay.replace(
                        "${event.client.prefix}chatbot ",
                        "",
                        true
                    ).replace("${event.client.altPrefix}chatbot ", "", true)
                }\n**(${event.author.asMention})** ${getChatBotResponse(message, event)}"
            ).queue()
        }
    }

    private fun getChatBotResponse(message: String, event: CommandEvent): String {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val httpget = HttpGet("https://www.siren.fun/api/chatbot?uuid=${event.author.id}&guild=${event.guild.id}&msg=$message")
            println("executing request " + httpget.uri)

            // Create a response handler
            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            // Body contains your json stirng
            var responseBody: String? = null
            responseBody = try {
                httpclient.execute(httpget, responseHandler)
            } catch (e: IOException) {
                log.warn("Unable to fetch chatbot response.", e)
                return "https://http.cat/500"
            }
            try {
                ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["cnt"] as String
            } catch (e: JsonProcessingException) {
                log.warn("Unable to read chatbot response.", e)
                return "https://http.cat/400"
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
        name = "chatbot"
        help = "lets you have a conversation with siren"
        arguments = "<message>"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}