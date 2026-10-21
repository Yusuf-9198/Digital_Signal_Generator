
package modulation;

public class DeltaDemodulator implements AnalogDemodulator {

    private final double stepSize;

    public DeltaDemodulator(double stepSize) {
        if (!Double.isFinite(stepSize) || stepSize <= 0.0) {
            throw new IllegalArgumentException(
                    "Step size must be positive and finite.");
        }

        this.stepSize = stepSize;
    }

    public double[] decode(String bitstream) {
        if (bitstream == null || bitstream.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bitstream cannot be null or empty.");
        }

        double[] reconstructed = new double[bitstream.length()];
        double previousReconstruction = 0.0;

        for (int i = 0; i < bitstream.length(); i++) {
            char bit = bitstream.charAt(i);

            if (bit == '1') {
                previousReconstruction += stepSize;
            } else if (bit == '0') {
                previousReconstruction -= stepSize;
            } else {
                throw new IllegalArgumentException(
                        "Bitstream must contain only 0 and 1.");
            }

            previousReconstruction = Math.max(
                    -1.0, Math.min(1.0, previousReconstruction));

            reconstructed[i] = previousReconstruction;
        }

        return reconstructed;
    }
}
