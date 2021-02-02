package ru.abenefic.cloudvault.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * Класс - представление папки пользователя - для сети и View на клиенте
 */

@Data
@AllArgsConstructor
public class FileTreeItem implements Serializable {
    @NonNull
    private String name;
    @NonNull
    private String path;

    @Override
    public String toString() {
        return name;
    }
}
