package com.boundless.template.core.common

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions

/** The computer's operating system, as far as choosing a data folder goes. */
enum class DesktopOs { MAC, WINDOWS, LINUX }

fun currentDesktopOs(osName: String = System.getProperty("os.name")): DesktopOs {
    val name = osName.lowercase()
    return when {
        name.contains("mac") || name.contains("darwin") -> DesktopOs.MAC
        name.contains("win") -> DesktopOs.WINDOWS
        else -> DesktopOs.LINUX
    }
}

/**
 * The per-user folder this app keeps its data in on a computer (docs/architecture.md "Storage"):
 * macOS Application Support, Windows Local (not Roaming) AppData, Linux XDG data home.
 */
fun desktopAppDataPath(
    appName: String,
    os: DesktopOs,
    home: String,
    env: (String) -> String?,
): File =
    when (os) {
        DesktopOs.MAC -> File(home, "Library/Application Support/$appName")
        // Roaming %APPDATA% is copied between computers on company networks; a database must not travel.
        DesktopOs.WINDOWS -> File(env("LOCALAPPDATA")?.takeIf { it.isNotBlank() } ?: "$home\\AppData\\Local", appName)
        // The XDG spec says to ignore a relative path and use the default.
        DesktopOs.LINUX -> File(env("XDG_DATA_HOME")?.takeIf { it.startsWith("/") } ?: "$home/.local/share", appName.lowercase())
    }

/** [desktopAppDataPath] for this computer and user, created if missing and readable only by the user. */
fun desktopAppDataDir(appName: String = APP_NAME): File {
    val dir = desktopAppDataPath(appName, currentDesktopOs(), System.getProperty("user.home"), System::getenv)
    if (!dir.exists()) {
        dir.mkdirs()
        runCatching { Files.setPosixFilePermissions(dir.toPath(), PosixFilePermissions.fromString("rwx------")) }
    }
    return dir
}
