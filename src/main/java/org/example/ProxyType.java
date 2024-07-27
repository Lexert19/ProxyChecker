package org.example;

public enum ProxyType {
    SOCKS4,
    SOCKS5,
    HTTPS,
    HTTP;
    public static ProxyType fromString(String protocol) {
        if (protocol != null) {
            try {
                return ProxyType.valueOf(protocol.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Obsługa przypadku, gdy ciąg znaków nie pasuje do żadnego z typów enum
                System.err.println("Niepoprawny typ protokołu: " + protocol);
            }
        }
        return null; // Lub można rzucić wyjątek, w zależności od wymagań
    }
}
