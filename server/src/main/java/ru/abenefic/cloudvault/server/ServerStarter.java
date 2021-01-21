package ru.abenefic.cloudvault.server;

import ru.abenefic.cloudvault.server.storage.StorageServer;
import ru.abenefic.cloudvault.server.support.Configuration;

public class ServerStarter {

    public static void main(String[] args) {
        int port = Configuration.getInstance().getSrvPort();
        new StorageServer(port);
    }
}
