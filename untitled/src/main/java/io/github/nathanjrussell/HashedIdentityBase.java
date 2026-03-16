package io.github.nathanjrussell;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * Base class with a stable {@link #hashCode()} derived from a HMAC-SHA256 of {@code governmentID}.
 * <p>
 * Notes:
 * <ul>
 *   <li>This is a teaching/demo-friendly implementation. For real systems, do not hardcode keys.
 *   Use {@code -Dapp.hmacKey=...} or the {@code APP_HMAC_KEY} environment variable.</li>
 *   <li>{@link #equals(Object)} is based on {@code governmentID} to honor the equals/hashCode contract.</li>
 * </ul>
 */
public abstract class HashedIdentityBase {

    private static final String HMAC_ALG = "HmacSHA256";
    private static final String HMAC_KEY_SYS_PROP = "app.hmacKey";
    private static final String HMAC_KEY_ENV = "APP_HMAC_KEY";

    protected final String firstName;
    protected final String lastName;
    /** Canonical spelling. */
    protected final String governmentID;
    protected final String employeeID;

    protected HashedIdentityBase(String firstName,
                                String lastName,
                                String governmentID,
                                String employeeID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.governmentID = governmentID;
        this.employeeID = employeeID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGovernmentID() {
        return governmentID;
    }

    /** @deprecated Prefer {@link #getGovernmentID()}. Kept for the misspelling requested in the prompt. */
    @Deprecated
    public String getGovernementID() {
        return governmentID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    @Override
    public final int hashCode() {
        if (governmentID == null) {
            return 0;
        }

        byte[] digest = hmacSha256(resolveHmacKeyBytes(), governmentID);
        // Use the first 4 bytes as big-endian int to satisfy hashCode() return type.
        return ByteBuffer.wrap(digest, 0, Integer.BYTES).getInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashedIdentityBase that = (HashedIdentityBase) o;
        if (governmentID == null && that.governmentID == null) return true;
        if (governmentID == null || that.governmentID == null) return false;
        return governmentID.equals(that.governmentID);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", governmentID='" + (governmentID == null ? null : "***") + '\'' +
                ", employeeID='" + employeeID + '\'' +
                '}';
    }

    private static byte[] resolveHmacKeyBytes() {
        String key = System.getProperty(HMAC_KEY_SYS_PROP);
        if (key == null || key.isBlank()) {
            key = System.getenv(HMAC_KEY_ENV);
        }
        // Demo-friendly fallback: deterministic but NOT secure.
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
