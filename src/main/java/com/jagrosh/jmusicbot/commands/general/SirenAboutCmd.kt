/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.general

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import com.jagrosh.jdautilities.doc.standard.CommandInfo
import com.jagrosh.jmusicbot.commands.admin.getDefaultColor
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import java.awt.Color

@CommandInfo(name = ["About"], description = "Gets information about the bot.")
class SirenAboutCmd(private val color: Color, private val description: String, private val features: Array<String>, vararg perms: Permission) : Command() {
    private var IS_AUTHOR = true
    private var REPLACEMENT_ICON = "+"
    private val perms: Array<Permission>
    private var oauthLink: String? = null
    fun setIsAuthor(value: Boolean) {
        IS_AUTHOR = value
    }

    fun setReplacementCharacter(value: String) {
        REPLACEMENT_ICON = value
    }

    override fun execute(event: CommandEvent) {
//        if (oauthLink == null) {
//            try {
//                ApplicationInfo info = event.getJDA().getApplicationInfo().complete();
//                oauthLink = info.isBotPublic() ? info.getInviteUrl(0L, perms) : "";
//            } catch (Exception e) {
//                Logger log = LoggerFactory.getLogger("OAuth2");
//                log.error("Could not generate invite link ", e);
        oauthLink = ""
        //            }
//        }
        val builder = EmbedBuilder()
        builder.setColor(getDefaultColor(event))
        builder.setAuthor("All about " + event.selfUser.name + "!", null, event.selfUser.avatarUrl)
        val join = !(event.client.serverInvite == null || event.client.serverInvite.isEmpty())
        val inv = !oauthLink!!.isEmpty()
        val invline = "\nPlease [`invite`](https://discord.com/api/oauth2/authorize?client_id=754375096734318712&permissions=2146958847&scope=bot) me to your server!"
        val descr = StringBuilder().append("Hello! I am **").append(event.selfUser.name).append("**, a bot that is hosted and owned by **Keyboardsheep 82**.")
                .append("""
    
    Type **`${event.client.prefix}help`** to see my commands!
    Please [`visit`](https://siren.fun) my website!
    """.trimIndent()).append(invline).append("\n\nSome of my features include: ```css")
        for (feature in features) descr.append("\n").append(if (event.client.success.startsWith("<")) REPLACEMENT_ICON else event.client.success).append(" ").append(feature)
        descr.append(" ```")
        builder.setDescription(descr)
        builder.addField("Stats", """${event.jda.guilds.size} servers
|1 shard""".trimMargin(), true)
        builder.addField("Users", """${event.jda.users.size} unique
            |${event.jda.guilds.stream().mapToInt { g: Guild -> g.members.size }.sum()} total""".trimMargin(), true)
        builder.addField("Channels", """${event.jda.textChannels.size} Text
            |${event.jda.voiceChannels.size} Voice""".trimMargin(), true)
        builder.setFooter("Last restart", null)
        builder.setTimestamp(event.client.startTime)
        event.reply(builder.build())
    }

    init {
        name = "about"
        help = "shows info about the bot"
        guildOnly = false
        this.perms = perms as Array<Permission>
        botPermissions = arrayOf(Permission.MESSAGE_EMBED_LINKS)
    }
}