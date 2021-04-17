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

class KickCmd(bot: Bot) : ModCommand() {
    var log = LoggerFactory.getLogger("KickCmd")
    override fun execute(event: CommandEvent) {
        val args = event.args.split("\\s".toRegex()).toTypedArray()
        val rawUserId = args[0]
        var reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "))
        if (reason.isEmpty()) {
            reason = "Reason not specified."
        }
        if (rawUserId.isEmpty()) {
            sendAndQueueEmbed(event, Color.red, ":scream_cat: Please mention a user!", "**Usage:** ${event.client.prefix}kick <username> [reason]")
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
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to kick!**", "**Reason:**\nUnknown User")
                log.info("Unknown User ($rawUserId)")
                return
            }
            if (!event.member.canInteract(event.guild.getMember(user)!!)) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to kick!**", "**Reason:**\nYou cannot kick $rawUserId!")
                log.info(event.member.effectiveName + " cannot kick " + rawUserId)
                return
            }
            if (!event.selfMember.canInteract(event.guild.getMember(user)!!)) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to kick!**", "**Reason:**\nI cannot kick $rawUserId!")
                log.info("I cannot kick $rawUserId")
                return
            }
            val finalUser: User = user
            val finalReason = reason
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription(":cat: **Successfully kicked $rawUserId!\nReason: `$reason`**")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build())
                    .queue({ message: Message? ->
                        val kickMessage = EmbedBuilder()
                                .setColor(Color.red)
                                .setDescription("""
    You were kicked by ${event.member.effectiveName}!
    **Reason:** `$finalReason`
    """.trimIndent())
                                .setTitle(":scream_cat: You have been kicked from " + event.guild.name + "!").build()
                        finalUser.openPrivateChannel()
                                .flatMap { channel: PrivateChannel -> channel.sendMessage(kickMessage) }
                                .queue()
                        kickAfterDelay(event, finalUser)
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

    private fun kickAfterDelay(event: CommandEvent, user: User) {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                event.guild.kick(user.id).queue()
            }
        }
        Timer("KickTimer").schedule(task, 500)
    }

    init {
        name = "kick"
        help = "kicks a user from your guild"
        arguments = "<username> [reason]"
        aliases = bot.config.getAliases(name)
        botPermissions = arrayOf(Permission.KICK_MEMBERS)
    }
}

class WarnCmd(bot: Bot) : ModCommand() {
    var log = LoggerFactory.getLogger("WarnCmd")
    override fun execute(event: CommandEvent) {
        val args = event.args.split("\\s".toRegex()).toTypedArray()
        val rawUserId = args[0]
        var reason = Arrays.stream(args).skip(1).collect(Collectors.joining(" "))
        if (reason.isEmpty()) {
            reason = "Reason not specified."
        }
        if (rawUserId.isEmpty()) {
            sendAndQueueEmbed(event, Color.red, ":scream_cat: Please mention a user!", "**Usage:** ${event.client.prefix}warn <username> [reason]")
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
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to warn!**", "**Reason:**\nUnknown User")
                log.info("Unknown User ($rawUserId)")
                return
            }
            if (!event.member.canInteract(event.guild.getMember(user)!!)) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to warn!**", "**Reason:**\nYou cannot warn $rawUserId!")
                log.info(event.member.effectiveName + " cannot warn " + rawUserId)
                return
            }
            if (!event.selfMember.canInteract(event.guild.getMember(user)!!)) {
                sendAndQueueEmbed(event, Color.red, ":scream_cat: **Failed to warn!**", "**Reason:**\nI cannot warn $rawUserId!")
                log.info("I cannot warn $rawUserId")
                return
            }
            val finalUser: User = user
            val finalReason = reason
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.green)
                    .setDescription(":cat: **Successfully warned $rawUserId!\nReason: `$reason`**")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build())
                    .queue({ message: Message? ->
                        val warnMessage = EmbedBuilder()
                                .setColor(Color.red)
                                .setDescription("""
    You were warned by ${event.member.effectiveName}!
    **Reason:** `$finalReason`
    """.trimIndent())
                                .setTitle(":scream_cat: You have been warned in " + event.guild.name + "!").build()
                        finalUser.openPrivateChannel()
                                .flatMap { channel: PrivateChannel -> channel.sendMessage(warnMessage) }
                                .queue()
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

    init {
        name = "warn"
        help = "warns a user in your guild"
        arguments = "<username> [reason]"
        aliases = bot.config.getAliases(name)
        botPermissions = arrayOf(Permission.KICK_MEMBERS)
    }
}