package dev.lemonclient.addon.utils;

import dev.lemonclient.addon.utils.misc.JSONUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;
import org.json.JSONObject;

public class Version {
   public static String API_URL;
   private final String string;
   private final int[] numbers;

   public Version(String string) {
      this.string = string;
      this.numbers = new int[3];
      String[] split = string.split("\\.");
      if (split.length != 3) {
         throw new IllegalArgumentException("Version string needs to have 3 numbers.");
      } else {
         for(int i = 0; i < 3; ++i) {
            try {
               this.numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException var5) {
               throw new IllegalArgumentException("Failed to parse version string.");
            }
         }

      }
   }

   public boolean isZero() {
      return this.numbers[0] == 0 && this.numbers[1] == 0 && this.numbers[2] == 0;
   }

   public boolean isHigherThan(Version version) {
      for(int i = 0; i < 3; ++i) {
         if (this.numbers[i] > version.numbers[i]) {
            return true;
         }

         if (this.numbers[i] < version.numbers[i]) {
            return false;
         }
      }

      return false;
   }

   public static Version getInstallerVersion() {
      Scanner scanner = (new Scanner((InputStream)Objects.requireNonNull(Version.class.getResourceAsStream("/metadata.json")))).useDelimiter("\\A");
      return new Version((new JSONObject(scanner.hasNext() ? scanner.next() : "")).getString("version"));
   }

   public static Version getLatest() throws IOException {
      return new Version(JSONUtils.readJsonFromUrl(API_URL + "Version/Installer/metadata.json").getString("latest_version"));
   }

   public String toString() {
      return this.string;
   }

   static {
      API_URL = new String(Base64.getDecoder().decode("aHR0cDovLzd6aGFuLnRvcDo2MDAv"), StandardCharsets.UTF_8);
   }
}
