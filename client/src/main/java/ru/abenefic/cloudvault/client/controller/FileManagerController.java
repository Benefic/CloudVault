package ru.abenefic.cloudvault.client.controller;

import javafx.scene.control.TreeView;
import ru.abenefic.cloudvault.client.Launcher;

public class FileManagerController {

    public TreeView tree;
    private Launcher mainApp;

    public void prepare(Launcher authDialogController) {
        mainApp = authDialogController;
    }
}
