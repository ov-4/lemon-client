package dev.lemonclient.addon.hwid.utils;

public class Alphabet {
   public static String get(int letter) {
      return Alphabet.LETTERS.values()[letter].name();
   }

   public static enum LETTERS {
      A,
      B,
      C,
      D,
      E,
      F,
      G,
      H,
      I,
      J,
      K,
      L,
      M,
      N,
      O,
      P,
      Q,
      R,
      S,
      T,
      U,
      V,
      W,
      X,
      Y,
      Z;

      // $FF: synthetic method
      private static LETTERS[] $values() {
         return new LETTERS[]{A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z};
      }
   }
}
