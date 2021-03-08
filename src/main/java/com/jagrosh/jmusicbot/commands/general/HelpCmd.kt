package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageHistory
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.util.*

class HelpCmd(bot: Bot) : Command() {
    lateinit var commandClient: CommandClient
    private val guiMessageIds: MutableSet<String> = LinkedHashSet() // TODO: 3/3/2021 Don't let this list grow FOREVER


    companion object {
        const val QUIET_MILLIS = 3000
        const val UNICODE_HOME = "🏠"
        const val UNICODE_GENERAL = "ℹ"
        const val UNICODE_MUSIC = "🎵"
        const val UNICODE_FUN = "🎮"
        const val UNICODE_UTILITY = "🔧"
        const val UNICODE_DJ = "💿"
        const val UNICODE_MOD = "🚨"
        const val UNICODE_ADMIN = "⚙"
        const val UNICODE_OWNER = "👑"
        const val UNICODE_CANCEL = "❎"
    }


    val unicodeReactionCharacterByCategoryMap = mapOf(
            null to UNICODE_GENERAL,
            Category("Music") to UNICODE_MUSIC,
            Category("Fun") to UNICODE_FUN,
            Category("Utility") to UNICODE_UTILITY,
            Category("DJ") to UNICODE_DJ,
            Category("Mod") to UNICODE_MOD,
            Category("Admin") to UNICODE_ADMIN,
            Category("Owner") to UNICODE_OWNER,
    )
    val categoryByUnicodeReactionCharacterMap =
            unicodeReactionCharacterByCategoryMap.map { entry -> entry.value to entry.key }.toMap()

    init {
        name = "help"
        help = "shows the menu you're looking at right now"
        guildOnly = false
    }

    override fun execute(event: CommandEvent) {

        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(event))
                .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                .setDescription(
                        getHomeDescription()
                )
                .appendDescription(
                        getReactionKey()
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
        message.addReaction(UNICODE_GENERAL).queue()
        message.addReaction(UNICODE_MUSIC).queue()
        message.addReaction(UNICODE_FUN).queue()
        message.addReaction(UNICODE_UTILITY).queue()
        message.addReaction(UNICODE_DJ).queue()
        message.addReaction(UNICODE_MOD).queue()
        message.addReaction(UNICODE_ADMIN).queue()
        message.addReaction(UNICODE_OWNER).queue()
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

                    event.member?.guild?.selfMember?.color?.rgb
                    val builder = MessageBuilder()
                    val ebuilder = EmbedBuilder()
                            .setColor(getDefaultColor(commandClient, event))
                            .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                            .setDescription(
                                    getHomeDescription()
                            )
                            .appendDescription(
                                    getReactionKey()
                            )
                    // TODO: 3/5/2021 add setHelpFooter to all of these (including the first one higher up)
                    event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                            .queue()
                }
                UNICODE_GENERAL -> {
                    handleReaction(event, "General Commands:")
                }
                UNICODE_MUSIC -> {
                    handleReaction(event, "Music Commands:")
                }
                UNICODE_FUN -> {
                    handleReaction(event, "Fun Commands:")
                }
                UNICODE_UTILITY -> {
                    handleReaction(event, "Utility Commands:")
                }
                UNICODE_DJ -> {
                    handleReaction(event, "DJ Commands:")
                }
                UNICODE_MOD -> {
                    handleReaction(event, "Moderator Commands:")
                }
                UNICODE_ADMIN -> {
                    handleReaction(event, "Admin Commands:")
                }
                UNICODE_OWNER -> {
                    handleReaction(event, "Owner Commands:")
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

    private fun handleReaction(
            event: MessageReactionAddEvent,
            title: String
    ) {
        event.reaction.removeReaction(event.user!!).queue()
        val builder = MessageBuilder()
        val ebuilder = EmbedBuilder()
                .setColor(getDefaultColor(commandClient, event))
                .setAuthor("Siren's Help", null, event.jda.selfUser.avatarUrl)
                .setTitle(title)
        appendCommands(event, ebuilder, event.reactionEmote.name)
        appendReturnHome(ebuilder)

        event.channel.editMessageById(event.messageIdLong, builder.setEmbed(ebuilder.build()).build())
                .queue()
    }

    private fun appendReturnHome(ebuilder: EmbedBuilder) {
        ebuilder.appendDescription("\n\n🏠 - **Return to main menu**")
    }

    private fun getHomeDescription() =
            "Hello! I am **Siren**, a bot that is hosted, owned, and developed by [**Keyboardsheep**](https://keyboardsheep.xyz).\n" +
                    "Want a list of all my commands? Click [here](https://docs.siren.fun/v2/commands)!\n" +
                    "You can visit my website by clicking [here](https://siren.fun)!\n" +
                    "Please [invite](https://siren.fun/invite) me to your server!\n\n"

    private fun getReactionKey() = "ℹ - **General Commands**\n\n" +
            "🎵 - **Music Commands**\n\n" +
            "🎮 - **Fun Commands**\n\n" +
            "🔧 - **Utility Commands**\n\n" +
            "💿 - **DJ Commands**\n\n" +
            "🚨 - **Moderator Commands**\n\n" +
            "⚙ - **Admin Commands**\n\n" +
            "👑 - **Owner Commands**\n\n" +
            "❎ - **Delete this message**"

    private fun appendCommands(event: MessageReactionAddEvent, ebuilder: EmbedBuilder, reactionCharacter: String) {
        val category = categoryByUnicodeReactionCharacterMap[reactionCharacter]
        commandClient.commands
                .filter { it.category?.name == category?.name }
                .forEach { command ->

//                    if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
//                        if (commandCategory != command.category) {
//                            commandCategory = command.category
//                            if (commandCategory?.name != null) {
//                                val message = commandCategory!!.name
//                                ebuilder.appendDescription("\n\n  **").appendDescription(message)
//                                    .appendDescription(":**\n")
//                            }
//                        }
                    ebuilder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                            .appendDescription(if (commandClient.prefix == null) " " else "")
                            .appendDescription(command.name)
                            .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                            .appendDescription(" - ").appendDescription(command.help)
                }
//            }
    }
}
