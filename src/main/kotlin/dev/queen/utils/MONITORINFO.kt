package dev.queen.utils

import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinDef.DWORD
import com.sun.jna.platform.win32.WinDef.RECT

class MONITORINFO: Structure() {
    @JvmField
    var cbSize: DWORD = DWORD(size().toLong())

    @JvmField
    var rcMonitor: RECT = RECT()

    @JvmField
    var rcWork: RECT = RECT()

    @JvmField
    var dwFlags: DWORD = DWORD(0)


    override fun getFieldOrder(): List<String> =
        listOf("cbSize", "rcMonitor", "rcWork", "dwFlags")
}