package io.github.nathanjrussell;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/** Simple concrete type for demos/tests. */
public record Employee(String firstName, String lastName, String governmentID, String employeeID) {

    private static final String HMAC_ALG = "HmacSHA256";
    private static final String HMAC_KEY_SYS_PROP = "app.hmacKey";
    private static final String HMAC_KEY_ENV = "APP_HMAC_KEY";

    public static Employee fromTypoField(String firstName, String lastName, String governementID, String employeeID) {
        return new Employee(firstName, lastName, governementID, employeeID);
    }

    /** @deprecated Prefer {@link #governmentID()}. Kept for the misspelling requested in the prompt. */
    @Deprecated
    public String getGovernementID() {
        return governmentID;
    }

    /** Backwards-friendly getter name (record also provides {@link #governmentID()}). */
    public String getGovernmentID() {
        return governmentID;
    }

    /** Backwards-friendly getter name (record also provides {@link #employeeID()}). */
    public String getEmployeeID() {
        return employeeID;
    }

    @Override
    public int hashCode() {
        if (governmentID == null) {
            return 0;
        }
        byte[] digest = hmacSha256(resolveHmacKeyBytes(), governmentID);
        return ByteBuffer.wrap(digest, 0, Integer.BYTES).getInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee that)) return false;
        if (governmentID == null && that.governmentID == null) return true;
        if (governmentID == null || that.governmentID == null) return false;
        return governmentID.equals(that.governmentID);
    }

    private static byte[] resolveHmacKeyBytes() {
        String key = System.getProperty(HMAC_KEY_SYS_PROP);
        if (key == null || key.isBlank()) {
            key = System.getenv(HMAC_KEY_ENV);
        }
        if (key == null || key.isBlank()) {
            key = "demo-key-change-me";
        }
        return key.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] hmacSha256(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(new SecretKeySpec(key, HMAC_ALG));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to compute HMAC-SHA256", e);
        }
    }
}
