package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandClient
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jmusicbot.Bot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

class HelpCmdNo(bot: Bot) : Command() {
    lateinit var commandClient: CommandClient

    init {
        name = "help"
        help = "shows the menu you're looking at right now"
        guildOnly = false
    }

    override fun execute(event: CommandEvent) {
        if (event.member.id == event.client.ownerId) {
            if (event.args.isEmpty()) {
                val builder = EmbedBuilder()
                var category: Category? = null
                builder.setTitle("**" + event.selfUser.name + "** Commands:")
                commandClient.commands
                    .filter { it.category?.name == null || it.category?.name == "Music" || it.category?.name == "Fun" || it.category?.name == "DJ" || it.category?.name == "Mod" }
                    .forEach { command ->
                        if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                            if (category != command.category) {
                                category = command.category
                                val message = category.let { it?.name } ?: "No Category"
                                builder.appendDescription("\n\n  **").appendDescription(message)
                                    .appendDescription(":**\n")
                            }
                            builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                .appendDescription(if (commandClient.prefix == null) " " else "")
                                .appendDescription(command.name)
                                .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                .appendDescription(" - ").appendDescription(command.help)
                        }
                    }
                val owner: User? = event.jda.getUserById(commandClient.ownerId)
                event.replyInDm(
                    builder.build(),
                    { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                    event!!.replyWarning(
                        "Help message cannot be sent because you are blocking Direct Messages."
                    )
                }
                val adminBuilder = EmbedBuilder()
                builder.setTitle("**" + event.selfUser.name + "** Commands:")
                commandClient.commands
                    .filter { it.category?.name == "Admin" }
                    .forEach { command ->
                        if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                            if (category != command.category) {
                                category = command.category
                                val message = category.let { it?.name } ?: "No Category"
                                adminBuilder.appendDescription("\n\n  **").appendDescription(message)
                                    .appendDescription(":**\n")
                            }
                            adminBuilder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                .appendDescription(if (commandClient.prefix == null) " " else "")
                                .appendDescription(command.name)
                                .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                .appendDescription(" - ").appendDescription(command.help)
                        }
                    }
                event.replyInDm(
                    adminBuilder.build(),
                    { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                    event!!.replyWarning(
                        "Help message cannot be sent because you are blocking Direct Messages."
                    )
                }
                val ownerBuilder = EmbedBuilder()
                builder.setTitle("**" + event.selfUser.name + "** Commands:")
                commandClient.commands
                    .filter { it.category?.name == "Owner" }
                    .forEach { command ->
                        if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                            if (category != command.category) {
                                category = command.category
                                val message = category.let { it?.name } ?: "No Category"
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
                if (owner != null) {
                    ownerBuilder.setFooter(
                        "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                        owner.avatarUrl
                    )
                }
                event.replyInDm(
                    ownerBuilder.build(),
                    { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                    event!!.replyWarning(
                        "Help message cannot be sent because you are blocking Direct Messages."
                    )
                }
            } else {
                if (event.args == "Mod" || event.args == "DJ" || event.args == "Admin" || event.args == "Music" || event.args == "Owner") {
                    val Builder = EmbedBuilder()
                    Builder.setTitle("**" + event.args + "** Commands:")
                    commandClient.commands
                        .filter { it.category?.name == event.args }
                        .forEach { command ->
                            if (!command.isHidden && (!command.isOwnerCommand || event!!.isOwner)) {
                                Builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                    .appendDescription(if (commandClient.prefix == null) " " else "")
                                    .appendDescription(command.name)
                                    .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                    .appendDescription(" - ").appendDescription(command.help)
                            }
                        }
                    val owner: User? = event.jda.getUserById(commandClient.ownerId)
                    if (owner != null) {
                        Builder.setFooter(
                            "For additional help, contact ${owner.asTag} or join https://discord.gg/Eyetd8J.",
                            owner.avatarUrl
                        )
                    }
                    event.replyInDm(
                        Builder.build(),
                        { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                        event!!.replyWarning(
                            "Help message cannot be sent because you are blocking Direct Messages."
                        )
                    }
                } else {
                    event.reply(":scream_cat: You must select from the following categories (Case sensitive): **Music, DJ, Fun, Mod, Admin, Owner**")
                }
            }
        } else {
            if (event.member.hasPermission(Permission.MANAGE_SERVER)) {
                if (event.args.isEmpty()) {
                    val builder = EmbedBuilder()
                    var category: Category? = null
                    builder.setTitle("**" + event.selfUser.name + "** Commands:")
                    commandClient.commands
                        .filter { it.category?.name == null || it.category?.name == "Music" || it.category?.name == "Fun" || it.category?.name == "DJ" || it.category?.name == "Mod" }
                        .forEach { command ->
                            if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                                if (category != command.category) {
                                    category = command.category
                                    val message = category.let { it?.name } ?: "No Category"
                                    builder.appendDescription("\n\n  **").appendDescription(message)
                                        .appendDescription(":**\n")
                                }
                                builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                    .appendDescription(if (commandClient.prefix == null) " " else "")
                                    .appendDescription(command.name)
                                    .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                    .appendDescription(" - ").appendDescription(command.help)
                            }
                        }
                    val owner: User? = event.jda.getUserById(commandClient.ownerId)
                    event.replyInDm(
                        builder.build(),
                        { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                        event!!.replyWarning(
                            "Help message cannot be sent because you are blocking Direct Messages."
                        )
                    }
                    val adminBuilder = EmbedBuilder()
                    builder.setTitle("**" + event.selfUser.name + "** Commands:")
                    commandClient.commands
                        .filter { it.category?.name == "Admin" }
                        .forEach { command ->
                            if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                                if (category != command.category) {
                                    category = command.category
                                    val message = category.let { it?.name } ?: "No Category"
                                    adminBuilder.appendDescription("\n\n  **").appendDescription(message)
                                        .appendDescription(":**\n")
                                }
                                adminBuilder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                    .appendDescription(if (commandClient.prefix == null) " " else "")
                                    .appendDescription(command.name)
                                    .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                    .appendDescription(" - ").appendDescription(command.help)
                            }
                        }
                    if (owner != null) {
                        adminBuilder.setFooter(
                            "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                            owner.avatarUrl
                        )
                    }
                    event.replyInDm(
                        adminBuilder.build(),
                        { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                        event!!.replyWarning(
                            "Help message cannot be sent because you are blocking Direct Messages."
                        )
                    }
                } else {
                    if (event.args == "Mod" || event.args == "DJ" || event.args == "Admin" || event.args == "Music") {
                        val Builder = EmbedBuilder()
                        Builder.setTitle("**" + event.args + "** Commands:")
                        commandClient.commands
                            .filter { it.category?.name == event.args }
                            .forEach { command ->
                                if (!command.isHidden && (!command.isOwnerCommand || event!!.isOwner)) {
                                    Builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                        .appendDescription(if (commandClient.prefix == null) " " else "")
                                        .appendDescription(command.name)
                                        .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                        .appendDescription(" - ").appendDescription(command.help)
                                }
                            }
                        val owner: User? = event.jda.getUserById(commandClient.ownerId)
                        if (owner != null) {
                            Builder.setFooter(
                                "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                                owner.avatarUrl
                            )
                        }
                        event.replyInDm(
                            Builder.build(),
                            { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                            event!!.replyWarning(
                                "Help message cannot be sent because you are blocking Direct Messages."
                            )
                        }
                    } else {
                        event.reply(":scream_cat: You must select from the following categories (Case sensitive): **Music, DJ, Fun, Mod, Admin**")
                    }
                }
            } else {
                if (event.member.hasPermission(Permission.BAN_MEMBERS)) {
                    if (event.args.isEmpty()) {
                        val builder = EmbedBuilder()
                        var category: Category? = null
                        builder.setTitle("**" + event.selfUser.name + "** Commands:")
                        commandClient.commands
                            .filter { it.category?.name == null || it.category?.name == "Music" || it.category?.name == "Fun" || it.category?.name == "DJ" || it.category?.name == "Mod" }
                            .forEach { command ->
                                if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                                    if (category != command.category) {
                                        category = command.category
                                        val message = category.let { it?.name } ?: "No Category"
                                        builder.appendDescription("\n\n  **").appendDescription(message)
                                            .appendDescription(":**\n")
                                    }
                                    builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                        .appendDescription(if (commandClient.prefix == null) " " else "")
                                        .appendDescription(command.name)
                                        .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                        .appendDescription(" - ").appendDescription(command.help)
                                }
                            }
                        val owner: User? = event.jda.getUserById(commandClient.ownerId)
                        if (owner != null) {
                            builder.setFooter(
                                "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                                owner.avatarUrl
                            )
                        }
                        event.replyInDm(
                            builder.build(),
                            { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                            event!!.replyWarning(
                                "Help message cannot be sent because you are blocking Direct Messages."
                            )
                        }
                    } else {
                        if (event.args == "Mod" || event.args == "DJ" || event.args == "Music") {
                            val Builder = EmbedBuilder()
                            Builder.setTitle("**" + event.args + "** Commands:")
                            commandClient.commands
                                .filter { it.category?.name == event.args }
                                .forEach { command ->
                                    if (!command.isHidden && (!command.isOwnerCommand || event!!.isOwner)) {
                                        Builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                            .appendDescription(if (commandClient.prefix == null) " " else "")
                                            .appendDescription(command.name)
                                            .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                            .appendDescription(" - ").appendDescription(command.help)
                                    }
                                }
                            val owner: User? = event.jda.getUserById(commandClient.ownerId)
                            if (owner != null) {
                                Builder.setFooter(
                                    "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                                    owner.avatarUrl
                                )
                            }
                            event.replyInDm(
                                Builder.build(),
                                { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                                event!!.replyWarning(
                                    "Help message cannot be sent because you are blocking Direct Messages."
                                )
                            }
                        } else {
                            event.reply(":scream_cat: You must select from the following categories (Case sensitive): **Music, DJ, Fun, Mod**")
                        }
                    }
                } else {
                    if (event.args.isEmpty()) {
                        val builder = EmbedBuilder()
                        var category: Category? = null
                        builder.setTitle("**" + event.selfUser.name + "** Commands:")
                        commandClient.commands
                            .filter { it.category?.name == null || it.category?.name == "Music" || it.category?.name == "Fun" || it.category?.name == "DJ" }
                            .forEach { command ->
                                if (!command.isHidden && (!command.isOwnerCommand || event.isOwner)) {
                                    if (category != command.category) {
                                        category = command.category
                                        val message = category.let { it?.name } ?: "No Category"
                                        builder.appendDescription("\n\n  **").appendDescription(message)
                                            .appendDescription(":**\n")
                                    }
                                    builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                        .appendDescription(if (commandClient.prefix == null) " " else "")
                                        .appendDescription(command.name)
                                        .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                        .appendDescription(" - ").appendDescription(command.help)
                                }
                            }
                        val owner: User? = event.jda.getUserById(commandClient.ownerId)
                        if (owner != null) {
                            builder.setFooter(
                                "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                                owner.avatarUrl
                            )
                        }
                        event.replyInDm(
                            builder.build(),
                            { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                            event!!.replyWarning(
                                "Help message cannot be sent because you are blocking Direct Messages."
                            )
                        }
                    } else {
                        if (event.args == "Mod" || event.args == "DJ" || event.args == "Music") {
                            val Builder = EmbedBuilder()
                            Builder.setTitle("**" + event.args + "** Commands:")
                            commandClient.commands
                                .filter { it.category?.name == event.args }
                                .forEach { command ->
                                    if (!command.isHidden && (!command.isOwnerCommand || event!!.isOwner)) {
                                        Builder.appendDescription("\n`").appendDescription(commandClient.textualPrefix)
                                            .appendDescription(if (commandClient.prefix == null) " " else "")
                                            .appendDescription(command.name)
                                            .appendDescription(if (command.arguments == null) "`" else " " + command.arguments + "`")
                                            .appendDescription(" - ").appendDescription(command.help)
                                    }
                                }
                            val owner: User? = event.jda.getUserById(commandClient.ownerId)
                            if (owner != null) {
                                Builder.setFooter(
                                    "For additional help, contact " + owner.asTag + " or join https://discord.gg/Eyetd8J.",
                                    owner.avatarUrl
                                )
                            }
                            event.replyInDm(
                                Builder.build(),
                                { unused: Message? -> if (event!!.isFromType(ChannelType.TEXT)) event!!.reactSuccess() }) { t: Throwable? ->
                                event!!.replyWarning(
                                    "Help message cannot be sent because you are blocking Direct Messages."
                                )
                            }
                        } else {
                            event.reply(":scream_cat: You must select from the following categories (Case sensitive): **Music, DJ, Fun**")
                        }
                    }
                }
            }
        }
    }
}