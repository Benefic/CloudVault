package ru.abenefic.cloudvault.common.commands;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class DirectoryTree implements CommandData {

    private final ArrayList<TreeItem> children = new ArrayList<>();

    public void add(TreeItem item) {
        children.add(item);
    }

    @Data
    @AllArgsConstructor
    public static class TreeItem implements Serializable {
        @NonNull
        private String name;
        @NonNull
        private String path;
    }
}
