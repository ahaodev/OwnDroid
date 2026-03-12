package com.bintianqi.owndroid.utils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.CRC32

/**
 * Minimal ADB-over-TCP client for executing shell commands via the local ADB daemon.
 * Only works on devices where ADB is running without authentication (userdebug/debuggable builds
 * with persist.adb.secure=0).
 */
object AdbLocalClient {
    private const val CMD_CNXN = 0x4e584e43
    private const val CMD_AUTH = 0x48545541
    private const val CMD_OPEN = 0x4e45504f
    private const val CMD_OKAY = 0x59414b4f
    private const val CMD_CLSE = 0x45534c43
    private const val CMD_WRTE = 0x45545257

    private const val ADB_VERSION = 0x01000000
    private const val MAX_PAYLOAD = 4096

    private data class Msg(val cmd: Int, val arg0: Int, val arg1: Int, val data: ByteArray)

    private fun writeMsg(out: OutputStream, cmd: Int, arg0: Int, arg1: Int, data: ByteArray = ByteArray(0)) {
        val crc = CRC32().also { it.update(data) }.value.toInt()
        val buf = ByteBuffer.allocate(24 + data.size).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(cmd); putInt(arg0); putInt(arg1)
            putInt(data.size); putInt(crc); putInt(cmd.inv())
            put(data)
        }
        out.write(buf.array())
        out.flush()
    }

    private fun readMsg(inp: InputStream): Msg {
        val header = ByteArray(24)
        var read = 0
        while (read < 24) read += inp.read(header, read, 24 - read)
        val buf = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)
        val cmd = buf.int; val arg0 = buf.int; val arg1 = buf.int
        val len = buf.int; buf.int; buf.int // skip crc + magic
        val data = if (len > 0) {
            ByteArray(len).also { d -> var r = 0; while (r < len) r += inp.read(d, r, len - r) }
        } else ByteArray(0)
        return Msg(cmd, arg0, arg1, data)
    }

    /**
     * Execute [command] via the ADB daemon at 127.0.0.1:[port].
     * @throws IOException if connection fails or auth is required.
     * @return stdout+stderr output as a trimmed string.
     */
    fun exec(command: String, port: Int = 5555, timeoutMs: Int = 15_000): String {
        Socket("127.0.0.1", port).use { sock ->
            sock.soTimeout = timeoutMs
            val out = sock.getOutputStream()
            val inp = sock.getInputStream()

            // Handshake — send CONNECT without shell_v2 to get simple output stream
            writeMsg(out, CMD_CNXN, ADB_VERSION, MAX_PAYLOAD, "host::".toByteArray())
            val hello = readMsg(inp)
            when {
                hello.cmd == CMD_AUTH -> throw IOException("ADB requires authentication (persist.adb.secure=1)")
                hello.cmd != CMD_CNXN -> throw IOException("Unexpected ADB response: 0x${hello.cmd.toString(16)}")
            }

            // Open a shell service stream
            val localId = 1
            writeMsg(out, CMD_OPEN, localId, 0, "shell:$command; echo __EXIT_\$?__\u0000".toByteArray())

            val output = StringBuilder()
            loop@ while (true) {
                val msg = readMsg(inp)
                when (msg.cmd) {
                    CMD_OKAY -> Unit
                    CMD_WRTE -> {
                        output.append(String(msg.data))
                        writeMsg(out, CMD_OKAY, localId, msg.arg0)
                    }
                    else -> break@loop
                }
            }

            // Extract exit code marker and strip it from output
            val raw = output.toString()
            val exitMatch = Regex("__EXIT_(\\d+)__").find(raw)
            val exitCode = exitMatch?.groupValues?.get(1)?.toIntOrNull() ?: -1
            val cleanOutput = raw.replace(Regex("__EXIT_\\d+__"), "").trim()

            if (exitCode != 0) throw IOException(cleanOutput.ifEmpty { "Command failed (exit $exitCode)" })
            return cleanOutput
        }
    }
}
