package com.okta.demo.ktor

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.sessions.*
import io.ktor.util.*
import org.slf4j.event.Level
import kotlin.collections.set

fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    // We use sessions stored in signed cookies
    install(Sessions) {
        cookie<UserSession>("MY_SESSION") {
            val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
            val secretAuthKey = hex("02030405060708090a0b0c")
            cookie.extensions["SameSite"] = "lax"
            cookie.httpOnly = true
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretAuthKey))
        }
    }

    // Respond for HEAD verb
    install(AutoHeadResponse)

    // Load each request
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    // Configure ktor to use OAuth and register relevant routes
    setupAuth()

    // Register application routes
    setupRoutes()
}


// Shortcut for the current session
val ApplicationCall.session: UserSession?
    get() = sessions.get<UserSession>()


