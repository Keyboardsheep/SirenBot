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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

@Suppress("DEPRECATION")
class HypixelSkyblockCmd(bot: Bot) : BaseCatCmd() {
    var log: Logger = LoggerFactory.getLogger("HypixelSkyblockBalCmd")
    override fun execute(event: CommandEvent) {
        val argsFormatted = java.net.URLEncoder.encode(event.args, "utf-8")
        val argsWithoutAnd = argsFormatted.replace("+and+", " ")
        val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
        if (event.args.isNullOrBlank()) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setTitle(":scream_cat: Please specify a user!")
                    .setDescription("**Usage:** ${event.client.prefix}skyblock <username>")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        } else {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setTitle("${args[0]}'s Skyblock Info:")
                    .setDescription(":bank: **$${skyblockBankBal(argsWithoutAnd, event)}**")
                    .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        }
    }

    private fun skyblockBankBal(argsWithoutAnd: String, bankBal: CommandEvent): String {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val httpget = HttpGet("https://sky.shiiyu.moe/api/v2/coins/${args[0]}")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody: String? = null
            responseBody = try {
                httpclient.execute(httpget, responseHandler)
            } catch (e: IOException) {
                log.warn("Unable to fetch skyblock bank bal.", e)
                return "Unable to detect bank balance."
            }
            try {
                ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["bank"] as String
            } catch (e: JsonProcessingException) {
                log.warn("Unable to read skyblock bank bal response.", e)
                return "Unable to detect bank balance, check the username."
            }
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    init {
        this.category = Category("Fun")
        name = "skyblock"
        help = "view hypixel skyblock stats"
        arguments = "<username>"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}