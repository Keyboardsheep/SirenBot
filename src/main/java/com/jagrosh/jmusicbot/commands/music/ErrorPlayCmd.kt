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
package com.jagrosh.jmusicbot.commands.music

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.User

class ErrorPlayCmd(bot: Bot) : Command() {
    override fun execute(event: CommandEvent) {
        val builder = MessageBuilder()
        val owner: User? = event.jda.getUserById(event.client.ownerId)
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setTitle("Temporary downtime")
                .setDescription("Unfortunately, Siren has been banned by YouTube meaning that most music commands are disabled. I am working as quickly as possible to fix this issue.")
                .setFooter("For additional help, contact ${owner?.asTag} or join https://discord.gg/Eyetd8J.")
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    init {
        name = "play"
        help = "COMMAND DISABLED"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}