
package modulation;

public class PCMDecoder implements AnalogDemodulator {

    private final int bitsPerSample;
    private final int levels;

    public PCMDecoder(int bitsPerSample) {
        if (bitsPerSample < 1 || bitsPerSample > 16) {
            throw new IllegalArgumentException(
                    "Bits per sample must be between 1 and 16.");
        }

        this.bitsPerSample = bitsPerSample;
        this.levels = 1 << bitsPerSample;
    }

    public double[] decode(String bitstream) {

        if (bitstream == null || bitstream.isEmpty()) {
            throw new IllegalArgumentException(
                    "Bitstream cannot be null or empty.");
        }

        if (bitstream.length() % bitsPerSample != 0) {
            throw new IllegalArgumentException(
                    "Bitstream length must be a multiple of bits per sample.");
        }

        double[] reconstructedSamples = new double[bitstream.length() / bitsPerSample];

        double stepSize = 2.0 / levels;

        for (int i = 0; i < reconstructedSamples.length; i++) {

            int start = i * bitsPerSample;
            int end = start + bitsPerSample;

            String binaryGroup = bitstream.substring(start, end);

            // Validate the binary group.
            if (!binaryGroup.matches("[01]+")) {
                throw new IllegalArgumentException(
                        "Bitstream must contain only 0 and 1.");
            }

            // Convert binary group to quantization index.
            int index = Integer.parseInt(binaryGroup, 2);

            // Reconstruct the midpoint of the quantization interval.
            reconstructedSamples[i] = -1.0 + (index + 0.5) * stepSize;
        }

        return reconstructedSamples;
    }
}
