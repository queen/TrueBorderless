package dev.queen

import com.sun.java.swing.plaf.windows.resources.windows_de
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.RECT
import com.sun.jna.platform.win32.WinNT.HANDLE
import dev.queen.utils.MONITORINFO
import dev.queen.utils.User32Ex
import dev.queen.utils.WindowUtil
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode

object ApplyBorderless {
    // gwl constants
    private const val GWL_STYLE = -16
    private const val GWL_EXSTYLE = -20

    // window styles
    private const val WS_CAPTION = 0x00C00000
    private const val WS_THICKFRAME = 0x00040000
    private const val WS_MINIMIZE = 0x20000000
    private const val WS_MAXIMIZEBOX = 0x00010000
    private const val WS_SYSMENU = 0x00080000

    // extended styles
    private const val WS_EX_DLGMODALFRAME = 0x00000001
    private const val WS_EX_CLIENTEDGE = 0x00000200
    private const val WS_EX_STATICEDGE = 0x00020000
    private const val WS_EX_WINDOWEDGE = 0x00000100

    // setwindowpos flags
    private const val SWP_FRAMECHANGED = 0x0020
    private const val SWP_NOZORDER = 0x0004
    private const val SWP_NOOWNERZORDER = 0x0200

    fun applyBorderless() {
        var hwnd = WindowUtil.getHwnd()
        val user32 = User32Ex.INSTANCE

        val monitor = user32.MonitorFromWindow(hwnd, 2)
        val mi = MONITORINFO()

        mi.cbSize = WinDef.DWORD(mi.size().toLong())

        user32.GetMonitorInfo(monitor, mi)
        val rect = mi.rcMonitor


        val width = rect.right - rect.left
        val height = rect.bottom - rect.top

        Display.setDisplayMode(DisplayMode(width, height))
        Display.update()

        hwnd = WindowUtil.getHwnd() // re-acquire hwnd after display mode change

        val mc = Minecraft.getMinecraft()
        mc.displayWidth = width
        mc.displayHeight = height
        mc.resize(width, height)


        val targetStyle = user32.GetWindowLong(hwnd, GWL_STYLE) and
                (WS_CAPTION or WS_THICKFRAME or WS_MINIMIZE or WS_MAXIMIZEBOX or WS_SYSMENU).inv()
        val targetExStyle = user32.GetWindowLong(hwnd, GWL_EXSTYLE) and
                (WS_EX_DLGMODALFRAME or WS_EX_CLIENTEDGE or WS_EX_STATICEDGE or WS_EX_WINDOWEDGE).inv()

        user32.SetWindowLong(hwnd, GWL_STYLE, targetStyle)
        user32.SetWindowLong(hwnd, GWL_EXSTYLE, targetExStyle)
        user32.SetWindowPos(hwnd, null, rect.left, rect.top, width, height, SWP_FRAMECHANGED or SWP_NOZORDER or SWP_NOOWNERZORDER)


        val currentStyle = user32.GetWindowLong(hwnd, GWL_STYLE)
        val currentExStyle = user32.GetWindowLong(hwnd, GWL_EXSTYLE)
    }
}