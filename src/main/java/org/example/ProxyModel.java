package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ProxyModel {
    private String ip;
    private int port;
    private ProxyType type;

}
