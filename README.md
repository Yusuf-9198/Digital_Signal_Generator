# 📡 Digital Signal Generator & Data Communication Analyzer

![Java](https://img.shields.io/badge/Java-17%2B%20%7C%2023-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

![GUI](https://img.shields.io/badge/GUI-Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)

![Algorithm](https://img.shields.io/badge/Palindrome-Manacher's%20O\(n\)-2EA44F?style=for-the-badge)

![Tests](https://img.shields.io/badge/Unit%20Tests-20%20Passing-brightgreen?style=for-the-badge)A modular Java engineering toolkit built for **ITT 036 (Data Communication)**. This system simulates the complete Physical Layer transmission pipeline: generating digital or analog signals, finding longest palindromic sequences in linear time, applying line coding and bipolar scrambling, visualizing waveforms in a custom Swing GUI, and reconstructing data at the receiver directly from signal voltage parameters.

---

## 🔄 System Pipeline

```text
[ Analog Input ] ──► ( PCM / Delta Modulation ) ──┐
                                                  ▼
[ Digital Input ] ──► ( Random / 4-Zero / 8-Zero ) ──► [ Bitstream ] ──► [ Manacher's O(n) Palindrome ]
                                                            │
                                                            ▼
                                                   ( Line Encoding )
                                        [ NRZ-L | NRZ-I | Manchester | Diff. Manch | AMI ]
                                                            │
                                                            ▼
                                               ( Optional AMI Scrambling )
                                                    [ B8ZS | HDB3 ]
                                                            │
                                      ┌─────────────────────┴─────────────────────┐
                                      ▼                                           ▼
                        [ Java Swing Waveform Plotter ]             [ Signal-Parameter Decoder ]
                         • Digital & Scrambled Pulses                • Descrambling (B8ZS / HDB3)
                         • PCM / DM Staircase Overlay                • Voltage & Edge Analysis
                         • Baud, DC & Transition Metrics             • Round-Trip Bit Verification
```

---

## ✨ Key Features

### 1. Digital Data Generation & $O(n)$ Analysis

- **Flexible Bitstream Modes**: Generates purely random bitstreams, sequences seeded with $4$ or $8$ consecutive zeros (to stress-test scramblers), or custom user-defined binary strings.
- **Linear-Time Palindrome Search**: Implements **Manacher's Algorithm** to locate the longest palindromic substring in strict $O(n)$ time and $O(n)$ space, avoiding $O(n^2)$ dynamic programming bottlenecks.

### 2. Analog-to-Digital Modulation

- **Pulse Code Modulation (PCM)**: Performs uniform midpoint quantization with configurable bit depth ($1\\text{–}16\\text{ bits/sample}$), converts samples to binary streams, and reconstructs the staircase analog waveform.
- **Delta Modulation (DM)**: Tracks analog waveforms using a $1\\text{-bit}$ differential staircase modulator and demodulator with customizable step size ($\\Delta$) and Mean Squared Error (MSE) tracking.

### 3. Line Coding & Bipolar Scrambling

Full encoding and waveform-level decoding support across five standard schemes and two zero-substitution protocols:

| Scheme | Category | Signal Levels | Clock Recovery | Key Encoding Rule |
| --- | --- | --- | --- | --- |
| **NRZ-L** | Unipolar / Polar | $+V, -V$ | Poor | `0` $\\rightarrow +V$, `1` $\\rightarrow -V$ (level-based) |
| **NRZ-I** | Polar | $+V, -V$ | Poor on `0`s | `1` inverts voltage level; `0` holds previous level |
| **Manchester** | Biphase (IEEE 802.3) | $+V, -V$ | Excellent | `0` $\\rightarrow$ High-to-Low; `1` $\\rightarrow$ Low-to-High mid-bit |
| **Diff. Manchester** | Biphase (IEEE 802.5) | $+V, -V$ | Excellent | Always transitions mid-bit; `0` transitions at bit start |
| **AMI** | Bipolar | $+V, 0, -V$ | Poor on `0`s | `0` $\\rightarrow 0\\text{V}$; `1` alternates between $+V$ and $-V$ |

#### Scrambling Protocols (for AMI)

To prevent loss of clock synchronization during long strings of zeros without introducing a DC component:

| Protocol | Standard | Trigger | Substitution Pattern | Rule Breakdown |
| --- | --- | --- | --- | --- |
| **B8ZS** | North America (T1) | `00000000` (8 zeros) | `000VB0VB` | **Prev** `+`: `0 0 0 + - 0 - +`<br>**Prev** `-`: `0 0 0 - + 0 + -` |
| **HDB3** | Europe / Japan (E1) | `0000` (4 zeros) | `000V` or `B00V` | **Odd pulses since last sub**: `0 0 0 V`<br>**Even pulses since last sub**: `B 0 0 V` |

*(Note: **V** = Bipolar Violation matching the previous non-zero pulse polarity; **B** = Valid Bipolar pulse alternating polarity).*

### 4. True Signal-Parameter Receiver Decoding

Rather than reading cached input bits, all decoders (`NRZLDecoder`, `NRZIDecoder`, `ManchesterDecoder`, `DifferentialManchesterDecoder`, `AMIDecoder`, `B8ZSDescrambler`, `HDB3Descrambler`) reconstruct the bitstream by inspecting **physical signal array parameters**—analyzing voltage amplitudes, mid-bit transitions, and bipolar violation patterns.

### 5. Physical Layer Metrics & Swing Visualization

- **Live Telemetry**: Computes Baud Rate ($S = N / r$), DC Component (average voltage shift), Transition Density, and Maximum Consecutive Zero Run-Length.
- **Interactive GUI Plotter**: Renders multi-panel waveforms with zero-reference baselines, sample index markers, and analog staircase overlays using Java Swing.

---

## 🗂 Project Structure

```text
Digital_Signal_Generator/
├── src/
│   ├── Main.java                                  # Interactive CLI controller & pipeline router
│   │
│   ├── data/                                      # Bitstream Generation & String Algorithms
│   │   ├── DataType.java                          # Modes: RANDOM, FOUR_ZEROS, EIGHT_ZEROS
│   │   ├── DigitalDataGenerator.java              # Configurable binary sequence generator
│   │   └── PalindromeFinder.java                  # O(n) Manacher's longest palindrome engine
│   │
│   ├── encoding/                                  # Physical Layer Line Encoders
│   │   ├── LineEncoder.java                       # Common encoder interface
│   │   ├── NRZL.java                              # Non-Return to Zero Level
│   │   ├── NRZI.java                              # Non-Return to Zero Invert
│   │   ├── Manchester.java                        # IEEE 802.3 Biphase encoder
│   │   ├── DifferentialManchester.java            # IEEE 802.5 Differential Biphase encoder
│   │   └── AMI.java                               # Alternate Mark Inversion encoder
│   │
│   ├── decoding/                                  # Signal-Parameter Receivers
│   │   ├── LineDecoder.java                       # Common decoder interface
│   │   ├── NRZLDecoder.java                       # Voltage-level threshold decoder
│   │   ├── NRZIDecoder.java                       # Boundary transition detector
│   │   ├── ManchesterDecoder.java                 # Mid-bit edge direction analyzer
│   │   ├── DifferentialManchesterDecoder.java     # Boundary + mid-bit state decoder
│   │   └── AMIDecoder.java                        # Bipolar pulse & violation detector
│   │
│   ├── scrambling/                                # Zero-Suppression Scramblers
│   │   ├── Scrambler.java                         # Common scrambler interface
│   │   ├── B8ZS.java                              # 8-zero substitution encoder
│   │   ├── B8ZSDescrambler.java                   # 000VB0VB violation remover
│   │   ├── HDB3.java                              # 4-zero parity-aware encoder
│   │   └── HDB3Descrambler.java                   # 000V / B00V violation remover
│   │
│   ├── modulation/                                # Analog-to-Digital Converters
│   │   ├── PCMEncoder.java                        # Uniform quantizer & binary encoder
│   │   ├── PCMDecoder.java                        # Midpoint staircase reconstructor
│   │   ├── DeltaModulator.java                    # 1-bit step tracker
│   │   └── DeltaDemodulator.java                  # Delta staircase integrator
│   │
│   ├── visualization/                             # Graphical User Interface
│   │   └── SignalPlotter.java                     # Custom Java Swing waveform renderer
│   │
│   └── [Test Suites]                              # Automated Verification Runners
│       ├── ProjectTest.java                       # Master suite (20 end-to-end assertions)
│       ├── EncodingDecoderTest.java               # Round-trip line coding checks
│       ├── B8ZSKnownCasesTest.java                # B8ZS polarity & edge-case tests
│       ├── HDB3KnownCasesTest.java                # HDB3 even/odd parity tests
│       ├── ScramblingTest.java                    # Long zero-run suppression benchmarks
│       ├── DeltaModulationTest.java               # DM staircase & MSE verification
│       └── SignalPlotterTest.java                 # Visual GUI rendering test
│
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher (tested up to JDK 23).
- No external third-party dependencies required (uses native Java `javax.swing` and `java.awt`).

### 1. Compile the Project

**Linux / macOS / WSL (Bash):**

```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

**Windows (PowerShell):**

```powershell
New-Item -ItemType Directory -Force -Path bin | Out-Null
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### 2. Launch the Interactive Analyzer

```bash
java -cp bin Main
```

### 3. Run Automated Verification Suites

Execute the master test runner (20 automated checks) or individual module tests:

```bash
# Master Test Suite
java -cp bin ProjectTest

# Module-Specific Tests
java -cp bin EncodingDecoderTest
java -cp bin B8ZSKnownCasesTest
java -cp bin HDB3KnownCasesTest
java -cp bin ScramblingTest
java -cp bin DeltaModulationTest
java -cp bin SignalPlotterTest
```

---

## 🖥️ Sample CLI Session

```text
==============================================
   DIGITAL SIGNAL GENERATOR & ANALYZER
==============================================

Select input type:
1. Digital bitstream
2. Analog samples (PCM / Delta Modulation)
Choice (1-2): 1

Select digital data source:
1. Completely random
2. Random with four consecutive zeros
3. Random with eight consecutive zeros
4. Enter custom binary bitstream
Choice (1-4): 4
Enter binary bitstream (e.g. 1011000000001): 11000000001

==============================================
   DIGITAL BITSTREAM & PALINDROME ANALYSIS
==============================================
Bitstream:          11000000001
Bit count (N):      11
Longest palindrome: 1000000001
Palindrome length:  10

Select line-coding scheme:
1. NRZ-L (Non-Return to Zero Level)
2. NRZ-I (Non-Return to Zero Invert on 1)
3. Manchester (Biphase)
4. Differential Manchester
5. AMI (Alternate Mark Inversion)
Choice (1-5): 5

Apply scrambling (to eliminate long zero runs)?
1. No scrambling
2. B8ZS (Bipolar with 8-Zero Substitution)
3. HDB3 (High-Density Bipolar 3-Zero)
Choice (1-3): 2

==============================================
   TRANSMISSION & SIGNAL METRICS
==============================================
Scheme:                  AMI + B8ZS
Transmitted signal:      [1, -1, 0, 0, 0, -1, 1, 0, 1, -1, 1]
Signal element count:    11
Signal elements per bit: 1.00 (r = 1.00)
Baud Rate:               1.00 * BitRate (N)
DC Component (avg level):0.0909 V (Non-zero DC)
Signal Transitions:      7 / 11 intervals (density: 0.64)
Max zero run in data:    8 zeros

==============================================
   RECEIVER DECODING & VERIFICATION
==============================================
Decoded bitstream:   11000000001
Round-trip status:   PASS (Exact match)
```