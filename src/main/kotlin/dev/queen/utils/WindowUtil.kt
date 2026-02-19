package dev.queen.utils

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import org.lwjgl.opengl.Display
import java.lang.reflect.Field

object WindowUtil {

    fun getHwnd(): WinDef.HWND {
        val title = Display.getTitle()
        val hwnd = User32.INSTANCE.FindWindow(null, title) ?: throw RuntimeException("Failed to find window with title: $title")
        return hwnd
    }

}