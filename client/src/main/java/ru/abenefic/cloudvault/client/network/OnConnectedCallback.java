package ru.abenefic.cloudvault.client.network;
// слушаем успешное подключение
@FunctionalInterface
public interface OnConnectedCallback {
    void call();
}
