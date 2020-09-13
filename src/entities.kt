package com.okta.demo.ktor

import java.time.LocalDateTime

data class BlogRecord(
    val userHandle: String,
    val text: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)


data class UserSession(
    val username: String,
    val idToken: String
)