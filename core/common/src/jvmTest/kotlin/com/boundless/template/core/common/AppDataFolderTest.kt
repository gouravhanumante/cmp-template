package com.boundless.template.core.common

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

// App data must land in the folder each OS keeps for it, never one that roams or is temporary.
class AppDataFolderTest {
    private fun path(os: DesktopOs, home: String, vararg env: Pair<String, String>) = desktopAppDataPath("MyApp", os, home) { env.toMap()[it] }

    @Test
    fun macUsesApplicationSupport() {
        assertEquals(File("/Users/ana/Library/Application Support/MyApp"), path(DesktopOs.MAC, "/Users/ana"))
    }

    @Test
    fun windowsUsesLocalAppDataNotRoaming() {
        val dir = path(DesktopOs.WINDOWS, "C:\\Users\\Ana", "LOCALAPPDATA" to "C:\\Users\\Ana\\AppData\\Local", "APPDATA" to "C:\\Users\\Ana\\AppData\\Roaming")
        assertEquals(File("C:\\Users\\Ana\\AppData\\Local", "MyApp"), dir)
    }

    @Test
    fun windowsWithoutLocalAppDataStillUsesTheLocalFolder() {
        assertEquals(File("C:\\Users\\Ana\\AppData\\Local", "MyApp"), path(DesktopOs.WINDOWS, "C:\\Users\\Ana"))
    }

    @Test
    fun linuxUsesXdgDataHomeAndFallsBackWhenItIsUnsetOrRelative() {
        assertEquals(File("/data/ana/myapp"), path(DesktopOs.LINUX, "/home/ana", "XDG_DATA_HOME" to "/data/ana"))
        assertEquals(File("/home/ana/.local/share/myapp"), path(DesktopOs.LINUX, "/home/ana"))
        assertEquals(File("/home/ana/.local/share/myapp"), path(DesktopOs.LINUX, "/home/ana", "XDG_DATA_HOME" to "relative/dir"))
    }

    @Test
    fun theOsIsReadFromItsName() {
        assertEquals(DesktopOs.MAC, currentDesktopOs("Mac OS X"))
        assertEquals(DesktopOs.WINDOWS, currentDesktopOs("Windows 11"))
        assertEquals(DesktopOs.LINUX, currentDesktopOs("Linux"))
    }
}
