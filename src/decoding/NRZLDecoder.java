
package decoding;

public class NRZLDecoder implements LineDecoder {

    @Override
    public String decode(int[] signal) {

        if (signal == null) {
            throw new IllegalArgumentException(
                    "Signal cannot be null.");
        }

        StringBuilder bits = new StringBuilder(signal.length);

        for (int level : signal) {

            if (level == 1) {
                bits.append('0');
            } else if (level == -1) {
                bits.append('1');
            } else {
                throw new IllegalArgumentException(
                        "Invalid NRZ-L signal level: " + level);
            }
        }

        return bits.toString();
    }
}
