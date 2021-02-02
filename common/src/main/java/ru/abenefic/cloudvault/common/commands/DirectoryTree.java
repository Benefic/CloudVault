package ru.abenefic.cloudvault.common.commands;


import lombok.Data;

import java.util.ArrayList;

/**
 * Класс-контейнер для иерархии папок пользователя на сервере
 */

@Data
public class DirectoryTree implements CommandData {

    private final ArrayList<FileTreeItem> children = new ArrayList<>();

    public void add(FileTreeItem item) {
        children.add(item);
    }

    @Override
    public String toString() {
        return "DirectoryTree{" +
                "children=" + children +
                '}';
    }

}
