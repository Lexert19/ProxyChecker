package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProxyModel {
    private String ip;
    private int port;
    private ProxyType type;

}
