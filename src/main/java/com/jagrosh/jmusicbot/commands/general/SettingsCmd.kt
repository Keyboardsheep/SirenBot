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
package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import com.jagrosh.jmusicbot.settings.Settings
import com.jagrosh.jmusicbot.utils.FormatUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Guild
import java.lang.String

/**
 *
 * @author John Grosh <john.a.grosh></john.a.grosh>@gmail.com>
 */
class SettingsCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val s = event.client.getSettingsFor<Settings>(event.guild)
        val builder = MessageBuilder()
                .append(EMOJI + " **")
                .append(FormatUtil.filter(event.selfUser.name))
                .append("** settings:")
        val tchan = s.getTextChannel(event.guild)
        val vchan = s.getVoiceChannel(event.guild)
        val role = s.getRole(event.guild)
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setDescription("""
    Text Channel: ${if (tchan == null) "Any" else "**#" + tchan.name + "**"}
    Voice Channel: ${if (vchan == null) "Any" else "**" + vchan.name + "**"}
    DJ Role: ${if (role == null) "None" else "**" + role.name + "**"}
    Custom Prefix: ${if (s.prefix == null) "None" else "`" + s.prefix + "`"}
    Repeat Mode: **${if (s.repeatMode) "On" else "Off"}**
    Default Playlist: ${if (s.defaultPlaylist == null) "None" else "**" + s.defaultPlaylist + "**"}
    Embed Color: ${if (s.embedColor == null) "**Role Color**" else "**${String.format("#%06X", 0xFFFFFF and s.embedColor)}**"}
    """.trimIndent() // TODO replace "Custom Hex" with the actual custom hex
                )
                .setFooter(event.jda.guilds.size.toString() + " servers | "
                        + event.jda.guilds.stream().filter { g: Guild -> g.selfMember.voiceState!!.inVoiceChannel() }.count()
                        + " audio connections", null)
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    companion object {
        private const val EMOJI = "\uD83C\uDFA7" // 🎧
    }

    init {
        name = "settings"
        help = "shows the bots settings"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}