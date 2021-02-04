package ru.abenefic.cloudvault.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RenameData implements CommandData {
    String filePath;
    String newName;
}
