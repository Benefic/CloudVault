package ru.abenefic.cloudvault.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectoryTreeItem {
    private String name;
    private String uuid;
}
