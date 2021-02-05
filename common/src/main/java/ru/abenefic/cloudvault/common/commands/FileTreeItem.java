package ru.abenefic.cloudvault.common.commands;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * Класс - представление папки пользователя - для сети и View на клиенте
 */

@Data
public class FileTreeItem implements Serializable {
    @NonNull
    private String name;
    @NonNull
    private String path;

    @Override
    public String toString() {
        return name;
    }

    public FileTreeItem(@NonNull String name, @NonNull String path) {
        this.name = name;
        this.path = path.replaceAll("\\\\", "/");
    }
}
