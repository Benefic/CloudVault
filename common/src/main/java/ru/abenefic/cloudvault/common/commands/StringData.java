package ru.abenefic.cloudvault.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс-контейнер для строковых данных для транспорта - просто маркер для того,
 * чтобы просунуть String как CommandData
 */

@Data
@AllArgsConstructor
public class StringData implements CommandData {
    private String data;
}
