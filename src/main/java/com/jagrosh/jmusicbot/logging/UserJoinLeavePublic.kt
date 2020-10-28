package com.jagrosh.jmusicbot.logging

import com.jagrosh.jdautilities.command.CommandEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.util.*

class UserJoinLeavePublic {
    protected var lastExecutionMillisByChannelMap: MutableMap<String, Long> = LinkedHashMap()
    private val dmGuiMessageIdToUserIdMap: MutableMap<String, String> = LinkedHashMap()
    private val guiMessageIds: MutableSet<String> = LinkedHashSet()
    fun execute(event: CommandEvent) {
        fun onMessageReactionAdd(event: MessageReactionAddEvent) {
            if (guiMessageIds.contains(event.messageId) || dmGuiMessageIdToUserIdMap.containsKey(event.messageId)) {
                if (event.user != event.jda.selfUser) when (event.reactionEmote.name) {
                }
            }
        }
    }
}