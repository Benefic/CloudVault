package ru.abenefic.cloudvault.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringData implements CommandData {
    private String data;
}
