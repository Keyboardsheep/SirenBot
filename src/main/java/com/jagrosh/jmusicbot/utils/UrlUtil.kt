package com.jagrosh.jmusicbot.utils

val allowedUrls: Set<String> = setOf(
    "https://www.youtube.com/",
    "https://youtube.com/",
    "http://www.youtube.com/",
    "http://youtube.com/",
    "https://www.youtu.be/",
    "https://youtu.be/",
    "http://www.youtu.be/",
    "http://youtu.be/",
    "https://m.youtube.com/",
    "http://m.youtube.com/",
    "https://www.soundcloud.com/",
    "https://soundcloud.com/",
    "http://www.soundcloud.com/",
    "http://soundcloud.com/",
)

fun isUrlAllowed(url: String): Boolean = allowedUrls.any { url.startsWith(it, ignoreCase = true) }
