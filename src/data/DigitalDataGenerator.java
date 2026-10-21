package data;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class DigitalDataGenerator {
    
    private final Random random;
    public DigitalDataGenerator(){
        random = new Random();
    }
    public String generate(int length, DataType type) {
        if (length <= 0) {
            throw new IllegalArgumentException("Data length must be positive.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Data type cannot be null.");
        }

        Map<DataType, GeneratorStrategy> strategies = new EnumMap<>(DataType.class);
        strategies.put(DataType.RANDOM, this::generateRandomData);
        strategies.put(DataType.FOUR_ZEROS, requestedLength -> generateDataWithFixedZeros(requestedLength, 4));
        strategies.put(DataType.EIGHT_ZEROS, requestedLength -> generateDataWithFixedZeros(requestedLength, 8));

        GeneratorStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid data type: " + type);
        }
        return strategy.generate(length);
    }

    public String generateRandomData(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Data length must be positive.");
        }
        StringBuilder data = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            data.append(random.nextInt(2));
        }
        return data.toString();
    }

    public String generateDataWithFixedZeros(int length, int zeroCount) {
        if (length <= 0) {
            throw new IllegalArgumentException("Data length must be positive.");
        }
        if (zeroCount <= 0) {
            throw new IllegalArgumentException("Zero count must be positive.");
        }
        if (zeroCount > length) {
            throw new IllegalArgumentException("Zero sequence cannot be longer than data length.");
        }
        StringBuilder data = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            data.append(random.nextInt(2));
        }
        int start = random.nextInt(length - zeroCount + 1);
        for (int i = start; i < start + zeroCount; i++) {
            data.setCharAt(i, '0');
        }
        return data.toString();
    }

    @FunctionalInterface
    private interface GeneratorStrategy {
        String generate(int length);
    }
}
