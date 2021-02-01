package ru.abenefic.cloudvault.client.network;

import ru.abenefic.cloudvault.common.NetworkCommand;

@FunctionalInterface
public interface CommandCallback {
    void call(NetworkCommand command);
}
