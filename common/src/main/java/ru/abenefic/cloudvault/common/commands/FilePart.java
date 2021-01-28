/*
 * Copyright (c) 2021. Benefic
 */

package ru.abenefic.cloudvault.common.commands;

public class FilePart implements CommandData {

    public static final int partSize = 1_048_576;

    private final String fileName;
    private final byte[] data;
    private final int dataLength;

    public FilePart(String fileName, byte[] data, int dataLength) {
        this.fileName = fileName;
        this.data = data;
        this.dataLength = dataLength;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "FilePart{" +
                "fileName='" + fileName + '\'' +
                ", data length=" + data.length +
                '}';
    }


    public int getDataLength() {
        return dataLength;
    }
}
