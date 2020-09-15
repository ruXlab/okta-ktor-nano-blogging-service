package com.okta.demo.ktor

import com.okta.jwt.JwtVerifiers
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


fun Application.setupAuth() {
    val oktaConfig = oktaConfigReader(ConfigFactory.load() ?: throw Exception("Could not load config"))

    install(Authentication) {
        oauth {
            urlProvider = { "http://localhost:8080/login/authorization-callback" }
            providerLookup = { oktaConfig.asOAuth2Config() }
            client = HttpClient()
            println(oktaConfig)
        }
    }

    val accessTokenVerifier = JwtVerifiers.accessTokenVerifierBuilder()
        .setAudience(oktaConfig.audience)
        .setIssuer(oktaConfig.orgUrl)
        .build()


    val idVerifier = JwtVerifiers.idTokenVerifierBuilder()
        .setClientId(oktaConfig.clientId)
        .setIssuer(oktaConfig.orgUrl)
        .build()

    // Routes, related to authorization, login and logout
    routing {
        authenticate {
            // Okta calls this endpoint providing accessToken along with requested idToken
            get("/login/authorization-callback") {
                val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                    ?: throw Exception("No principal was given")

                val accessToken = accessTokenVerifier.decode(principal.accessToken)

                val idTokenString = principal.extraParameters["id_token"]
                    ?: throw Exception("id_token wasn't returned")
                val idToken = idVerifier.decode(idTokenString, null)

                val fullName = (idToken.claims["name"] ?: accessToken.claims["sub"] ?: "UNKNOWN_NAME").toString()

                println("User $fullName logged in successfully")

                val session = UserSession(
                    username = fullName.replace("[^a-zA-Z0-9]".toRegex(), ""),
                    idToken = idTokenString
                )

                call.sessions.set(session)
                call.respondRedirect("/")
            }

            // When guest accessing /login it automatically redirects to okta login page
            get("/login") {
                call.respondRedirect("/")
            }
        }

        // Perform logout by cleaning cookies and start RP-initiated logout
        get("/logout") {
            val idToken = call.session?.idToken

            call.sessions.clear<UserSession>()

            val redirectLogout = when (idToken) {
                null -> "/"
                else -> URLBuilder(oktaConfig.logoutUrl).run {
                    parameters.append("post_logout_redirect_uri", "http://localhost:8080")
                    parameters.append("id_token_hint", idToken)
                    buildString()
                }
            }

            call.respondRedirect(redirectLogout)
        }
    }
}
