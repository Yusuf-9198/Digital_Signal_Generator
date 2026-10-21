
package decoding;

public class NRZIDecoder implements LineDecoder {

    @Override
    public String decode(int[] signal) {

        if (signal == null) {
            throw new IllegalArgumentException(
                    "Signal cannot be null.");
        }

        StringBuilder bits = new StringBuilder(signal.length);

        int previousLevel = -1;

        for (int level : signal) {

            if (level != 1 && level != -1) {
                throw new IllegalArgumentException(
                        "Invalid NRZ-I signal level: " + level);
            }

            if (level != previousLevel) {
                bits.append('1');
            } else {
                bits.append('0');
            }

            previousLevel = level;
        }

        return bits.toString();
    }
}
