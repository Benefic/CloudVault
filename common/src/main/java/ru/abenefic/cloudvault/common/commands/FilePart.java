/*
 * Copyright (c) 2021. Benefic
 */

package ru.abenefic.cloudvault.common.commands;

import java.util.Arrays;

public class FilePart implements CommandData {

    public static final int partSize = 1_048_576 / 2;

    private final String fileName;
    private final byte[] data;
    private final boolean isEnd;

    public FilePart(String fileName, byte[] data, int dataLength, boolean isEnd) {
        this.fileName = fileName;
        if (dataLength > 0 && data.length > dataLength) {
            this.data = Arrays.copyOf(data, dataLength);
        } else {
            this.data = data;
        }
        this.isEnd = isEnd;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isEnd() {
        return isEnd;
    }

    @Override
    public String toString() {
        return "FilePart{" +
                "fileName='" + fileName + '\'' +
                ", data length=" + data.length +
                '}';
    }

}
