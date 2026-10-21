
package decoding;

public class AMIDecoder implements LineDecoder {

    @Override
    public String decode(int[] signal) {

        if (signal == null) {
            throw new IllegalArgumentException(
                    "Signal cannot be null.");
        }

        StringBuilder bits = new StringBuilder(signal.length);

        int lastPulse = 0;

        for (int i = 0; i < signal.length; i++) {

            int level = signal[i];

            if (level == 0) {

                bits.append('0');

            } else if (level == 1 || level == -1) {

                if (lastPulse != 0 && level == lastPulse) {
                    throw new IllegalArgumentException(
                            "AMI bipolar violation at index " + i);
                }

                bits.append('1');
                lastPulse = level;

            } else {

                throw new IllegalArgumentException(
                        "Invalid AMI signal level at index " + i);
            }
        }

        return bits.toString();
    }
}
