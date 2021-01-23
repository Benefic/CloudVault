package ru.abenefic.cloudvault.client.support;

import lombok.Data;

@Data
public class Config {

    private static Config current;

    private int SERVER_PORT = 8189;
    private String SERVER_HOST = "localhost";

    private Config() {
    }

    public static Config current() {
        if (current == null) {
            current = new Config();
        }
        return current;
    }

}
