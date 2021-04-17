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
                .setTitle("**OGMS Discord Server Rules & Guidelines**")
                .setDescription("**Welcome to the OGMS Discord Server! Please follow all the rules listed below.**\n" +
                        "**General Rules**\n" +
                        "- Respect all users in the server\n" +
                        "- Follow and respect by staff instructions\n" +
                        "- Staff decision is final, Do not argue with the staff\n" +
                        "- Do not impersonate staff or users of the server\n" +
                        "- Punishment Evading (mutes & bans) will result in a permanent ban\n" +
                        "- Inappropriate names & pictures are not acceptable.\n" +
                        "**Text Chat Rules**\n" +
                        "- Follow all staff Direction\n" +
                        "- Do not spam messages in chat\n" +
                        "- Advertising any external links, servers or groups is not allowed\n" +
                        "- Respect all users of the server\n" +
                        "- Do not use channels for unintended purposes\n" +
                        "- Do not send any links to sites that are unsafe or inappropriate\n" +
                        "- Discrimination of any sort is not tolerated\n" +
                        "- Do not discuss politics, exploits, hacks or religion here.\n" +
                        "- Name calling, threats or harassment will not be tolerated\n" +
                        "- NSFW content and/or offensive posts are not permitted\n" +
                        "- No soliciting (Selling of products or services)\n" +
                        "- Threats of any matter to the server, staff or any user is strictly moderated and will not be tolerated regardless of the intended manner\n" +
                        "**Voice Chat Rules**\n" +
                        "In addition to these rules, all text channel rules apply\n" +
                        "- Do not excessively channel switch\n" +
                        "- No soundboards, voice changers, or playing loud music\n" +
                        "- Do not record voice channels without consent\n" +
                        "Failing to follow these rules will result in punishment varying on offence\n" +
                        "In addition to these rules, all of our members are expected to follow and respect the Discord Terms Of Service (TOS) listed at the link below. Any punishments issued by the staff team can only be appealed once.\n" +
                        "\n\n" +
                        "Discord TOS: https://discord.com/terms")
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