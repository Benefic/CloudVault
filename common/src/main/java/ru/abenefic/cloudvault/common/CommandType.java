/*
 * Copyright (c) 2021. Benefic
 */

package ru.abenefic.cloudvault.common;

/**
 * Перечисление типов команд, для избежания лишних instanceof и ограничения возможных объектов в транспорте
 */
public enum CommandType {
    GET_TREE,
    GET_FILES,
    FILE_TRANSFER,
    EXIT,
    GET_FILE
}
