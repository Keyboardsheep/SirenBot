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
package com.jagrosh.jmusicbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.AdminCommand
import com.jagrosh.jmusicbot.settings.Settings
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import org.slf4j.LoggerFactory
import java.awt.Color
import java.lang.String

class SetEmbedColorCmd(bot: Bot) : AdminCommand() {
    var log = LoggerFactory.getLogger("CatCmd")

    init {
        name = "setembedcolor"
        help = "changes Siren's default embed color"
        arguments = "<hex code>"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }

    override fun execute(event: CommandEvent) {
        val args = event.args.split("\\s".toRegex()).toTypedArray()
        val settings = event.client.getSettingsFor<Settings>(event.guild)
        if (args[0].isNullOrBlank()) {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle(":scream_cat: Please include a hex code!", "https://www.google.com/search?q=color+picker")
                    .setDescription("**Usage:** `${event.client.prefix}setembedcolor <hex code>`\n**Example:** `${event.client.prefix}setembedcolor 00a6a0`")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        } else {
            if (args[0] != "role") {
                val hexColor = args[0].replace("#", "")
                try {
                    val decimalColor = Integer.parseInt(hexColor, 16)
                    settings.embedColor = decimalColor
                    val builder = MessageBuilder()
                    val cleanHexColor = String.format("%06X", 0xFFFFFF and decimalColor)
                    val ebuilder = EmbedBuilder()
                            .setColor(getDefaultColor(event))
                            .setTitle(":cat: Successfully changed embed color!")
                            .setDescription("You have changed this guild's embed color to `#$cleanHexColor`.\n\n*If you want a different color, click [__here__](https://www.google.com/search?q=color+picker) to select your new hex.*")
                            .setThumbnail("https://singlecolorimage.com/get/$cleanHexColor/400x400")
                    event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
                } catch (e: NumberFormatException) {
                    log.info("Cannot parse hex ($hexColor)")
                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle(":scream_cat: Cannot parse hex code!")
                            .setDescription("I'm sorry, I don't understand the hex code `$hexColor`.\n*If you don't know what hex your color is, click [__here__](https://www.google.com/search?q=color+picker) to get your hex.*")
                    event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
                }
                val decimalColor = Integer.parseInt(hexColor, 16)
                event.client.getSettingsFor<Settings>(event.guild).embedColor = decimalColor as Int?
            } else {
                settings.embedColor = null
                val builder = MessageBuilder()
                val ebuilder = EmbedBuilder()
                        .setColor(getDefaultColor(event))
                        .setTitle(":cat: Successfully changed embed color!")
                        .setDescription("You have changed this guild's embed color to the color of Siren's role. If his role doesn't have a color, it will default to black.")
                event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
            }
        }


    }


}

fun getDefaultColor(event: CommandEvent): Int {
    val settings = event.client.getSettingsFor<Settings>(event.guild)
    return getDefaultColor(settings, event.guild)
}

fun getDefaultColor(settings: Settings?, guild: Guild): Int {
    return if (settings?.embedColor == null) {
        guild.selfMember.color?.rgb ?: Color.black.rgb
    } else {
        settings.embedColor
    }
}