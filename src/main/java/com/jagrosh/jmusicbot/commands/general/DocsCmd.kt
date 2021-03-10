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
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder

class DocsCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
            .setColor(getDefaultColor(event))
            .setTitle("Click here to view Siren's docs website.", "https://docs.siren.fun")
            .setDescription("If you'd like to view Siren's command list, click [here](https://siren.fun/commands) or run the command **`${event.client.prefix}help`**.")
            .setThumbnail("https://siren.fun/images/company-1-h_kf0s6q4z.png")
            .setFooter("Requested by ${event.author.asTag}", event.author.avatarUrl)
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    init {
        name = "docs"
        help = "gives a link Siren's docs"
        aliases = bot.config.getAliases(name)
        guildOnly = false
    }

}