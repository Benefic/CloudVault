package ru.abenefic.cloudvault.common.commands;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Контейнер для представлений файлов
 */

@Data
public class FilesList implements CommandData {
    private List<FileItem> list = new ArrayList<>();

    public void add(FileItem fileItem) {
        list.add(fileItem);
    }
}
