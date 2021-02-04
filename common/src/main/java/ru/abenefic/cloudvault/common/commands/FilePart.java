/*
 * Copyright (c) 2021. Benefic
 */

package ru.abenefic.cloudvault.common.commands;

import java.util.Arrays;

/**
 * Транспорт кусков разбитого файла
 */

public class FilePart implements CommandData {

    public static final int partSize = 100_000;

    private final String fileName;
    private final byte[] data;
    private final boolean isEnd;
    private final double progress;
    private final int partNumber;

    public FilePart(String fileName, byte[] data, int dataLength, boolean isEnd, double progress, int partNumber) {
        this.fileName = fileName;
        this.progress = progress;
        this.partNumber = partNumber;
        if (dataLength > 0 && data.length > dataLength) {
            // это "хвост файла", обрезаем массив, тащить весь ни к чему
            this.data = Arrays.copyOf(data, dataLength);
        } else if (dataLength == -1) {
            // заглушка для
            this.data = new byte[0];
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
                // ", data =" + Arrays.toString(data) +
                '}';
    }

    public double getProgress() {
        return progress;
    }

    public int getPartNumber() {
        return partNumber;
    }
}
