package io.github.nathanjrussell;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HashedIdentityBaseTest {

    @AfterEach
    void cleanup() {
        System.clearProperty("app.hmacKey");
    }

    @Test
    void hashCode_isStableForSameGovernmentIdAndKey() {
        System.setProperty("app.hmacKey", "unit-test-key");

        Employee a = new Employee("Ada", "Lovelace", "GOV-123", "E-1");
        Employee b = new Employee("Alan", "Turing", "GOV-123", "E-2");

        assertEquals(a.hashCode(), b.hashCode(), "Same governmentID should yield same hashCode (with same key)");
        assertEquals(a, b, "equals() should align with hashCode() identity basis");
    }

    @Test
    void hashCode_changesWhenKeyChanges() {
        Employee emp = new Employee("Ada", "Lovelace", "GOV-123", "E-1");

        System.setProperty("app.hmacKey", "key-1");
        int h1 = emp.hashCode();

        System.setProperty("app.hmacKey", "key-2");
        int h2 = emp.hashCode();

        assertNotEquals(h1, h2, "Different HMAC keys should yield different hashCode values");
    }

    @Test
    void worksAsHashMapKey() {
        System.setProperty("app.hmacKey", "unit-test-key");

        Employee key1 = new Employee("Ada", "Lovelace", "GOV-123", "E-1");
        Employee key2 = new Employee("Alan", "Turing", "GOV-123", "E-2");

        Map<Employee, String> map = new HashMap<>();
        map.put(key1, "value");

        assertEquals("value", map.get(key2));
    }

    @Test
    void typoGetterReturnsSameGovernmentId() {
        Employee emp = Employee.fromTypoField("Ada", "Lovelace", "GOV-999", "E-9");
        assertEquals("GOV-999", emp.getGovernmentID());
        assertEquals("GOV-999", emp.getGovernementID());
    }
}
