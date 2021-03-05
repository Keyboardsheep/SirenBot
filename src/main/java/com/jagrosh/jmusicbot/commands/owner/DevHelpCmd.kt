package com.jagrosh.jmusicbot.commands.owner

import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.OwnerCommand
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.awt.Color
import java.util.*

class DevHelpCmd(bot: Bot) : OwnerCommand() {
    lateinit var commandClient: CommandClient
    private val guiMessageIds: MutableSet<String> = LinkedHashSet() // TODO: 3/3/2021 Don't let this list grow FOREVER


    companion object {
        const val QUIET_MILLIS = 3000
        const val UNICODE_HOME = "🏠"
        const val UNICODE_MUSIC = "🎵"
        const val UNICODE_FUN = "🎮"
        const val UNICODE_UTILITY = "🔧"
        const val UNICODE_DJ = "💿"
        const val UNICODE_MOD = "🚨"
        const val UNICODE_ADMIN = "⚙"
        const val UNICODE_CANCEL = "❎"
    }

    init {
        name = "devhelp"
        help = "shows the menu you're looking at right now"
        guildOnly = false
    }

    override fun execute(event: CommandEvent) {

        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
            .setColor(getDefaultColor(event))
            .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
            .setDescription(
                "Hello! I am **Siren**, a bot that is hosted and owned by [**Keyboardsheep**](https://keyboardsheep.xyz).\n" +
                        "Want a list of all my commands? Click [here](https://docs.siren.fun/v2/commands)!\n" +
                        "You can visit my website by clicking [here](https://siren.fun)!\n" +
                        "Please [invite](https://siren.fun/invite) me to your server!\n\n"
            )
            .appendDescription(
                "🎵 - **Music Commands**\n\n" +
                        "🎮 - **Fun Commands**\n\n" +
                        "🔧 - **Utility Commands**\n\n" +
                        "💿 - **DJ Commands**\n\n" +
                        "🚨 - **Moderator Commands**\n\n" +
                        "⚙ - **Admin Commands**\n\n" +
                        "❎ - **Delete this message**"
            )
        event.channel.sendMessage(builder.setEmbed(ebuilder.build()).build())
            .queue { message: Message -> handleQueuedHelpMessage(message) }

    }

    private fun handleQueuedHelpMessage(message: Message) {
        guiMessageIds.add(message.id)

        addHelpReactions(message)
    }

    private fun addHelpReactions(message: Message) {
        message.addReaction(UNICODE_HOME).queue()
        message.addReaction(UNICODE_MUSIC).queue()
        message.addReaction(UNICODE_FUN).queue()
        message.addReaction(UNICODE_UTILITY).queue()
        message.addReaction(UNICODE_DJ).queue()
        message.addReaction(UNICODE_MOD).queue()
        message.addReaction(UNICODE_ADMIN).queue()
        message.addReaction(UNICODE_CANCEL).queue()
    }

    private fun setHelpFooter(ownerBuilder: EmbedBuilder, jda: JDA) {
        val owner: User? = jda.getUserById(commandClient.ownerId)

        val ownerSnippet = if (owner != null) "contact ${owner.asTag} or" else ""

        ownerBuilder.setFooter(
            "For additional help, $ownerSnippet join https://discord.gg/Eyetd8J.",
            owner?.avatarUrl
                ?: "https://cdn.discordapp.com/icons/710128446814289920/a_59f64ff98b72a950146affdc7d4e0466.gif" // PYGMY POOOOOOOOOF IMAGE
        )
    }

    fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (guiMessageIds.contains(event.messageId)) {
            if (event.user != event.jda.selfUser) when (event.reactionEmote.name) {
                UNICODE_HOME -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setDescription(
                            "Hello! I am **Siren**, a bot that is hosted and owned by [**Keyboardsheep**](https://keyboardsheep.xyz).\n" +
                                    "Want my commands in web form? Click [here](https://docs.siren.fun/v2/commands)!\n" +
                                    "You can visit my website by clicking [here](https://siren.fun)!\n" +
                                    "Please [invite](https://siren.fun/invite) me to your server!\n\n"
                        )
                        .appendDescription(
                            "🎵 - **Music Commands**\n\n" +
                                    "🎮 - **Fun Commands**\n\n" +
                                    "🔧 - **Utility Commands**\n\n" +
                                    "💿 - **DJ Commands**\n\n" +
                                    "🚨 - **Moderator Commands**\n\n" +
                                    "⚙ - **Admin Commands**\n\n" +
                                    "❎ - **Delete this message**"
                        )
                    // TODO: 3/5/2021 add setHelpFooter to all of these (including the first one higher up)
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_MUSIC -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setTitle("Music Commands:")
                        .setDescription("🏠 - **Return to main menu**")
//                        .setDescription() TODO make this show current music commands
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_FUN -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setTitle("Fun Commands:")
                        .setDescription("🏠 - **Return to main menu**")
//                        .setDescription() TODO make this show current fun commands
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_UTILITY -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setTitle("Utility Commands:")
                        .setDescription("🏠 - **Return to main menu**")
//                        .setDescription() TODO make this show current utility commands
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_DJ -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setTitle("DJ Commands:")
                        .setDescription("🏠 - **Return to main menu**")
//                        .setDescription() TODO make this show current dj commands
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_MOD -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setTitle("Moderator Commands:")
                        .setDescription("🏠 - **Return to main menu**")
//                        .setDescription() TODO make this show current mod commands
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_ADMIN -> {
                    event.reaction.removeReaction(event.user!!).queue()

                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                        .setColor(Color.black.rgb) // TODO: 3/3/2021 use getDefaultColor(event)
                        .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                        .setTitle("Admin Commands:")
                        .setDescription("🏠 - **Return to main menu**")
//                        .setDescription() TODO make this show current admin commands
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                        .queue()
                }
                UNICODE_CANCEL -> event.channel.getHistoryBefore(event.messageId, 1)
                    .queue { messageHistory: MessageHistory ->
                        for (message in messageHistory.retrievedHistory) {
                            event.channel.deleteMessageById(message.id).queue()
                        }
                        event.channel.deleteMessageById(event.messageId).queue()
                    }
            }
        }
    }
}
