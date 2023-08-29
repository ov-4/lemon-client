package dev.lemonclient.addon.utils.others;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

public interface User32DLL extends Library {
   User32DLL INSTANCE = (User32DLL)Native.loadLibrary("user32", User32DLL.class);

   int MessageBoxA(long var1, String var3, String var4, int var5);

   int FindWindow(String var1, String var2);

   int GetClassName(int var1, char[] var2, int var3);

   boolean GetWindowRect(int var1, WinDef.RECT var2);

   boolean GetClientRect(int var1, WinDef.RECT var2);

   int GetWindowText(int var1, char[] var2, int var3);

   int GetTopWindow(int var1);

   int GetWindowTextLength(int var1);

   int GetWindowModuleFileName(int var1, char[] var2, int var3);

   int GetWindowThreadProcessId(long var1, IntByReference var3);

   boolean EnumWindows(WinUser.WNDENUMPROC var1, Pointer var2);

   boolean EnumChildWindows(int var1, WinUser.WNDENUMPROC var2, Pointer var3);

   boolean EnumThreadWindows(int var1, WinUser.WNDENUMPROC var2, Pointer var3);

   boolean BringWindowToTop(int var1);

   boolean FlashWindowEx(WinUser.FLASHWINFO var1);

   WinDef.HICON LoadIcon(WinDef.HINSTANCE var1, String var2);

   int LoadImage(WinDef.HINSTANCE var1, String var2, int var3, int var4, int var5, int var6);

   long GetNextWindow(long var1, int var3);

   boolean DestroyIcon(WinDef.HICON var1);

   int GetWindowLong(int var1, int var2);

   int SetWindowLong(int var1, int var2, int var3);

   BaseTSD.LONG_PTR GetWindowLongPtr(int var1, int var2);

   Pointer SetWindowLongPtr(int var1, int var2, Pointer var3);

   boolean SetLayeredWindowAttributes(int var1, int var2, byte var3, int var4);

   boolean GetLayeredWindowAttributes(int var1, IntByReference var2, ByteByReference var3, IntByReference var4);

   boolean UpdateLayeredWindow(int var1, WinDef.HDC var2, WinDef.POINT var3, WinUser.SIZE var4, WinDef.HDC var5, WinDef.POINT var6, int var7, WinUser.BLENDFUNCTION var8, int var9);

   int SetWindowRgn(int var1, WinDef.HRGN var2, boolean var3);

   boolean GetKeyboardState(byte[] var1);

   short GetAsyncKeyState(int var1);

   WinUser.HHOOK SetWindowsHookEx(int var1, WinUser.HOOKPROC var2, WinDef.HINSTANCE var3, int var4);

