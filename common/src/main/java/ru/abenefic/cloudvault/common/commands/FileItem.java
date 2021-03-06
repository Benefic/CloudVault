package ru.abenefic.cloudvault.common.commands;

import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;

/**
 * Основной класс - представление файла - как для сети, так и для View
 */

@Data                                           // Comparable - для сортировки в талице в окне
public class FileItem implements Serializable, Comparable<FileItem> {
    private boolean isFolder;
    private String name;
    private long createAt;
    private String extension;
    private Date date;
    private long size;
    // этот параметр говорит, есть ли такой файл на клиентской стороне. Для отображения в таблице
    private boolean exist;

    public static FileItem fromPath(Path path, FileTime createAt) {
        FileItem fileItem = new FileItem();
        String pathname = String.valueOf(path);
        File file = new File(pathname);
        if (file.exists()) {
            fileItem.isFolder = file.isDirectory();
            fileItem.name = file.getName();
            fileItem.extension = fileItem.isFolder ? "" : pathname.substring(pathname.lastIndexOf(".") + 1);
            fileItem.createAt = createAt.toMillis();
            fileItem.date = new Date(fileItem.createAt);
            fileItem.size = file.length();
        }
        return fileItem;
    }

    @Override
    public int compareTo(FileItem o) {
        return name.compareTo(o.getName());
    }
}
