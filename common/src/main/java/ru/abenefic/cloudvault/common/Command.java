/*
 * Copyright (c) 2021. Benefic
 */

package ru.abenefic.cloudvault.common;


import ru.abenefic.cloudvault.common.commands.CommandData;
import ru.abenefic.cloudvault.common.commands.FilePart;

/**
 * Основной класс транспорта - он передаётся и ловится Хэндлерами на обеих сторонах
 * По совместительству "фабрика" команд
 */
public class Command implements NetworkCommand {

    private CommandType type;
    private CommandData data;
    private String token;

    public static Command getTreeCommand() {
        Command command = new Command();
        command.type = CommandType.GET_TREE;
        return command;
    }

    public static Command getFilesCommand(CommandData commandData) {
        Command command = new Command();
        command.type = CommandType.GET_FILES;
        command.data = commandData;
        return command;
    }

    public static Command getFileCommand(CommandData commandData) {
        Command command = new Command();
        command.type = CommandType.GET_FILE;
        command.data = commandData;
        return command;
    }


    public static Command filePartTransferCommand(String fileName, byte[] fileData, int dataLength, boolean isEnd) {
        Command command = new Command();
        command.type = CommandType.FILE_TRANSFER;
        command.data = new FilePart(fileName, fileData, dataLength, isEnd);
        return command;
    }

    public static Command exitCommand() {
        Command command = new Command();
        command.type = CommandType.EXIT;
        return command;
    }

    public CommandType getType() {
        return type;
    }

    public CommandData getData() {
        return data;
    }

    public void setData(CommandData data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Command{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
