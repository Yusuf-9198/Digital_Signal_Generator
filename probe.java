import java.util.*;
import encoding.*;
import decoding.*;
import scrambling.*;

public class Probe {
  static String brutePalindrome(String s) {
    String best = "";
    for (int i=0;i<s.length();i++) {
      for (int j=i;j<s.length();j++) {
        String t=s.substring(i,j+1);
        if (t.equals(new StringBuilder(t).reverse().toString()) && t.length() > best.length()) best=t;
      }
    }
    return best;
  }
  static String findPalindrome(String s) {
    if (s == null || s.isEmpty()) return "";
    String transformed = "^";
    for (int i=0;i<s.length();i++) transformed += '#' + s.charAt(i);
    transformed += "#$";
    int n = transformed.length();
    int[] radius = new int[n];
    int center=0,right=0,maxLen=0,maxCenter=0;
    for (int i=1;i<n-1;i++) {
      int mirror=2*center-i;
      if (i < right) radius[i] = Math.min(right-i, radius[mirror]);
      while (i+1+radius[i] < n && i-1-radius[i] >= 0 && transformed.charAt(i+1+radius[i]) == transformed.charAt(i-1-radius[i])) radius[i]++;
      if (i + radius[i] > right) { center=i; right=i+radius[i]; }
      if (radius[i] > maxLen) { maxLen = radius[i]; maxCenter = i; }
    }
    int start = (maxCenter - maxLen) / 2;
    return s.substring(start, start + maxLen);
  }
  static void testPalindrome() {
    for (int len = 0; len <= 12; len++) {
      for (int iter = 0; iter < 1000; iter++) {
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random(len * 100 + iter);
        for (int i=0;i<len;i++) sb.append(rnd.nextBoolean() ? '0' : '1');
        String s = sb.toString();
        String got = findPalindrome(s);
        String exp = brutePalindrome(s);
        if (!Objects.equals(got, exp)) {
            throw new AssertionError("pal mismatch: s=|" + s + "| got=|" + got + "| exp=|" + exp + "|");
        }
      }
    }
    System.out.println("Palindromes OK");
  }

  static void testB8ZS() {
    AMIEncoder e = new AMIEncoder();
    AMIDecoder d = new AMIDecoder();
    B8ZSScrambler s = new B8ZSScrambler();
    B8ZSDescrambler ds = new B8ZSDescrambler();
    Random rnd = new Random(2);
    for (int len = 1; len <= 50; len++) {
      for (int iter = 0; iter < 500; iter++) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<len;i++) sb.append(rnd.nextBoolean() ? '1' : '0');
        String bits = sb.toString();
        int[] enc = s.scramble(e.encode(bits));
        String recovered = d.decode(ds.descramble(enc));
        if (!bits.equals(recovered)) {
            throw new AssertionError("B8ZS failed: bits=" + bits + " enc=" + Arrays.toString(enc) + " rec=" + recovered);
        }
      }
    }
    System.out.println("B8ZS OK");
  }

  static void testHDB3() {
    AMIEncoder e = new AMIEncoder();
    AMIDecoder d = new AMIDecoder();
    HDB3Scrambler s = new HDB3Scrambler();
    HDB3Descrambler ds = new HDB3Descrambler();
    Random rnd = new Random(3);
    for (int len = 1; len <= 60; len++) {
      for (int iter = 0; iter < 500; iter++) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<len;i++) sb.append(rnd.nextBoolean() ? '1' : '0');
        String bits = sb.toString();
        int[] enc = s.scramble(e.encode(bits));
        String recovered = d.decode(ds.descramble(enc));
        if (!bits.equals(recovered)) {
            throw new AssertionError("HDB3 failed: bits=" + bits + " enc=" + Arrays.toString(enc) + " rec=" + recovered);
        }
      }
    }
    System.out.println("HDB3 OK");
  }

  public static void main(String[] args) {
    testPalindrome();
    testB8ZS();
    testHDB3();
  }
}
