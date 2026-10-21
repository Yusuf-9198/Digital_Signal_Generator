
package scrambling;

public class HDB3Descrambler implements Descrambler {

    public int[] descramble(int[] signal) {
        if (signal == null) {
            throw new IllegalArgumentException(
                    "Input signal cannot be null.");
        }

        int n = signal.length;

        // Validate the signal values.
        for (int i = 0; i < n; i++) {
            if (signal[i] < -1 || signal[i] > 1) {
                throw new IllegalArgumentException(
                        "Signal values must be -1, 0, or 1.");
            }
        }

        // Preserve the original signal for pattern detection.
        int[] result = signal.clone();

        /*
         * First pass: detect B00V.
         *
         * B must have normal AMI polarity relative to the previous
         * transmitted nonzero pulse, and V must have the same polarity
         * as B.
         */
        for (int i = 0; i + 3 < n; i++) {
            int b = signal[i];

            if (b == 0
                    || signal[i + 1] != 0
                    || signal[i + 2] != 0
                    || signal[i + 3] != b) {
                continue;
            }

            int previousPulse = 0;

            for (int j = i - 1; j >= 0; j--) {
                if (signal[j] != 0) {
                    previousPulse = signal[j];
                    break;
                }
            }

            int expectedB = (previousPulse == 0)
                    ? 1
                    : -previousPulse;

            if (b == expectedB) {
                result[i] = 0;
                result[i + 3] = 0;
                i += 3;
            }
        }

        /*
         * Second pass: detect 000V.
         *
         * V has the same polarity as the preceding nonzero transmitted
         * pulse. Remove V, leaving the original four data zeros.
         */
        for (int i = 0; i + 3 < n; i++) {
            if (signal[i] != 0
                    || signal[i + 1] != 0
                    || signal[i + 2] != 0
                    || signal[i + 3] == 0) {
                continue;
            }

            int v = signal[i + 3];
            int previousPulse = 0;

            for (int j = i - 1; j >= 0; j--) {
                if (signal[j] != 0) {
                    previousPulse = signal[j];
                    break;
                }
            }

            if (previousPulse != 0 && v == previousPulse) {
                result[i + 3] = 0;
                i += 3;
            }
        }

        /*
         * Third pass: restore ordinary AMI polarity.
         *
         * Once substitution pulses are removed, every remaining nonzero
         * pulse represents a data 1. Rebuild the alternating AMI levels.
         */
        int lastPulse = 0;

        for (int i = 0; i < n; i++) {
            if (result[i] != 0) {
                lastPulse = (lastPulse == 0) ? 1 : -lastPulse;
                result[i] = lastPulse;
            }
        }

        return result;
    }
}
