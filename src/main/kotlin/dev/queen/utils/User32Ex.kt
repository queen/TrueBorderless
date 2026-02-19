package dev.queen.utils

import com.sun.jna.*
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.*;


interface User32Ex: StdCallLibrary {
    companion object {
        val INSTANCE: User32Ex = Native.loadLibrary("user32", User32Ex::class.java, W32APIOptions.DEFAULT_OPTIONS) as User32Ex
    }

    fun GetWindowLong(hwnd: WinDef.HWND, nIndex: Int): Int
    fun SetWindowLong(hwnd: WinDef.HWND, nIndex: Int, dwNewLong: Int): Int
    fun SetWindowPos(hwnd: WinDef.HWND, hWndInsertAfter: WinDef.HWND?, x: Int, y: Int, cx: Int, cy: Int, uFlags: Int): Boolean

    fun GetMonitorInfo(hMonitor: WinNT.HANDLE, lpmi: MONITORINFO): Boolean
    fun MonitorFromWindow(hwnd: WinDef.HWND, dwFlags: Int): WinNT.HANDLE
}