   long CallNextHookEx(WinUser.HHOOK var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   boolean UnhookWindowsHookEx(WinUser.HHOOK var1);

   int GetMessage(WinUser.MSG var1, int var2, int var3, int var4);

   boolean PeekMessage(WinUser.MSG var1, int var2, int var3, int var4, int var5);

   boolean TranslateMessage(WinUser.MSG var1);

   long DispatchMessage(WinUser.MSG var1);

   void PostMessage(int var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   int PostThreadMessage(int var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   void PostQuitMessage(int var1);

   int GetSystemMetrics(int var1);

   int SetParent(int var1, int var2);

   boolean IsWindowVisible(int var1);

   boolean MoveWindow(int var1, int var2, int var3, int var4, int var5, boolean var6);

   boolean SetWindowPos(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   boolean AttachThreadInput(WinDef.DWORD var1, WinDef.DWORD var2, boolean var3);

   boolean SetForegroundWindow(int var1);

   int GetForegroundWindow();

   int SetFocus(int var1);

   WinDef.DWORD SendInput(WinDef.DWORD var1, WinUser.INPUT[] var2, int var3);

   WinDef.DWORD WaitForInputIdle(int var1, WinDef.DWORD var2);

   boolean InvalidateRect(int var1, WinDef.RECT var2, boolean var3);

   boolean RedrawWindow(int var1, WinDef.RECT var2, WinDef.HRGN var3, WinDef.DWORD var4);

   int GetWindow(int var1, WinDef.DWORD var2);

   boolean UpdateWindow(int var1);

   boolean ShowWindow(int var1, int var2);

   boolean CloseWindow(int var1);

   boolean RegisterHotKey(int var1, int var2, int var3, int var4);

   boolean UnregisterHotKey(Pointer var1, int var2);

   boolean GetLastInputInfo(WinUser.LASTINPUTINFO var1);

   WinDef.ATOM RegisterClassEx(WinUser.WNDCLASSEX var1);

   boolean UnregisterClass(String var1, WinDef.HINSTANCE var2);

   int CreateWindowEx(int var1, String var2, String var3, int var4, int var5, int var6, int var7, int var8, int var9, WinDef.HMENU var10, WinDef.HINSTANCE var11, WinDef.LPVOID var12);

   boolean DestroyWindow(int var1);

   boolean GetClassInfoEx(WinDef.HINSTANCE var1, String var2, WinUser.WNDCLASSEX var3);

   long CallWindowProc(Pointer var1, int var2, int var3, WinDef.WPARAM var4, WinDef.LPARAM var5);

   long DefWindowProc(int var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   WinUser.HDEVNOTIFY RegisterDeviceNotification(int var1, Structure var2, int var3);

   boolean UnregisterDeviceNotification(WinUser.HDEVNOTIFY var1);

   int RegisterWindowMessage(String var1);

   boolean GetMonitorInfo(WinUser.HMONITOR var1, WinUser.MONITORINFO var2);

   boolean GetMonitorInfo(WinUser.HMONITOR var1, WinUser.MONITORINFOEX var2);

   boolean EnumDisplayMonitors(WinDef.HDC var1, WinDef.RECT var2, WinUser.MONITORENUMPROC var3, WinDef.LPARAM var4);

   boolean GetWindowPlacement(int var1, WinUser.WINDOWPLACEMENT var2);

   boolean SetWindowPlacement(int var1, WinUser.WINDOWPLACEMENT var2);

   boolean AdjustWindowRect(WinDef.RECT var1, WinDef.DWORD var2, boolean var3);

   boolean AdjustWindowRectEx(WinDef.RECT var1, WinDef.DWORD var2, boolean var3, WinDef.DWORD var4);

   boolean ExitWindowsEx(WinDef.UINT var1, WinDef.DWORD var2);

   boolean LockWorkStation();

   long SendMessageTimeout(int var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4, int var5, int var6, WinDef.DWORDByReference var7);

   BaseTSD.ULONG_PTR GetClassLongPtr(int var1, int var2);

   int GetRawInputDeviceList(WinUser.RAWINPUTDEVICELIST[] var1, IntByReference var2, int var3);

   int GetDesktopWindow();

   boolean PrintWindow(int var1, WinDef.HDC var2, int var3);

   boolean IsWindowEnabled(int var1);

   boolean IsWindow(int var1);

   int FindWindowEx(int var1, int var2, String var3, String var4);

   int GetAncestor(int var1, int var2);

   int GetParent(int var1);

   boolean GetCursorPos(WinDef.POINT var1);

   boolean SetCursorPos(long var1, long var3);

   int SetWinEventHook(int var1, int var2, int var3, WinUser.WinEventProc var4, int var5, int var6, int var7);

   boolean UnhookWinEvent(int var1);

   int GetClassLong(int var1, int var2);

   int RegisterClipboardFormat(String var1);

   int GetActiveWindow();

   long SendMessage(int var1, int var2, int var3, long var4);

   int ReleaseDC(long var1, WinDef.HDC var3);

   int ReleaseDC(WinDef.HWND var1, WinDef.HDC var2);
}
