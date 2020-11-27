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
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder

class PollCmd(bot: Bot) : AdminCommand() {
    override fun execute(event: CommandEvent) {
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setTitle("New Poll: When should we kill the Ender Dragon on the new SMP?")
                .setDescription("Some people want to fight the dragon tomorrow, others want to wait. Please only fill in the poll below if you have played on the new SMP.\n" +
                        "\n**Vote options:\n" +
                        ":one: I want to fight the dragon tomorrow.\n" +
                        ":two: I want to wait a week or two before the fight the dragon.\n" +
                        ":three: I want to wait a long time before the fight the dragon.**\n")
        event.channel.deleteMessageById(event.message.id).queue()
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    init {
        name = "poll"
        help = "posts an announcement **(BETA)**"
        arguments = "<message>"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}