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
class LoveTestCmd(bot: Bot) : BaseCatCmd() {
    var log: Logger = LoggerFactory.getLogger("LoveTestCmd")
    override fun execute(event: CommandEvent) {
        val argsFormatted = java.net.URLEncoder.encode(event.args, "utf-8")
        val argsWithoutAnd = argsFormatted.replace("+and+", " ")
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setAuthor("Love Test Results:", null, "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Heart_coraz%C3%B3n.svg/1200px-Heart_coraz%C3%B3n.svg.png")
                .setTitle("**:one: ${loveTestFname(argsWithoutAnd, event)}\n\n" +
                        ":two: ${loveTestSname(argsWithoutAnd, event)}\n\n" +
                        ":bar_chart: ${loveTestPercentage(argsWithoutAnd, event)}%\n\n" +
                        ":pencil: ${loveTestNotes(argsWithoutAnd, event)}**")
                .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    private fun loveTestPercentage(argsWithoutAnd: String, kittyFact: CommandEvent): String {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val httpget = HttpGet("https://love-calculator.p.rapidapi.com/getPercentage?fname=${args[0]}&sname=${args[1]}&rapidapi-key=9bd583665emsh222e22b0a648b81p10c06bjsnfb5eef2cbb62")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody: String? = null
            responseBody = try {
                httpclient.execute(httpget, responseHandler)
            } catch (e: IOException) {
                log.warn("Unable to fetch lovetest.", e)
                return "Unable to compute lovetest."
            }
            try {
                ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["percentage"] as String
            } catch (e: JsonProcessingException) {
                log.warn("Unable to read lovetest response.", e)
                return "Unable to read lovetest response."
            }
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    private fun loveTestNotes(argsWithoutAnd: String, kittyFact: CommandEvent): String {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val httpget = HttpGet("https://love-calculator.p.rapidapi.com/getPercentage?fname=${args[0]}&sname=${args[1]}&rapidapi-key=9bd583665emsh222e22b0a648b81p10c06bjsnfb5eef2cbb62")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody: String? = null
            responseBody = try {
                httpclient.execute(httpget, responseHandler)
            } catch (e: IOException) {
                log.warn("Unable to fetch lovetest.", e)
                return "Unable to compute lovetest."
            }
            try {
                ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["result"] as String
            } catch (e: JsonProcessingException) {
                log.warn("Unable to read lovetest response.", e)
                return "Unable to read lovetest response."
            }
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    private fun loveTestFname(argsWithoutAnd: String, kittyFact: CommandEvent): String {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val httpget = HttpGet("https://love-calculator.p.rapidapi.com/getPercentage?fname=${args[0]}&sname=${args[1]}&rapidapi-key=9bd583665emsh222e22b0a648b81p10c06bjsnfb5eef2cbb62")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody: String? = null
            responseBody = try {
                httpclient.execute(httpget, responseHandler)
            } catch (e: IOException) {
                log.warn("Unable to fetch lovetest.", e)
                return "Unable to compute lovetest."
            }
            try {
                ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["fname"] as String
            } catch (e: JsonProcessingException) {
                log.warn("Unable to read lovetest response.", e)
                return "Unable to read lovetest response."
            }
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    private fun loveTestSname(argsWithoutAnd: String, kittyFact: CommandEvent): String {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val args = argsWithoutAnd.split(" ".toRegex()).toTypedArray()
            val httpget = HttpGet("https://love-calculator.p.rapidapi.com/getPercentage?fname=${args[0]}&sname=${args[1]}&rapidapi-key=9bd583665emsh222e22b0a648b81p10c06bjsnfb5eef2cbb62")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody: String? = null
            responseBody = try {
                httpclient.execute(httpget, responseHandler)
            } catch (e: IOException) {
                log.warn("Unable to fetch lovetest.", e)
                return "Unable to compute lovetest."
            }
            try {
                ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["sname"] as String
            } catch (e: JsonProcessingException) {
                log.warn("Unable to read lovetest response.", e)
                return "Unable to read lovetest response."
            }
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    init {
        this.category = Category("Fun")
        name = "lovetest"
        help = "test the chances of relationship"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}