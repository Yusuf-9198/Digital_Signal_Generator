
package encoding;

public class AMIEncoder implements LineEncoder {

    @Override
    public int[] encode(String bits) {

        if (bits == null) {
            throw new IllegalArgumentException(
                "Input bits cannot be null."
            );
        }

        int[] signal = new int[bits.length()];

        int polarity = 1; // First 1 produces +1

        for (int i = 0; i < bits.length(); i++) {

            char bit = bits.charAt(i);

            if (bit == '1') {

                signal[i] = polarity;
                polarity = -polarity;

            } else if (bit == '0') {

                signal[i] = 0;

            } else {

                throw new IllegalArgumentException(
                    "Input must contain only 0 and 1."
                );
            }
        }

        return signal;
    }
}
