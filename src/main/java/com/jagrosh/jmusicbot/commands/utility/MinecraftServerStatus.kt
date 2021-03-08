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
package com.jagrosh.jmusicbot.commands.utility

import com.fasterxml.jackson.databind.ObjectMapper
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.UtilityCommand
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
import java.time.Instant

@Suppress("DEPRECATION")
class MinecraftServerStatus(bot: Bot) : UtilityCommand() {
    var log: Logger = LoggerFactory.getLogger("MCServerStatusCmd")
    override fun execute(event: CommandEvent) {
        val argsFormatted = event.args
        val argsWithoutAnd = argsFormatted
        if (event.args.isNullOrBlank()) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setTitle(":scream_cat: Please provide a server address!")
                    .setDescription("**Usage:** ${event.client.prefix}mcstatus <address> [port]")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        } else {
            val builder = MessageBuilder()

            val serverStatusResults = fetchServerStatusResults(argsWithoutAnd)

            val statusIcon = try {
                if (serverStatusResults["online"] == true) {
                    "https://res.cloudinary.com/keyboardsheep/image/upload/v1615095905/yh3yzcmxfmfwwkm0ph7o.png"
                } else {
                    "https://res.cloudinary.com/keyboardsheep/image/upload/v1615095896/nlu8kpmyn3efszvrkg5o.jpg"
                }
            } catch (e: Exception) {
                log.warn("Unable to fetch server status for ${event.args}", e)
                ""
            }
            val status = try {
                if (serverStatusResults["online"] == true) {
                    "Online"
                } else {
                    "Offline"
                }
            } catch (e: Exception) {
                log.warn("Unable to fetch server status for ${event.args}", e)
                "**Error checking server status.**"
            }
            val playersNow = try {
                (serverStatusResults["players"] as Map<*, *>)["now"]
            } catch (e: Exception) {
                log.warn("Unable to fetch current players for ${event.args}", e)
            }
            val playersMax = try {
                (serverStatusResults["players"] as Map<*, *>)["max"]
            } catch (e: Exception) {
                log.warn("Unable to fetch max players for ${event.args}", e)
            }
            val serverVersion = try {
                (serverStatusResults["server"] as Map<*, *>)["name"]
            } catch (e: Exception) {
                log.warn("Unable to fetch max players for ${event.args}", e)
            }

            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setAuthor(status, null, statusIcon)
                    .setDescription("**Players: ${playersNow}/${playersMax}**\n\n")
                    .appendDescription("**Version(s): $serverVersion**")
                    .setTimestamp(Instant.ofEpochSecond(serverStatusResults["last_updated"].toString().toLong()))
                    .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        }
    }

    private fun fetchServerStatusResults(argsWithoutAnd: String): Map<*, *> {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val port = args[1]?.toString()
            var realPort = "25565"
            if (port !== null) {
                realPort = args[1]
            }
            val httpget =
                    HttpGet("https://mcapi.us/server/status?ip=${args[0]}&port=$realPort")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody = httpclient.execute(httpget, responseHandler)

            (ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    init {
        this.category = Category("Utility")
        name = "mcstatus"
        help = "check the status of a minecraft server"
        arguments = "<address> [port]"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}