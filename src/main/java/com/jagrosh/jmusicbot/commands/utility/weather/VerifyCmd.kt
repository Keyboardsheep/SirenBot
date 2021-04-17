package com.jagrosh.jmusicbot.commands.utility.weather

import com.fasterxml.jackson.databind.ObjectMapper
import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.db.WeatherLocation
import com.jagrosh.jmusicbot.db.WeatherLocationTable
import com.jagrosh.jmusicbot.db.db
import com.jagrosh.jmusicbot.utils.AESUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import org.apache.http.client.HttpClient
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.BufferedReader
import java.io.File

class VerifyCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val args = event.args.split("\\s".toRegex()).toTypedArray()
        val sirenCode = args[0].replace("SIRENCODE-", "")
        val bufferedReader: BufferedReader = File("key.txt").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        val decryptedString = AESUtil.decrypt(sirenCode, inputString)
        val webUserLocation = decryptedString.replace(event.author.id, "").replace("--SIRENCODE--", "")
        val webUserId = decryptedString.replace(webUserLocation, "").replace("--SIRENCODE--", "")
        val ipResults = fetchIpResults(webUserLocation)
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setTitle("**Weather Location Detected**")
                .setDescription("**If this is not your location, click [here](https://www.siren.fun/new-weather/setup#${event.author.id}).**")
                .addField("City", ipResults["city"].toString(), true)
                .addField("State/Region", ipResults["regionName"].toString(), true)
                .addField("Country", ipResults["country"].toString(), true)
                .setFooter("Detected IP Address: $webUserLocation")
        if (webUserId == event.author.id) {
            if (event.channelType.isGuild) {
                event.reactSuccess()
                event.reply("${event.author.asMention}, please check your DMs.")
            }
            event.replyInDm(
                    ebuilder.build(),
                    { unused: Message? -> if (event.isFromType(ChannelType.TEXT)) event.reactSuccess() }) { t: Throwable? ->
                event.replyWarning(
                        "Weather verification message cannot be sent because you are blocking Direct Messages."
                )
            }
            upsertWeatherLocation(webUserId, webUserLocation)
        } else {
            val ebuilder = EmbedBuilder()
                    .setTitle("**Your Siren Code doesn't match with your Discord account.**")
                    .setDescription("**Please retry by clicking [here](https://siren.fun/weather). If this is repeatedly happening, join our [support server](https://discord.gg/Eyetd8J).**")
            event.reactError()
            event.reply(ebuilder.build())
        }
    }

    private fun upsertWeatherLocation(webUserId: String, webUserLocation: String) {
        transaction(db) {
            val weatherLocation: WeatherLocation = WeatherLocation.find { WeatherLocationTable.discordUserId eq webUserId }.firstOrNull()
                    ?: WeatherLocation.new {
                        discordUserId = webUserId
                    }
            weatherLocation.location = webUserLocation
        }
    }

    private fun fetchIpResults(userIp: String): Map<*, *> {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val httpget =
                    HttpGet("http://ip-api.com/json/$userIp?fields=18080761")
            println("executing request " + httpget.uri)

            val responseHandler: ResponseHandler<String> = BasicResponseHandler()
            var responseBody = httpclient.execute(httpget, responseHandler)

            (ObjectMapper().readValue("[$responseBody]", MutableList::class.java) as List<*>)[0] as Map<*, *>
        } finally {
            httpclient.connectionManager.shutdown()
        }
    }

    init {
        name = "weather-verify"
        help = "posts an announcement **(BETA)**"
        arguments = "<siren code>"
        aliases = bot.config.getAliases(name)
        guildOnly = false

    }
}