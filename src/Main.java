import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

import data.DataType;
import data.DigitalDataGenerator;
import data.PalindromeFinder;

import decoding.AMIDecoder;
import decoding.DifferentialManchesterDecoder;
import decoding.LineDecoder;
import decoding.ManchesterDecoder;
import decoding.NRZIDecoder;
import decoding.NRZLDecoder;

import encoding.AMIEncoder;
import encoding.DifferentialManchesterEncoder;
import encoding.LineEncoder;
import encoding.ManchesterEncoder;
import encoding.NRZIEncoder;
import encoding.NRZLEncoder;

import modulation.AnalogDemodulator;
import modulation.AnalogModulator;
import modulation.DeltaDemodulator;
import modulation.DeltaModulator;
import modulation.PCMDecoder;
import modulation.PCMEncoder;

import scrambling.B8ZSDescrambler;
import scrambling.B8ZSScrambler;
import scrambling.Descrambler;
import scrambling.HDB3Descrambler;
import scrambling.HDB3Scrambler;
import scrambling.Scrambler;

import visualization.SignalPlotter;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;

            while (running) {
                System.out.println("\n==============================================");
                System.out.println("   DIGITAL SIGNAL GENERATOR & ANALYZER");
                System.out.println("==============================================");

                int inputChoice = readInputChoice(scanner);
                PipelineData pipelineData = createInputPipeline(scanner, inputChoice);

                String bits = pipelineData.bits;
                printBitstreamAnalysis(bits);

                LineCodingOption lineCodingOption = readLineCodingOption(scanner);
                int[] encodedSignal = lineCodingOption.encoder.encode(bits);
                int[] transmittedSignal = encodedSignal.clone();

                ScramblingOption appliedScrambler = selectScramblingIfNeeded(scanner, lineCodingOption, encodedSignal);
                if (appliedScrambler != null) {
                    transmittedSignal = appliedScrambler.scrambler.scramble(encodedSignal);
                }

                String selectedScheme = (appliedScrambler == null)
                        ? lineCodingOption.name
                        : lineCodingOption.name + " + " + appliedScrambler.name;

                printTransmissionMetrics(bits, transmittedSignal, selectedScheme);

                if (readYesNo(scanner, "Do you want to decode the signal? (y/n): ")) {
                    decodeAndVerify(bits, lineCodingOption, transmittedSignal, appliedScrambler);
                }

                SignalPlotter.plotDigital(selectedScheme + " Signal", transmittedSignal);

                if (pipelineData.originalAnalogSamples != null) {
                    SignalPlotter.plotPCM("Original Analog Signal", pipelineData.originalAnalogSamples);
                    SignalPlotter.plotPCM(pipelineData.reconstructionTitle, pipelineData.reconstructedSamples);
                }

                System.out.println("\n----------------------------------------------");
                System.out.println("Select next action:");
                System.out.println("1. Analyze another signal");
                System.out.println("2. Exit");
                int nextAction = readInt(scanner, "Choice (1-2): ", 1, 2);
                running = nextAction == 1;
                if (!running) {
                    System.out.println("\nExiting Digital Signal Generator. Thank you!");
                }
            }

        } catch (Exception e) {
            System.err.println("\nUnexpected application error: " + e.getMessage());
        }
    }

    private static int readInputChoice(Scanner scanner) {
        System.out.println("\nSelect input type:");
        System.out.println("1. Digital bitstream");
        System.out.println("2. Analog samples (PCM / Delta Modulation)");
        return readInt(scanner, "Choice (1-2): ", 1, 2);
    }

    private static PipelineData createInputPipeline(Scanner scanner, int inputChoice) {
        Map<Integer, Supplier<PipelineData>> pipelines = new HashMap<>();
        pipelines.put(1, () -> createDigitalPipeline(scanner));
        pipelines.put(2, () -> createAnalogPipeline(scanner));
        return pipelines.get(inputChoice).get();
    }

    private static PipelineData createDigitalPipeline(Scanner scanner) {
        System.out.println("\nSelect digital data source:");
        System.out.println("1. Completely random");
        System.out.println("2. Random with four consecutive zeros");
        System.out.println("3. Random with eight consecutive zeros");
        System.out.println("4. Enter custom binary bitstream");

        int dataChoice = readInt(scanner, "Choice (1-4): ", 1, 4);
        DigitalDataGenerator generator = new DigitalDataGenerator();

        Map<Integer, Supplier<String>> dataSources = new HashMap<>();
        dataSources.put(1, () -> generateDataForType(scanner, generator, DataType.RANDOM));
        dataSources.put(2, () -> generateDataForType(scanner, generator, DataType.FOUR_ZEROS));
        dataSources.put(3, () -> generateDataForType(scanner, generator, DataType.EIGHT_ZEROS));
        dataSources.put(4, () -> readBitstream(scanner, "Enter binary bitstream (e.g. 1011000000001): "));

        return new PipelineData(dataSources.get(dataChoice).get(), null, null, null);
    }

    private static String generateDataForType(Scanner scanner, DigitalDataGenerator generator, DataType type) {
        int length = readInt(scanner, "Enter bit sequence length (minimum 8): ", 8, 10000);
        return generator.generate(length, type);
    }

    private static PipelineData createAnalogPipeline(Scanner scanner) {
        System.out.println("\nSelect analog modulation scheme:");
        System.out.println("1. Pulse Code Modulation (PCM)");
        System.out.println("2. Delta Modulation (DM)");
        int modulationChoice = readInt(scanner, "Choice (1-2): ", 1, 2);

        int sampleCount = readInt(scanner, "Enter number of analog samples (>= 1): ", 1, 1000);
        double[] analogSamples = new double[sampleCount];

        System.out.println("Enter sample values in range [-1.0, 1.0]:");
        for (int i = 0; i < sampleCount; i++) {
            analogSamples[i] = readDouble(scanner, String.format("  Sample [%d]: ", i), -1.0, 1.0);
        }

        Map<Integer, Supplier<PipelineData>> analogPipelines = new HashMap<>();
        analogPipelines.put(1, () -> runPCM(scanner, analogSamples));
        analogPipelines.put(2, () -> runDeltaModulation(scanner, analogSamples));

        return analogPipelines.get(modulationChoice).get();
    }

    private static PipelineData runPCM(Scanner scanner, double[] analogSamples) {
        int bitsPerSample = readInt(scanner, "Enter PCM bits per sample (1-16): ", 1, 16);
        AnalogModulator modulator = new PCMEncoder(bitsPerSample);
        AnalogDemodulator demodulator = new PCMDecoder(bitsPerSample);

        String bits = modulator.encode(analogSamples);
        double[] reconstructed = demodulator.decode(bits);

        PCMEncoder typedEncoder = (PCMEncoder) modulator;
        System.out.println("\n========== PCM MODULATION ==========");
        System.out.println("Bits per sample:      " + typedEncoder.getBitsPerSample());
        System.out.println("Quantization levels:  " + typedEncoder.getLevels());
        System.out.println("Digital bitstream:    " + bits);
        System.out.println("Reconstructed samples:" + Arrays.toString(reconstructed));

        return new PipelineData(bits, analogSamples, reconstructed, "PCM Reconstructed Signal");
    }

    private static PipelineData runDeltaModulation(Scanner scanner, double[] analogSamples) {
        double stepSize = readDouble(scanner, "Enter DM step size delta (0.01 - 1.0, e.g. 0.2): ", 0.0001, 1.0);
        AnalogModulator modulator = new DeltaModulator(stepSize);
        AnalogDemodulator demodulator = new DeltaDemodulator(stepSize);

        String bits = modulator.encode(analogSamples);
        double[] reconstructed = demodulator.decode(bits);

        System.out.println("\n===== DELTA MODULATION =====");
        System.out.println("Step size (delta):    " + stepSize);
        System.out.println("Digital bitstream:    " + bits);
        System.out.println("Reconstructed samples:" + Arrays.toString(reconstructed));

        return new PipelineData(bits, analogSamples, reconstructed, "Delta Modulation Reconstructed Signal");
    }

    private static void printBitstreamAnalysis(String bits) {
        System.out.println("\n==============================================");
        System.out.println("   DIGITAL BITSTREAM & PALINDROME ANALYSIS");
        System.out.println("==============================================");
        System.out.println("Bitstream:       " + bits);
        System.out.println("Bit count (N):   " + bits.length());

        String palindrome = PalindromeFinder.findLongestPalindrome(bits);
        System.out.println("Longest palindrome: " + palindrome);
        System.out.println("Palindrome length:  " + palindrome.length());
    }

    private static LineCodingOption readLineCodingOption(Scanner scanner) {
        System.out.println("\nSelect line-coding scheme:");
        System.out.println("1. NRZ-L (Non-Return to Zero Level)");
        System.out.println("2. NRZ-I (Non-Return to Zero Invert on 1)");
        System.out.println("3. Manchester (Biphase)");
        System.out.println("4. Differential Manchester");
        System.out.println("5. AMI (Alternate Mark Inversion)");

        int schemeChoice = readInt(scanner, "Choice (1-5): ", 1, 5);

        Map<Integer, LineCodingOption> lineCodingOptions = new HashMap<>();
        lineCodingOptions.put(1, new LineCodingOption("NRZ-L", new NRZLEncoder(), new NRZLDecoder()));
        lineCodingOptions.put(2, new LineCodingOption("NRZ-I", new NRZIEncoder(), new NRZIDecoder()));
        lineCodingOptions.put(3, new LineCodingOption("Manchester", new ManchesterEncoder(), new ManchesterDecoder()));
        lineCodingOptions.put(4, new LineCodingOption("Differential Manchester", new DifferentialManchesterEncoder(), new DifferentialManchesterDecoder()));
        lineCodingOptions.put(5, new LineCodingOption("AMI", new AMIEncoder(), new AMIDecoder()));

        return lineCodingOptions.get(schemeChoice);
    }

    private static ScramblingOption selectScramblingIfNeeded(
            Scanner scanner,
            LineCodingOption lineCodingOption,
            int[] encodedSignal) {

        if (!"AMI".equals(lineCodingOption.name)) {
            return null;
        }

        System.out.println("\nApply scrambling (to eliminate long zero runs)?");
        System.out.println("1. No scrambling");
        System.out.println("2. B8ZS (Bipolar with 8-Zero Substitution)");
        System.out.println("3. HDB3 (High-Density Bipolar 3-Zero)");

        int scrambleChoice = readInt(scanner, "Choice (1-3): ", 1, 3);

        Map<Integer, ScramblingOption> scramblingOptions = new HashMap<>();
        scramblingOptions.put(2, new ScramblingOption("B8ZS", new B8ZSScrambler(), new B8ZSDescrambler()));
        scramblingOptions.put(3, new ScramblingOption("HDB3", new HDB3Scrambler(), new HDB3Descrambler()));

        return scramblingOptions.get(scrambleChoice);
    }

    private static void decodeAndVerify(
            String originalBits,
            LineCodingOption lineCodingOption,
            int[] transmittedSignal,
            ScramblingOption appliedScrambler) {

        int[] signalForDecoding = transmittedSignal.clone();
        if (appliedScrambler != null) {
            signalForDecoding = appliedScrambler.descrambler.descramble(signalForDecoding);
        }

        String decodedBits = lineCodingOption.decoder.decode(signalForDecoding);

        System.out.println("\n==============================================");
        System.out.println("   RECEIVER DECODING & VERIFICATION");
        System.out.println("==============================================");
        System.out.println("Decoded bitstream:   " + decodedBits);
        System.out.println("Original bitstream:  " + originalBits);
        boolean passed = originalBits.equals(decodedBits);
        System.out.println("Round-trip status:   " + (passed ? "PASS (Exact match)" : "FAIL (Mismatch)"));
    }

    private static void printTransmissionMetrics(String bits, int[] signal, String scheme) {
        System.out.println("\n==============================================");
        System.out.println("   TRANSMISSION & SIGNAL METRICS");
        System.out.println("==============================================");
        System.out.println("Scheme:              " + scheme);
        System.out.println("Transmitted signal:  " + Arrays.toString(signal));
        System.out.println("Signal element count:" + signal.length);

        int n = bits.length();
        int m = signal.length;

        double r = (double) n / m;
        System.out.printf("Signal elements per bit: %.2f (r = %.2f)%n", (double) m / n, r);
        System.out.printf("Baud Rate:               %.2f * BitRate (N)%n", (double) m / n);

        double sum = 0;
        for (int level : signal) {
            sum += level;
        }
        double dcComponent = sum / m;
        System.out.printf("DC Component (avg level): %.4f V %s%n",
                dcComponent,
                Math.abs(dcComponent) < 1e-6 ? "(DC Balanced / Zero DC)" : "(Non-zero DC)");

        int transitions = 0;
        for (int i = 0; i < m - 1; i++) {
            if (signal[i] != signal[i + 1]) {
                transitions++;
            }
        }
        double transitionDensity = (double) transitions / m;
        System.out.printf("Signal Transitions:      %d / %d intervals (density: %.2f)%n",
                transitions,
                m,
                transitionDensity);

        System.out.println("Max zero run in data:    " + maxZeroRun(bits) + " zeros");
    }

    private static int maxZeroRun(String s) {
        int max = 0;
        int curr = 0;
        for (char c : s.toCharArray()) {
            if (c == '0') {
                curr++;
                max = Math.max(max, curr);
            } else {
                curr = 0;
            }
        }
        return max;
    }

    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter an integer between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double readDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                double val = Double.parseDouble(line);
                if (Double.isFinite(val) && val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a finite number between %.4f and %.4f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number.");
            }
        }
    }

    private static String readBitstream(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.matches("[01]+")) {
                return line;
            }
            System.out.println("Invalid bitstream. Input must be non-empty and contain only '0' and '1'.");
        }
    }

    private static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(line) || "yes".equals(line)) {
                return true;
            }
            if ("n".equals(line) || "no".equals(line)) {
                return false;
            }
            System.out.println("Please enter y or n.");
        }
    }

    private static class PipelineData {
        private final String bits;
        private final double[] originalAnalogSamples;
        private final double[] reconstructedSamples;
        private final String reconstructionTitle;

        private PipelineData(String bits, double[] originalAnalogSamples, double[] reconstructedSamples, String reconstructionTitle) {
            this.bits = bits;
            this.originalAnalogSamples = originalAnalogSamples;
            this.reconstructedSamples = reconstructedSamples;
            this.reconstructionTitle = reconstructionTitle;
        }
    }

    private static class LineCodingOption {
        private final String name;
        private final LineEncoder encoder;
        private final LineDecoder decoder;

        private LineCodingOption(String name, LineEncoder encoder, LineDecoder decoder) {
            this.name = name;
            this.encoder = encoder;
            this.decoder = decoder;
        }
    }

    private static class ScramblingOption {
        private final String name;
        private final Scrambler scrambler;
        private final Descrambler descrambler;

        private ScramblingOption(String name, Scrambler scrambler, Descrambler descrambler) {
            this.name = name;
            this.scrambler = scrambler;
            this.descrambler = descrambler;
        }
    }
}
