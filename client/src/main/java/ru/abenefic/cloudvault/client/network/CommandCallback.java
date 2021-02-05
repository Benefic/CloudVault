package ru.abenefic.cloudvault.client.network;

import ru.abenefic.cloudvault.common.NetworkCommand;

/**
 * Объекты, реализующие этот интерфейс, могут реагировать на команды с сервера
 */
@FunctionalInterface
public interface CommandCallback {
    void call(NetworkCommand command);
}
