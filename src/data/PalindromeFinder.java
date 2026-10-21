package data;

public class PalindromeFinder {
    // it will find the longest palindromic substring in the given binary data

    public static String findLongestPalindrome(String data){
        if(data == null ||  data.isEmpty()) return "";

        // Transform the data so that we can handle even and odd length easily
        String transformed = preprocess(data);

        int n = transformed.length();

        // palindromeRadius[i] -> radius of palindrome centered at i
        int[] palindromeRadius = new int[n];

        int center = 0;
        int right = 0;

        int maxLength = 0;
        int maxCenter = 0;

        for (int i = 1; i < n - 1; i++) {
            int mirror = 2 * center - i;

            // If 'i' is inside the current palindrome,
            // use previously calculated information.
            if (i < right) {
                palindromeRadius[i] = Math.min(right - i, palindromeRadius[mirror]);
            }

            // Expand around current center
            while (i + 1 + palindromeRadius[i] < n
                    && i - 1 - palindromeRadius[i] >= 0
                    && transformed.charAt(i + 1 + palindromeRadius[i])
                    == transformed.charAt(i - 1 - palindromeRadius[i])) {
                palindromeRadius[i]++;
            }

            // Update center and right boundary 
            if (i + palindromeRadius[i] > right) {
                center = i;
                right = i + palindromeRadius[i];
            }

            // Keep track of longest palindrome
            if (palindromeRadius[i] > maxLength) {
                maxLength = palindromeRadius[i];
                maxCenter = i;
            }
        }

        // Convert transformed string position back to original data
        int start = (maxCenter - maxLength) / 2;
        return data.substring(start, start + maxLength);
    }

    // Adding separators so that both even and odd length palindromes can be processed uniformly.
    private static String preprocess(String data) {
        StringBuilder result = new StringBuilder();
        result.append('^');

        for(int i=0;i<data.length();i++){
            result.append('#');
            result.append(data.charAt(i));
        }
        result.append('#');
        result.append('$');

        return result.toString();
    }
    
}
