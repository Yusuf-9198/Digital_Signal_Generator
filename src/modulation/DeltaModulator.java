
package modulation;

public class DeltaModulator implements AnalogModulator {

    private final double stepSize;

    public DeltaModulator(double stepSize) {
        if (!Double.isFinite(stepSize) || stepSize <= 0.0) {
            throw new IllegalArgumentException(
                    "Step size must be positive and finite.");
        }

        this.stepSize = stepSize;
    }

    public String encode(double[] samples) {
        if (samples == null || samples.length == 0) {
            throw new IllegalArgumentException(
                    "Samples cannot be null or empty.");
        }

        StringBuilder bits = new StringBuilder(samples.length);
        double previousReconstruction = 0.0;

        for (double sample : samples) {
            if (!Double.isFinite(sample)
                    || sample < -1.0 || sample > 1.0) {
                throw new IllegalArgumentException(
                        "Each sample must be finite and in [-1, 1].");
            }

            if (sample >= previousReconstruction) {
                bits.append('1');
                previousReconstruction += stepSize;
            } else {
                bits.append('0');
                previousReconstruction -= stepSize;
            }

            // Keep the reconstructed value in the supported range.
            previousReconstruction = Math.max(
                    -1.0, Math.min(1.0, previousReconstruction));
        }

        return bits.toString();
    }

    public double getStepSize() {
        return stepSize;
    }
}
