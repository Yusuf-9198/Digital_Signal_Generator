
package scrambling;

public class B8ZSDescrambler implements Descrambler {

    public int[] descramble(int[] signal) {

        if (signal == null) {
            throw new IllegalArgumentException(
                    "Signal cannot be null.");
        }

        int[] result = signal.clone();

        for (int i = 0; i + 7 < result.length; i++) {

            // B8ZS starts with three zeros.
            if (result[i] != 0 ||
                    result[i + 1] != 0 ||
                    result[i + 2] != 0) {
                continue;
            }

            int p = result[i + 3];

            // Match 000+-0-+ or 000-+0+-.
            boolean positivePattern = p == 1 &&
                    result[i + 4] == -1 &&
                    result[i + 5] == 0 &&
                    result[i + 6] == -1 &&
                    result[i + 7] == 1;

            boolean negativePattern = p == -1 &&
                    result[i + 4] == 1 &&
                    result[i + 5] == 0 &&
                    result[i + 6] == 1 &&
                    result[i + 7] == -1;

            if (positivePattern || negativePattern) {

                // Restore the original eight zeros.
                for (int j = i; j < i + 8; j++) {
                    result[j] = 0;
                }

                i += 7;
            }
        }

        return result;
    }
}
