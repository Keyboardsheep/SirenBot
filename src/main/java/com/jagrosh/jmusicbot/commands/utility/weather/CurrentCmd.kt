package com.jagrosh.jmusicbot.commands.utility.weather

import com.fasterxml.jackson.databind.ObjectMapper
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.db.WeatherLocation
import com.jagrosh.jmusicbot.db.WeatherLocationTable
import com.jagrosh.jmusicbot.db.db
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.jetbrains.exposed.sql.transactions.transaction

class CurrentCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val weatherLocation: WeatherLocation? = transaction(db) {
            val aDiscordUserId = event.author.id
            WeatherLocation.find { WeatherLocationTable.discordUserId eq aDiscordUserId }.firstOrNull()
        }
        if (weatherLocation == null) {
            event.replyError("Unable to find your location") // TODO: 4/17/2021 MAKE IT A NICE EMBED AND ALSO MAKE A LINK TO THE WEATHER DETECT WEBSITE THINGY
            return
        }
        val location = weatherLocation.location
        val weatherResults = fetchWeatherResults(location)
        val currentMap = weatherResults["current"] as Map<*, *>
        val conditionMap = currentMap["condition"] as Map<*, *>
        val locationMap = weatherResults["location"] as Map<*, *>
        val ebuilder = EmbedBuilder()
                .setAuthor("${conditionMap["text"].toString()} in ${locationMap["name"].toString()}", null, "https:${conditionMap["icon"]}") // TODO: 4/8/2021 fix https://0w0.life/sT2N.png & make a website for the weather which will go into the url
                .setDescription("**If this is not your location, click [here](https://www.siren.fun/new-weather/setup#${event.author.id}).**")
                .addField("Temp", "${currentMap["temp_f"].toString()}F/${currentMap["temp_c"].toString()}C", true)
                .setFooter("Detected IP Address: $location • Last updated: ${currentMap["last_updated"].toString()}")
        if (event.channelType.isGuild) {
            event.channel.deleteMessageById(event.message.id).queue()
        }
        event.replyInDm(
                ebuilder.build(),
                { unused: Message? -> if (event.isFromType(ChannelType.TEXT)) event.reactSuccess() }) { t: Throwable? ->
            event.replyWarning(
                    "Weather verification message cannot be sent because you are blocking Direct Messages."
            )
        }
    }

    private fun fetchWeatherResults(userIp: String): Map<*, *> {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val httpget =
                    HttpGet("https://www.siren.fun/api/weather/current?q=$userIp")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody = httpclient.execute(httpget, responseHandler)

            (ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    init {
        name = "weather-current"
        help = "posts an announcement **(BETA)**"
        arguments = "<siren code>"
        aliases = bot.config.getAliases(name)
        guildOnly = false

    }
}