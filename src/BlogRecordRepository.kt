package com.okta.demo.ktor

class BlogRecordRepository {
    private val records = mutableListOf<BlogRecord>()

    val all: List<BlogRecord>
        get() = records

    fun insert(userHandle: String, text: String) {
        records += BlogRecord(userHandle, text)
    }

    fun byUser(userHandle: String)
        = records.filter { it.userHandle == userHandle }
}

val blogRecords = BlogRecordRepository().apply {
    insert("kack", "Hello world!")
    insert("kack", "Keep messages short and sweet! 💬")
    insert("ann", "OMG it's a future unikorn 🦄!")
    insert("rux", "Chronological feed! It's just like the good old days! ")
    insert("kotlin", "Wise language selection")
    insert("whitestone", "We'd like to invest 💰💰💰")
    insert("cat", "🐈🐱🙀😼😻🐾")
}
