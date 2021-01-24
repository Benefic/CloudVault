package ru.abenefic.cloudvault.client.support;

import lombok.Data;

@Data
public class Context {

    private static Context current;

    private int serverPort = 8189;
    private String serverHost = "localhost";
    private String login;
    private String password;

    private Context() {
    }

    public static Context current() {
        if (current == null) {
            current = new Context();
        }
        return current;
    }

}
