import modulation.PCMDecoder;
import modulation.PCMEncoder;

public class PCMModulationTest {

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + name);
        } else {
            System.out.println("[FAIL] " + name);
        }
    }

    public static void main(String[] args) {
        double[] samples = { -1.0, -0.6, -0.2, 0.0, 0.2, 0.55, 1.0 };
        int bitsPerSample = 4;

        PCMEncoder encoder = new PCMEncoder(bitsPerSample);
        PCMDecoder decoder = new PCMDecoder(bitsPerSample);

        String bitstream = encoder.encode(samples);
        double[] reconstructed = decoder.decode(bitstream);

        check("PCM bitstream length", bitstream.length() == samples.length * bitsPerSample);
        check("PCM reconstructed sample count", reconstructed.length == samples.length);

        // Quantization tolerance for 4-bit PCM in [-1, 1].
        double tolerance = 2.0 / (1 << bitsPerSample);
        boolean withinTolerance = true;
        for (int i = 0; i < samples.length; i++) {
            if (Math.abs(samples[i] - reconstructed[i]) > tolerance) {
                withinTolerance = false;
                break;
            }
        }
        check("PCM reconstruction within quantization tolerance", withinTolerance);
    }
}
