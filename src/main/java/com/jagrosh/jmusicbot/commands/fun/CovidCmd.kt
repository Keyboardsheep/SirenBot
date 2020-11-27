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
import java.text.NumberFormat
import java.util.*

@Suppress("DEPRECATION")
class CovidCmd(bot: Bot) : BaseCatCmd() {
    var log: Logger = LoggerFactory.getLogger("CovidCmd")
    override fun execute(event: CommandEvent) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = java.util.Date(timeUpdated)
        sdf.format(date)
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setTitle("**:cat: Latest COVID-19 Statistics:**")
                .addField("Total Cases", (NumberFormat.getNumberInstance(Locale.US).format(totalCases)), true)
                .addField("Total Recovered", (NumberFormat.getNumberInstance(Locale.US).format(totalRecovered)), true)
                .addField("Active Cases", (NumberFormat.getNumberInstance(Locale.US).format(activeCases)), true)
                .addField("Cases Today", (NumberFormat.getNumberInstance(Locale.US).format(todayCases)), true)
                .addField("Recovered Today", (NumberFormat.getNumberInstance(Locale.US).format(todayRecovered)), true)
                .addField("Critical Cases", (NumberFormat.getNumberInstance(Locale.US).format(criticalCases)), true)
                .setFooter("Last Updated: $date")
                .setDescription("COVID-19 is caused by a coronavirus called SARS-CoV-2. Older adults and people who have severe underlying medical conditions like heart or lung disease or diabetes seem to be at higher risk for developing more serious complications from COVID-19 illness. [**__`MORE INFO`__**](https://www.google.com/search?q=COVID-19)")
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    private val totalCases: Int?
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return null
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["cases"] as Int
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return null
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    private val totalRecovered: Int?
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return null
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["recovered"] as Int
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return null
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    private val activeCases: Int?
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return null
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["active"] as Int
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return null
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    private val todayCases: Int?
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return null
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["todayCases"] as Int
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return null
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    private val todayRecovered: Int?
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return null
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["todayRecovered"] as Int
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return null
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    private val criticalCases: Int?
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return null
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["critical"] as Int
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return null
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    private val timeUpdated: Long
        get() {
            val httpclient: HttpClient = DefaultHttpClient()
            return try {
                val httpget = HttpGet("https://disease.sh/v3/covid-19/all/")
                println("executing request " + httpget.uri)

                val responseHandler: ResponseHandler<String> = BasicResponseHandler()
                var responseBody: String? = null
                responseBody = try {
                    httpclient.execute(httpget, responseHandler)
                } catch (e: IOException) {
                    log.warn("Unable to fetch total cases.", e)
                    return 0
                }
                try {
                    ((ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>)["updated"] as Long
                } catch (e: JsonProcessingException) {
                    log.warn("Unable to read total cases response.", e)
                    return 0
                }
            } finally {
                httpclient.connectionManager.shutdown()
            }
        }

    init {
        this.category = Category("Fun")
        name = "covid"
        help = "shows statistics of COVID-19"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}