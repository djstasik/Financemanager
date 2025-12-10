package financialmanager.util;

import java.util.UUID;

public class IDGenerator {

    private IDGenerator() {
    }

    public static String generateId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateId(String prefix) {
        return prefix + "_" + generateId();
    }
}