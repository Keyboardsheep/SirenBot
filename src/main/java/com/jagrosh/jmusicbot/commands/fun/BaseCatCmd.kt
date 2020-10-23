package com.jagrosh.jmusicbot.commands.`fun`

import com.jagrosh.jmusicbot.commands.FunCommand
import org.slf4j.LoggerFactory
import java.util.*

abstract class BaseCatCmd : FunCommand() {
    var lastExecutionMillisByChannelMap: MutableMap<String, Long> = LinkedHashMap()
    var startupLog = LoggerFactory.getLogger("Startup")

    companion object {
        const val QUIET_MILLIS = 5000
    }
}