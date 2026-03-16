package io.github.nathanjrussell;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Demo key (do not hardcode secrets in real apps)
        System.setProperty("app.hmacKey", "demo-key-change-me");

        Employee e1 = new Employee("Ada", "Lovelace", "GOV-123", "E-1");
        Employee e2 = new Employee("Alan", "Turing", "GOV-123", "E-2");

        System.out.println("e1.hashCode() = " + e1.hashCode());
        System.out.println("e2.hashCode() = " + e2.hashCode());
        System.out.println("e1.equals(e2) = " + e1.equals(e2));

        Map<Employee, String> map = new HashMap<>();
        map.put(e1, "Found via HMAC hash");

        System.out.println("map.get(e2) = " + map.get(e2));
    }
}