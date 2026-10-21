
import visualization.SignalPlotter;
import modulation.PCMEncoder;
import modulation.PCMDecoder;
import modulation.DeltaModulator;
import modulation.DeltaDemodulator;
import encoding.ManchesterEncoder;

public class SignalPlotterTest {

    public static void main(String[] args) {

        // Test 1: Plot a Manchester-coded digital signal.
        String bits = "10110";
        int[] digitalSignal = new ManchesterEncoder().encode(bits);

        SignalPlotter.plotDigital(
                "Manchester Encoding", digitalSignal);

        // Test 2: Plot the reconstructed PCM signal.
        double[] analogSamples = {
                -1.0, -0.5, 0.0, 0.5, 1.0,
                0.5, 0.0, -0.5, -1.0
        };

        PCMEncoder encoder = new PCMEncoder(3);
        PCMDecoder decoder = new PCMDecoder(3);

        String pcmBits = encoder.encode(analogSamples);
        double[] reconstructed = decoder.decode(pcmBits);

        SignalPlotter.plotPCM(
                "PCM Reconstruction", reconstructed);

        // Test 3: Plot the Delta Modulation signal.
        DeltaModulator dmEncoder = new DeltaModulator(0.25);
        DeltaDemodulator dmDecoder = new DeltaDemodulator(0.25);

        String dmBits = dmEncoder.encode(analogSamples);
        double[] dmReconstructed = dmDecoder.decode(dmBits);
        
        SignalPlotter.plotPCM(
                "Delta Modulation Reconstruction", dmReconstructed);
    }
}
