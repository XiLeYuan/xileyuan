package com.xly.business.message.model

data class Message(
    val id: String,
    val sender: String,
    val content: String,
    val time: String
)