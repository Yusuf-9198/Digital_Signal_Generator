import java.util.Arrays;
import modulation.DeltaModulator;
import modulation.DeltaDemodulator;

public class DeltaModulationTest {

    public static void main(String[] args) {
        double[] samples = {
                0.1, 0.3, 0.6, 0.4, 0.0, -0.3, -0.6, -0.2, 0.4
        };

        double stepSize = 0.2;

        DeltaModulator encoder = new DeltaModulator(stepSize);
        DeltaDemodulator decoder = new DeltaDemodulator(stepSize);

        String bits = encoder.encode(samples);
        double[] reconstructed = decoder.decode(bits);

        System.out.println("Original samples:");
        System.out.println(Arrays.toString(samples));

        System.out.println("\nDelta-modulated bitstream:");
        System.out.println(bits);

        System.out.println("\nReconstructed staircase:");
        System.out.println(Arrays.toString(reconstructed));

        // Compute quantization metrics
        double maxError = 0.0;
        double sumSqError = 0.0;
        for (int i = 0; i < samples.length; i++) {
            double err = Math.abs(samples[i] - reconstructed[i]);
            maxError = Math.max(maxError, err);
            sumSqError += err * err;
        }
        double mse = sumSqError / samples.length;

        System.out.printf("%nMaximum Absolute Error: %.4f%n", maxError);
        System.out.printf("Mean Squared Error (MSE): %.4f%n", mse);

        boolean passed = bits.length() == samples.length && reconstructed.length == samples.length;
        System.out.println("Test Status: " + (passed ? "PASS" : "FAIL"));
    }
}
