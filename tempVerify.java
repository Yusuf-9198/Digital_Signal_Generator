import java.util.*;

public class Verify {
    static String palindrome(String s) {
        if (s == null || s.isEmpty()) return "";
        String transformed = "^";
        for (int i = 0; i < s.length(); i++) {
            transformed += "#" + s.charAt(i);
        }
        transformed += "#$";
        int n = transformed.length();
        int[] radius = new int[n];
        int center = 0, right = 0, maxLength = 0, maxCenter = 0;
        for (int i = 1; i < n - 1; i++) {
            int mirror = 2 * center - i;
            if (i < right) radius[i] = Math.min(right - i, radius[mirror]);
            while (i + 1 + radius[i] < n && i - 1 - radius[i] >= 0 && transformed.charAt(i + 1 + radius[i]) == transformed.charAt(i - 1 - radius[i])) {
                radius[i]++;
            }
            if (i + radius[i] > right) {
                center = i;
                right = i + radius[i];
            }
            if (radius[i] > maxLength) {
                maxLength = radius[i];
                maxCenter = i;
            }
        }
        int start = (maxCenter - maxLength) / 2;
        return s.substring(start, start + maxLength);
    }
    static String brute(String s) {
        String best = "";
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                String t = s.substring(i, j + 1);
                if (t.equals(new StringBuilder(t).reverse().toString()) && t.length() > best.length()) best = t;
            }
        }
        return best;
    }

    static void checkPalindrome() {
        String[] samples = {"", "1", "0", "10", "01", "11", "00", "101", "100", "abba", "abcba", "abacdfgdcaba", "aabbaa", "aaa", "0110", "1001", "110010011", "01011010"};
        for (String s : samples) {
            String got = palindrome(s);
            String exp = brute(s);
            if (!Objects.equals(got, exp)) {
                throw new AssertionError("palindrome mismatch: s=" + s + " got=" + got + " exp=" + exp);
            }
        }
        for (int len = 0; len < 12; len++) {
            for (int k = 0; k < 200; k++) {
                char[] arr = new char[len];
                for (int i=0;i<len;i++) arr[i]=(Math.random() < 0.5 ? '0' : '1');
                String s = new String(arr);
                String got = palindrome(s);
                String exp = brute(s);
                if (!Objects.equals(got, exp)) {
                    throw new AssertionError("random mismatch: s=" + s + " got=" + got + " exp=" + exp);
                }
            }
        }
        System.out.println("Palindromes OK");
    }

    public static void main(String[] args) {
        checkPalindrome();
    }
}
