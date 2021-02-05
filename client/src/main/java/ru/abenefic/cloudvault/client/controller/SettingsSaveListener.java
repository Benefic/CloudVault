package ru.abenefic.cloudvault.client.controller;

// при сохранении надо закрыть окно, за это отвечает вызывающий объект путём реализации этого интерфейса
@FunctionalInterface
public interface SettingsSaveListener {
    void onSave();
}
