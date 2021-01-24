package ru.abenefic.cloudvault.client.support;

import lombok.Data;

@Data
public class Config {

    private static Config current;

    private int serverPort = 8189;
    private String serverHost = "localhost";
    private String login;
    private String password;

    private Config() {
    }

    public static Config current() {
        if (current == null) {
            current = new Config();
        }
        return current;
    }

}
