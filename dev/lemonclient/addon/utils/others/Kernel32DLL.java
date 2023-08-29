package dev.lemonclient.addon.utils.others;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

public interface Kernel32DLL extends Library {
   Kernel32DLL INSTANCE = (Kernel32DLL)Native.loadLibrary("kernel32", Kernel32DLL.class);

   boolean SetConsoleTitleA(String var1);

   boolean Beep(int var1, int var2);

   void CopyMemory(byte[] var1, byte[] var2, long var3);

   void GetSystemTime(WinBase.SYSTEMTIME var1);

   int GetCurrentThreadId();

   int GetCurrentProcessId();

   int OpenProcess(int var1, boolean var2, int var3);

   boolean Closeint(int var1);

   boolean ReadProcessMemory(int var1, long var2, byte[] var4, int var5, int[] var6);

   boolean WriteProcessMemory(int var1, long var2, byte[] var4, int var5, int[] var6);

   long VirtualAllocEx(int var1, long var2, int var4, int var5, int var6);

   boolean VirtualFreeEx(int var1, long var2, int var4, int var5);

   boolean VirtualProtectEx(int var1, long var2, int var4, int var5, int[] var6);

   boolean VirtualQueryEx(int var1, long var2, WinNT.MEMORY_BASIC_INFORMATION var4, int var5);

   int CreateRemoteThread(int var1, long var2, int var4, long var5, long var7, int var9, int[] var10);

   int CreateRemoteThread(int var1, long var2, int var4, int var5, long var6, int var8, int[] var9);

   int OpenThread(int var1, boolean var2, int var3);

   int SuspendThread(int var1);

   void CloseHandle(int var1);

   int GetModuleHandle(String var1);

   int ResumeThread(int var1);

   boolean TerminateThread(int var1, int var2);

   boolean FlushInstructionCache(int var1, long var2, int var4);

   boolean GetExitCodeThread(int var1, int[] var2);

   int CreateToolhelp32Snapshot(int var1, int var2);

   boolean Process32First(int var1, Tlhelp32.PROCESSENTRY32 var2);

   boolean Process32Next(int var1, Tlhelp32.PROCESSENTRY32 var2);

   boolean Thread32Next(int var1, Tlhelp32.THREADENTRY32 var2);

   boolean ContinueThread(int var1);

   int WaitForSingleObject(int var1, int var2);

   int WaitForMultipleObjects(int var1, int[] var2, boolean var3, int var4);

   void Sleep(int var1);

   int LoadLibraryEx(String var1);

   long GetProcAddress(long var1, String var3);

   void DisableThreadLibraryCalls(long var1);
}
