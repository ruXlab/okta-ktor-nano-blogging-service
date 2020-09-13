package com.okta.demo.ktor

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Application.setupRoutes() = routing {
    post("/") { 
        val actor = call.session?.username
            ?: throw Exception("User must be logged in first")
        val text = call.receiveParameters()["text"]?.takeIf(String::isNotBlank)
            ?: throw Exception("Invalid request - text must be provided")

        blogRecords.insert(actor, text)

        call.respondRedirect("/")
    }

    get("/{username?}") {
        val username = call.parameters["username"]
        call.respondHtmlTemplate(MainTemplate(call.session?.username)) {
            content {
                val canSendMessage = call.session != null
                if (username == null) feedPage("🏠 Home feed", blogRecords.all, canSendMessage)
                else feedPage("👤 ${username}'s blog", blogRecords.byUser(username), canSendMessage)
            }
        }
    }
}

