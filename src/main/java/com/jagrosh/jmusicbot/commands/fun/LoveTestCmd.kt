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

@Suppress("DEPRECATION")
class LoveTestCmd(bot: Bot) : BaseCatCmd() {
    var log: Logger = LoggerFactory.getLogger("LoveTestCmd")
    override fun execute(event: CommandEvent) {
        val argsFormatted = java.net.URLEncoder.encode(event.args, "utf-8")
        val argsWithoutAnd = argsFormatted.replace("+and+", " ")
        if (event.args.isNullOrBlank()) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setTitle(":scream_cat: Please list two names!")
                .setDescription("**Usage:** ${event.client.prefix}lovetest <name one> and <name two>")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        } else {
            val builder = MessageBuilder()
            val title = try {
                val loveTestResults = fetchLoveTestResults(argsWithoutAnd)
                "**:one: ${loveTestResults["fname"]}\n\n" +
                        ":two: ${loveTestResults["sname"]}\n\n" +
                        ":bar_chart: ${loveTestResults["percentage"]}%\n\n" +
                        ":pencil: ${loveTestResults["result"]}**\n\n"
            } catch (e: Exception) {
                log.warn("Unable to fetch lovetest.", e)
                "**Unable to compute lovetest.**"
            }

            val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setAuthor(
                    "Love Test Results:",
                    null,
                    "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Heart_coraz%C3%B3n.svg/1200px-Heart_coraz%C3%B3n.svg.png"
                )
                .setTitle(title)
                .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        }
    }

    private fun fetchLoveTestResults(argsWithoutAnd: String): Map<*, *> {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val httpget =
                HttpGet("https://www.siren.fun/api/lovetest?f=${args[0]}&s=${args[1]}")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody = httpclient.execute(httpget, responseHandler)

            (ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    init {
        this.category = Category("Fun")
        name = "lovetest"
        help = "test the chances of relationship"
        arguments = "<name one> and <name two>"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}