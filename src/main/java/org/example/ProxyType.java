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
                System.err.println("Niepoprawny typ protokołu: " + protocol);
            }
        }
        return null;
    }
}
