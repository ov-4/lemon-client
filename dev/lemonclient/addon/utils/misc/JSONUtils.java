package dev.lemonclient.addon.utils.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
   private static String readAll(Reader rd) throws IOException {
      StringBuilder stringBuilder = new StringBuilder();

      int cp;
      while((cp = rd.read()) != -1) {
         stringBuilder.append((char)cp);
      }

      return stringBuilder.toString();
   }

   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
      InputStream is = (new URL(url)).openStream();

      JSONObject var2;
      try {
         var2 = new JSONObject(readAll(new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))));
      } catch (Throwable var5) {
         if (is != null) {
            try {
               is.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (is != null) {
         is.close();
      }

      return var2;
   }
}
