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
import java.awt.Color

@Suppress("DEPRECATION")
class IpLookupCmd(bot: Bot) : UtilityCommand() {
    var log: Logger = LoggerFactory.getLogger("IpLookupCmd")
    override fun execute(event: CommandEvent) {
        val argsFormatted = event.args
        val ipResults = fetchIpResults(argsFormatted)
        var zipCode = try {
            ipResults["zip"]
        } catch (e: Exception) {
            "N/A"
        }
        if (zipCode.toString().isBlank()) {
            zipCode = "N/A"
        }
        if (event.args.isNullOrEmpty()) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(":scream_cat: Please specify an IP!")
                .setDescription("**Usage:** ${event.client.prefix}iplookup <ip address>")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        } else {
            try {
                val builder = MessageBuilder()
                val ebuilder = EmbedBuilder()
                    .setColor(getDefaultColor(event))
                    .setTitle("**IP Lookup Results:** (${ipResults["query"]})")
                    .addField("Continent", ipResults["continent"] as String?, true)
                    .addField("Country", ipResults["country"] as String?, true)
                    .addField("State/Region", ipResults["regionName"] as String?, true)
                    .addField("City", ipResults["city"] as String?, true)
                    .addField("Zip Code", zipCode as String?, true)
                    .addField("Timezone", ipResults["timezone"] as String?, true)
                    .addField(
                        "Latitude",
                        "[__${ipResults["lat"]?.toString()}__](https://www.google.com/maps/search/${ipResults["lat"]?.toString()},+${ipResults["lon"]?.toString()})",
                        true
                    )
                    .addField(
                        "Longitude",
                        "[__${ipResults["lon"]?.toString()}__](https://www.google.com/maps/search/${ipResults["lat"]?.toString()},+${ipResults["lon"]?.toString()})",
                        true
                    )
                    .addField("ISP", ipResults["isp"] as String?, true)
                    .addField("Mobile", ipResults["mobile"]?.toString()!!.capitalize(), true)
                    .addField("Proxy/VPN", ipResults["proxy"]?.toString()!!.capitalize(), true)
                    .addField("Datacenter", ipResults["hosting"]?.toString()!!.capitalize(), true)
                    .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
                    .setImage("https://www.siren.fun/api/iplookup_image?lat=${ipResults["lat"]?.toString()}&lon=${ipResults["lon"]?.toString()}")
                event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
            } catch (e: Exception) {
                val builder = MessageBuilder()
                val ebuilder = EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle(":scream_cat: Oops, an error occurred!")
                    .setDescription("**Possible causes:**\n• The IP you gave was invalid. Check that `$argsFormatted` is a valid IP.\n\n• There was an error with Siren. If your IP is valid, please join the [support server](https://discord.gg/Eyetd8J)!")
                event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
            }
        }
    }

    private fun fetchIpResults(argsFormatted: String): Map<*, *> {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val httpget =
                HttpGet("http://ip-api.com/json/$argsFormatted?fields=18080761")
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
        name = "iplookup"
        help = "shows some info about an ip address"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}