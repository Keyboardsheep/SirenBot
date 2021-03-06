package com.jagrosh.jmusicbot.commands.admin

import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.AdminCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import java.awt.Color
import java.util.*

class AnnounceEmbedCmd(bot: Bot) : AdminCommand() {
    override fun execute(event: CommandEvent) {
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setTitle(event.args)
        event.channel.deleteMessageById(event.message.id).queue()
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
    }

    init {
        name = "announce"
        help = "posts an announcement **(BETA)**"
        arguments = "<message>"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}

class PruneCmd(bot: Bot) : AdminCommand() {
    private var lastExecutionMillis: Long = 0
    override fun execute(event: CommandEvent) {
        val now = System.currentTimeMillis()
        if (now > lastExecutionMillis + QUIET_MILLIS) {
            if (event.args.length < 1) {
                // Usage
                val usage = EmbedBuilder()
                usage.setColor(0xff3923)
                usage.setTitle("Specify amount to delete")
                usage.setDescription("Usage: `${event.client.prefix}prune [# of messages]`")
                event.channel.sendMessage(usage.build()).queue()
            } else {
                try {
                    val messages = event.channel.history.retrievePast(event.args.toInt()).complete()
                    event.channel.purgeMessages(messages)

                    // Success
                    val success = EmbedBuilder()
                    success.setColor(0x22ff2a)
                    success.setTitle(":smiley_cat: Successfully deleted " + event.args + " messages.")
                    val messageEmbed = success.build()
                    event.channel.sendMessage(messageEmbed).queue { message: Message -> deleteAfterDelay(message) }
                    lastExecutionMillis = now
                } catch (e: IllegalArgumentException) {
                    if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                        // Too many messages
                        val error = EmbedBuilder()
                        error.setColor(0xff3923)
                        error.setTitle(":scream_cat: Too many messages selected")
                        error.setDescription("**You can only delete a max of 100 messages!**")
                        event.channel.sendMessage(error.build()).queue()
                    } else {
                        // Messages too old
                        // TODO Add other error
                        val error = EmbedBuilder()
                        error.setColor(0xff3923)
                        error.setTitle(":scream_cat: Selected messages are older than 2 weeks")
                        error.setDescription("Messages older than 2 weeks cannot be deleted.")
                        event.channel.sendMessage(error.build()).queue()
                    }
                }
            }
        } else {
            val builder = MessageBuilder()
            val ebuilder = EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("**Please slow down between commands!**")
                    .setDescription("Please wait ** " + ((QUIET_MILLIS - (now - lastExecutionMillis)) / 1000 + 1) + " ** more seconds.")
            event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build()).queue()
        }
    }

    private fun deleteAfterDelay(message: Message) {
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                message.delete().queue()
            }
        }
        Timer("MessageDeleteTimer").schedule(task, 5000)
    }

    companion object {
        const val QUIET_MILLIS = 5000
    }

    init {
        name = "prune"
        help = "deletes messages in bulk"
        arguments = "<number of messages>"
        aliases = bot.config.getAliases(name)
        guildOnly = true
    }
}