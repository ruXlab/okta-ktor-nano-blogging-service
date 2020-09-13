package com.okta.demo.ktor

import io.ktor.html.*
import kotlinx.html.*
import kotlinx.html.FormEncType.applicationXWwwFormUrlEncoded
import kotlinx.html.FormMethod.post
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val timeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);


/**
 * Generic web page template, contains content placeholder where
 * content should be placed
 */
class MainTemplate(private val currentUsername: String? = null) : Template<HTML> {
    val content = Placeholder<HtmlBlockTag>()
    override fun HTML.apply() {
        head {
            title { +"Nano Blogging Service" }
            styleLink("https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css")
            meta(name = "viewport", content = "width=device-width, initial-scale=1, shrink-to-fit=no")
            meta(charset = "utf-8")
        }
        body("d-flex flex-column h-100") {
            header {
                div("navbar navbar-dark bg-dark shadow-sm") {
                    div("container") {
                        a(href = "/", classes = "font-weight-bold navbar-brand") {
                            +"📝 𝓝𝓐𝓝𝓞 𝓑𝓛𝓞𝓖𝓖𝓘𝓝𝓖 𝓢𝓔𝓡𝓥𝓘𝓒𝓔"
                        }
                        div("navbar-nav flex-row") {
                            if (currentUsername != null) {
                                a(href = "/${currentUsername}", classes = "nav-link mr-4") {
                                    +"Hello, $currentUsername"
                                }
                                a(href = "/logout", classes = "nav-link") {
                                    +"Logout"
                                }
                            } else {
                                div("navbar-text mr-4") {
                                    +"Hello, Guest"
                                }
                                div("navbar-item") {
                                    a(href = "/login", classes = "nav-link") {
                                        +"Login"
                                    }
                                }
                            }
                        }

                    }
                }
            }
            main("flex-shrink-0 mt-3") {
                div("container col-xs-12 col-lg-8") {
                    insert(content)
                }
            }
        }
    }
}


/**
 * Displays feed block only
 */
fun FlowContent.feedBlock(feedItems: List<BlogRecord>) {
    feedItems.forEach { record ->
        div("entity card m-4") {
            div("w-100 card-header") {
                h4("user font-weight-bold mb-0 pb-0 d-inline-block") {
                    a(href = "/${record.userHandle}") { +record.userHandle }
                }
                span("float-right text-secondary") {
                    +record.createdAt.format(timeFormatter)
                }
            }
            div("card-body") {
                h5 { +record.text }
            }
        }
    }
}

/**
 * Renders send message form
 */
fun FlowContent.sendMessageForm() {
    form("/", encType = applicationXWwwFormUrlEncoded, method = post) {
        div("mb-3") {
            div("input-group") {
                input(classes = "form-control", name = "text") {
                    placeholder = "Your nano message"
                    required = true
                    autoFocus = true
                }
                div("input-group-append") {
                    button(classes = "btn btn-success") { +"Send! 🚀" }
                }
            }
        }
    }
}

/**
 * Renders feed page with given title and records
 */
fun FlowContent.feedPage(title: String, records: List<BlogRecord>, canPostMessage: Boolean) {
    if (canPostMessage)
        sendMessageForm()

    hr { }
    h2("text-center") { +title }
    feedBlock(records.sortedByDescending(BlogRecord::createdAt))
}


