package ru.abenefic.cloudvault.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileTransferResult implements CommandData {
    boolean result;
}
