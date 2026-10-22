package com.boundless.template.core.database

/** Where the database lives, so a screen can warn when nothing will survive a reload. */
enum class DatabaseStorage {
    ON_DEVICE,

    /** Web only: the page wasn't served with the COOP/COEP headers, so the browser won't share its file storage. */
    MEMORY_ONLY,
}

expect fun databaseStorage(): DatabaseStorage
