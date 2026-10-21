
package decoding;

public class ManchesterDecoder implements LineDecoder {

    @Override
    public String decode(int[] signal) {

        if (signal == null) {
            throw new IllegalArgumentException(
                    "Signal cannot be null.");
        }

        if (signal.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Manchester signal length must be even.");
        }

        StringBuilder bits = new StringBuilder();

        for (int i = 0; i < signal.length; i += 2) {

            int first = signal[i];
            int second = signal[i + 1];

            if (first == 1 && second == -1) {
                bits.append('0');
            } else if (first == -1 && second == 1) {
                bits.append('1');
            } else {
                throw new IllegalArgumentException(
                        "Invalid Manchester pair at index " + i);
            }
        }

        return bits.toString();
    }
}
