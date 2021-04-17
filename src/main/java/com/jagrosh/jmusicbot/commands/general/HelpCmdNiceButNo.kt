package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import com.jagrosh.jmusicbot.commands.OwnerCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

class HelpCmdNiceButNo(bot: Bot) : Command() {
    lateinit var commandClient: CommandClient

    init {
        name = "help"
        help = "shows the menu you're looking at right now"
        guildOnly = false
    }

    override fun execute(event: CommandEvent) {

        val isOwner = event.member.id == event.client.ownerId

        var firstEmbed = true
        commandClient.commands
                .filter { it !is OwnerCommand }
                .groupBy { it.category?.name }
                .forEach { name, commands ->
                    val builder = EmbedBuilder()
                    if (firstEmbed) {
                        builder.setTitle("**" + event.selfUser.name + "** Commands:")
                        firstEmbed = false
                    }
                    var commandCategory: Category? = null

                    commands.forEach { command ->
                        if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                            if (commandCategory != command.category) {
                                commandCategory = command.category
                                if (commandCategory?.name != null) {
                                    val message = commandCategory!!.name
                                    builder.appendDescription("\n\n  **").appendDescription(message)
                                            .appendDescription(":**\n")
                                }
                            }
                            builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                    .appendDescription(if (commandClient.prefix == null) " " else "")
                                    .appendDescription(command.name)
                                    .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                    .appendDescription(" - ").appendDescription(command.help)
                        }
                    }
                    if (name == "Admin" && !isOwner) {
                        setHelpFooter(builder, event.jda)
                    }
                    event.replyInDm(
                            builder.build(),
                            { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                        event!!.replyWarning(
                                "Help message cannot be sent because you are blocking Direct Messages."
                        )
                    }
                }

        if (isOwner) {

            val ownerBuilder = EmbedBuilder()
            var commandCategory: Category? = null

            commandClient.commands
                    .filter { it.category?.name == "Owner" }
                    .forEach { command ->
                        if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                            if (commandCategory != command.category) {
                                commandCategory = command.category
                                val message = commandCategory.let { it?.name } ?: "No Category"
                                ownerBuilder.appendDescription("\n\n  **").appendDescription(message)
                                        .appendDescription(":**\n")
                            }
                            ownerBuilder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                    .appendDescription(if (commandClient.prefix == null) " " else "")
                                    .appendDescription(command.name)
                                    .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                    .appendDescription(" - ").appendDescription(command.help)
                        }
                    }
//            if (owner != null) {
//                setHelpFooter(ownerBuilder, event.jda)
//            }
            event.replyInDm(
                    ownerBuilder.build(),
                    { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                event!!.replyWarning(
                        "Help message cannot be sent because you are blocking Direct Messages."
                )
            }
        }

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
}