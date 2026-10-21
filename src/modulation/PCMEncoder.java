
package modulation;

public class PCMEncoder implements AnalogModulator {

    private final int bitsPerSample;
    private final int levels;

    public PCMEncoder(int bitsPerSample) {
        if (bitsPerSample < 1 || bitsPerSample > 16) {
            throw new IllegalArgumentException(
                    "Bits per sample must be between 1 and 16.");
        }

        this.bitsPerSample = bitsPerSample;
        this.levels = 1 << bitsPerSample;
    }

    public String encode(double[] samples) {
        if (samples == null || samples.length == 0) {
            throw new IllegalArgumentException(
                    "Samples cannot be null or empty.");
        }

        StringBuilder bitstream = new StringBuilder();

        for (double sample : samples) {

            if (!Double.isFinite(sample)
                    || sample < -1.0 || sample > 1.0) {
                throw new IllegalArgumentException(
                        "Each sample must be finite and in [-1, 1].");
            }

            // Map the sample to a quantization level.
            int index = (int) Math.floor(
                    ((sample + 1.0) / 2.0) * levels);

            // Handle the upper endpoint sample = 1.0.
            index = Math.min(index, levels - 1);

            // Convert the level index into fixed-width binary.
            String binary = Integer.toBinaryString(index);

            bitstream.append(
                    "0".repeat(bitsPerSample - binary.length()));
            bitstream.append(binary);
        }

        return bitstream.toString();
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getLevels() {
        return levels;
    }
}
