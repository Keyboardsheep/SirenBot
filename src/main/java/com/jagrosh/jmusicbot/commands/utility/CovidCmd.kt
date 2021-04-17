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
import java.text.NumberFormat
import java.util.*

@Suppress("DEPRECATION")
class CovidCmd(bot: Bot) : UtilityCommand() {
    var log: Logger = LoggerFactory.getLogger("CovidCmd")
    override fun execute(event: CommandEvent) {
        val covidStats = fetchCovidStats()
        val timeUpdated: Long = covidStats["updated"].toString().toLong()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val date = Date(timeUpdated)
        sdf.format(date)
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
            .setColor(getDefaultColor(event))
            .setTitle("**:cat: Latest Global COVID-19 Statistics:**")
            .addField("Total Cases", (NumberFormat.getNumberInstance(Locale.US).format(covidStats["cases"])), true)
            .addField(
                "Total Recovered",
                (NumberFormat.getNumberInstance(Locale.US).format(covidStats["recovered"])),
                true
            )
            .addField("Active Cases", (NumberFormat.getNumberInstance(Locale.US).format(covidStats["active"])), true)
            .addField("Cases Today", (NumberFormat.getNumberInstance(Locale.US).format(covidStats["todayCases"])), true)
            .addField(
                "Recovered Today",
                (NumberFormat.getNumberInstance(Locale.US).format(covidStats["todayRecovered"])),
                true
            )
            .addField(
                "Critical Cases",
                (NumberFormat.getNumberInstance(Locale.US).format(covidStats["critical"])),
                true
            )
            .setFooter("Last Updated: $date")
            .setDescription("COVID-19 is caused by a coronavirus called SARS-CoV-2. Older adults and people who have severe underlying medical conditions like heart or lung disease or diabetes seem to be at higher risk for developing more serious complications from COVID-19 illness. [**__`MORE INFO`__**](https://www.google.com/search?q=COVID-19)")
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    private fun fetchCovidStats(): Map<*, *> {
        val httpclient: HttpClient = DefaultHttpClient()
        return try {
            val httpget =
                    HttpGet("https://disease.sh/v3/covid-19/all/")
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
        name = "covid"
        help = "shows statistics of COVID-19"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }
}