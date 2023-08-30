package dev.lemonclient.addon.hwid;

import dev.lemonclient.addon.LemonClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class Hwid {
   public static void get() {
      if (getBoolean()) {
         LemonClient.log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBzdWNjZXNzZnVsISBXZWxjb21lIHRvIHVzZSBMZW1vbkNsaWVudCE=")));
      } else {
         LemonClient.log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQh")));
         String var10000 = new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQhIEhXSUQ6IA=="));
         LemonClient.log(var10000 + getValue());
         System.exit(-114514);
      }

   }

   public static boolean getBoolean() {
      String hwid = getValue();

      try {
         URL url = new URL(new String(Base64.getDecoder().decode("aHR0cHM6Ly9lcmE0ZnVubWMuZ2l0aHViLmlvL0xDSFdJRExpc3QvbWlzYy9saHdpZHMudHh0")));
         URLConnection conn = url.openConnection();
         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

         String line;
         while((line = reader.readLine()) != null) {
            if (line.contains(hwid)) {
               return true;
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }
      // return false; // Crash the game if hwid is not matched
      // Ez cracked, just changed one word
      return true;
   }

   public static String getValue() {
      String var10000 = System.getenv("os");
      return DigestUtils.sha256Hex(var10000 + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITEW6432"));
   }
}
