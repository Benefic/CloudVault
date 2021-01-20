package ru.abenefic.cloudvault.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ServerStarter {
    private static final int DEFAULT_PORT = 8189;
    private static final Logger LOG = LogManager.getLogger(ServerStarter.class);

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }
        try {
            //TODO
            throw new IOException();
        } catch (IOException e) {
            LOG.error("Failed to start VaultServer", e);
            System.exit(1);
        }
    }
}
