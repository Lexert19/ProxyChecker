package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class SaveProxy {
    public static void appendProxyToFile(ProxyModel proxy){
        BufferedWriter writer = null;
        try {
            String output =  proxy.getIp() + ":" + proxy.getPort();
            String filePath = Paths.get("").toAbsolutePath().toString() + "/"+proxy.getType()+".txt";
            writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.write(output);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
