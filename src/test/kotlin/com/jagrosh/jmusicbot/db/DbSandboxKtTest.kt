package com.jagrosh.jmusicbot.db

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test

class DbSandboxKtTest {

    @Test
    fun `create db`() {
        createDb()
    }

    @Test
    fun `create or update location`() {
        transaction(db) {
            val aDiscordUserId = "foo2"
            val weatherLocation: WeatherLocation = WeatherLocation.find { WeatherLocationTable.discordUserId eq aDiscordUserId }.firstOrNull()
                    ?: WeatherLocation.new {
                        discordUserId = aDiscordUserId
                    }


            weatherLocation.location = "5541" + java.util.Random().nextInt(10)

        }
    }

    @Test
    fun `list all locations`() {
        transaction(db) {

            WeatherLocation.all().forEach {
                println("it = ${it}")
            }
        }
    }
}