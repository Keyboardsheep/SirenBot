/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
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
package com.jagrosh.jmusicbot.commands.mod

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.ModCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.User
import org.slf4j.LoggerFactory
import java.awt.Color
import java.util.*
import java.util.stream.Collectors

class BanCmd(bot: Bot) : ModCommand() {
    var log = LoggerFactory.getLogger("BanCmd")
    override fun execute(event: CommandEvent) {
        val args = event.args.split("\\s".toRegex()).toTypedArray()
        val rawUserId = args[0]
        var reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "))
        if (reason.isEmpty()) {
            reason = "Reason not specified."
        }
        if (rawUserId.isEmpty()) {
            sendAndQueueEmbed(event, Color.red, ":scream_cat: Please mention a user!", "**Usage:** siren ban <username> [reason]")
        } else {
            val userId = rawUserId.replace("\\D+".toRegex(), "")
            var user: User?
            try {
                user = event.jda.getUserById(userId)
            } catch (e: Exception) {
                user = null
                log.info("Unknown User ($rawUserId)", e)
            }
            if (user == null) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to ban!**", "**Reason:**\nUnknown User")
                log.info("Unknown User ($rawUserId)")
                return
            }
            if (!event.member.canInteract(event.guild.getMember(user)!!)) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to ban!**", "**Reason:**\nYou cannot ban $rawUserId!")
                log.info(event.member.effectiveName + " cannot ban " + rawUserId)
                return
            }
            if (!event.selfMember.canInteract(event.guild.getMember(user)!!)) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to ban!**", "**Reason:**\nI cannot ban $rawUserId!")
                log.info("I cannot ban $rawUserId")
                return
            }
            val finalUser: User = user
            val finalReason = reason
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription(":cat: **Successfully banned $rawUserId!\nReason: `$reason`**")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build())
                    .queue({ message: Message? ->
                        val banMessage = EmbedBuilder()
                                .setColor(Color.red)
                                .setDescription("""
    You were banned by ${event.member.effectiveName}!
    **Reason:** `$finalReason`
    """.trimIndent())
                                .setTitle(":scream_cat: You have been banned from " + event.guild.name + "!").build()
                        finalUser.openPrivateChannel()
                                .flatMap { channel: PrivateChannel -> channel.sendMessage(banMessage) }
                                .queue()
                        banAfterDelay(event, finalUser)
                    }) { throwable: Throwable? -> }
        }
    }

    private fun sendAndQueueEmbed(event: CommandEvent, color: Color, title: String, description: String) {
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(color)
                .setTitle(title)
                .setDescription(description)
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    private fun banAfterDelay(event: CommandEvent, user: User) {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                event.guild.ban(user.id, 0).queue()
            }
        }
        Timer("BanTimer").schedule(task, 500)
    }

    init {
        name = "ban"
        help = "bans a user from your guild"
        arguments = "<username> [reason]"
        aliases = bot.config.getAliases(name)
        botPermissions = arrayOf(Permission.BAN_MEMBERS)
    }
}