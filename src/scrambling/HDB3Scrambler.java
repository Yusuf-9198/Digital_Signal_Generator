
package scrambling;

public class HDB3Scrambler implements Scrambler {

    @Override
    public int[] scramble(int[] amiSignal) {
        if (amiSignal == null) {
            throw new IllegalArgumentException("Input signal cannot be null.");
        }

        int n = amiSignal.length;
        int[] result = new int[n];
        boolean[] isOne = new boolean[n];

        // Validate that the input is a valid AMI signal and record its 1s.
        int previousInputPulse = 0;

        for (int i = 0; i < n; i++) {
            int value = amiSignal[i];

            if (value < -1 || value > 1) {
                throw new IllegalArgumentException(
                        "Signal values must be -1, 0, or 1.");
            }

            if (value != 0) {
                if (previousInputPulse != 0
                        && value == previousInputPulse) {
                    throw new IllegalArgumentException(
                            "Input is not a valid AMI signal at index " + i);
                }

                isOne[i] = true;
                previousInputPulse = value;
            }
        }

        int lastPulse = 0;
        int pulseCount = 0;
        int i = 0;

        while (i < n) {
            // Find four consecutive input zeros.
            boolean fourZeros = i + 3 < n
                    && !isOne[i]
                    && !isOne[i + 1]
                    && !isOne[i + 2]
                    && !isOne[i + 3];

            if (fourZeros) {
                if (pulseCount % 2 == 1) {
                    // Odd number of pulses: 000V.
                    // V has the same polarity as the previous pulse.
                    if (lastPulse == 0) {
                        throw new IllegalStateException(
                                "Invalid HDB3 state for 000V substitution.");
                    }

                    result[i + 3] = lastPulse;
                } else {
                    // Even number of pulses: B00V.
                    // B follows normal AMI polarity; V violates it.
                    int bPulse = (lastPulse == 0) ? 1 : -lastPulse;

                    result[i] = bPulse;
                    result[i + 3] = bPulse;
                    lastPulse = bPulse;
                }

                pulseCount = 0;
                i += 4;
            } else {
                if (isOne[i]) {
                    // Encode a data 1 using alternating AMI polarity.
                    lastPulse = (lastPulse == 0) ? 1 : -lastPulse;
                    result[i] = lastPulse;
                    pulseCount++;
                }

                i++;
            }
        }

        return result;
    }
}
