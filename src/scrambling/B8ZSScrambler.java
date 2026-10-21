
package scrambling;

public class B8ZSScrambler implements Scrambler {

    @Override
    public int[] scramble(int[] amiSignal) {

        if (amiSignal == null) {
            throw new IllegalArgumentException(
                    "AMI signal cannot be null.");
        }

        int[] result = amiSignal.clone();
        int lastPulse = -1;

        for (int i = 0; i < result.length; i++) {

            int level = result[i];

            if (level != -1 && level != 0 && level != 1) {
                throw new IllegalArgumentException(
                        "Invalid AMI level at index " + i);
            }

            if (level != 0) {
                lastPulse = level;
            }

            // Need eight positions for substitution.
            if (i + 7 >= result.length) {
                continue;
            }

            boolean eightZeros = true;

            for (int j = i; j < i + 8; j++) {
                if (result[j] != 0) {
                    eightZeros = false;
                    break;
                }
            }

            if (!eightZeros) {
                continue;
            }

            int p = lastPulse;

            // 000VB0VB
            result[i + 3] = p;
            result[i + 4] = -p;
            result[i + 5] = 0;
            result[i + 6] = -p;
            result[i + 7] = p;

            lastPulse = p;
            i += 7;
        }

        return result;
    }
}
