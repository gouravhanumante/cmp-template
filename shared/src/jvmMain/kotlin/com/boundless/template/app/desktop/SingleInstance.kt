package com.boundless.template.app.desktop

import java.io.File
import java.io.RandomAccessFile
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.channels.FileLock
import java.nio.channels.OverlappingFileLockException
import java.security.SecureRandom
import kotlin.concurrent.thread

/**
 * One copy of the app per user on a computer: only one copy may write the database. The first
 * copy holds a lock in the data folder and listens on a local port; a second copy tells it to come to
 * the front and quits.
 */
class SingleInstance private constructor(
    private val lock: FileLock,
    private val server: ServerSocket,
) : AutoCloseable {
    override fun close() {
        server.close()
        lock.release()
        lock.channel().close()
    }

    companion object {
        private const val LOCK_FILE = "instance.lock"
        private const val PORT_FILE = "instance.port"

        /** Returns the claim if this is the first copy; otherwise asks that copy to [onActivate] and returns null. */
        fun claim(dataDir: File, onActivate: () -> Unit): SingleInstance? {
            val channel = RandomAccessFile(File(dataDir, LOCK_FILE), "rw").channel
            val lock =
                try {
                    channel.tryLock()
                } catch (_: OverlappingFileLockException) {
                    null
                }
            val portFile = File(dataDir, PORT_FILE)
            if (lock == null) {
                channel.close()
                askRunningCopyToActivate(portFile)
                return null
            }
            // The token stops any other local program from poking the window by guessing the port.
            val token = ByteArray(16).also(SecureRandom()::nextBytes).joinToString("") { "%02x".format(it) }
            val server = ServerSocket(0, 8, InetAddress.getLoopbackAddress())
            portFile.writeText("${server.localPort} $token")
            thread(isDaemon = true, name = "single-instance") {
                while (!server.isClosed) {
                    runCatching {
                        server.accept().use { socket ->
                            if (socket.getInputStream().bufferedReader().readLine() == token) onActivate()
                        }
                    }
                }
            }
            return SingleInstance(lock, server)
        }

        private fun askRunningCopyToActivate(portFile: File) {
            val (port, token) = runCatching { portFile.readText().trim().split(" ") }.getOrNull()?.takeIf { it.size == 2 } ?: return
            runCatching {
                Socket(InetAddress.getLoopbackAddress(), port.toInt()).use { it.getOutputStream().write("$token\n".encodeToByteArray()) }
            }
        }
    }
}
