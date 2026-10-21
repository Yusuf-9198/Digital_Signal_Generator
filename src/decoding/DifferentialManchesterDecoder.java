
package decoding;

public class DifferentialManchesterDecoder
        implements LineDecoder {

    @Override
    public String decode(int[] signal) {

        if (signal == null) {
            throw new IllegalArgumentException(
                    "Signal cannot be null.");
        }

        if (signal.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Differential Manchester signal length "
                            + "must be even.");
        }

        StringBuilder bits = new StringBuilder();

        int previousLevel = 1; // Same initial reference as encoder

        for (int i = 0; i < signal.length; i += 2) {

            int first = signal[i];
            int second = signal[i + 1];

            if (!validLevel(first) || !validLevel(second)) {
                throw new IllegalArgumentException(
                        "Signal levels must be +1 or -1.");
            }

            // A transition in the middle is mandatory.
            if (first == second) {
                throw new IllegalArgumentException(
                        "Missing mid-bit transition at index " + i);
            }

            // Start transition means 0; no transition means 1.
            if (first != previousLevel) {
                bits.append('0');
            } else {
                bits.append('1');
            }

            previousLevel = second;
        }

        return bits.toString();
    }

    private boolean validLevel(int level) {
        return level == 1 || level == -1;
    }
}
