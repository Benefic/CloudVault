package ru.abenefic.cloudvault.client.controller;

import javafx.scene.control.TreeView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.Launcher;
import ru.abenefic.cloudvault.client.network.Connection;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.CommandType;

public class FileManagerController {
    private static final Logger LOG = LogManager.getLogger(FileManagerController.class);

    public TreeView tree;
    private Launcher mainApp;

    public void prepare(Launcher authDialogController) {
        mainApp = authDialogController;
        updateTree();
    }

    private void updateTree() {
        try {
            new Connection().getDirectoryTree(this);
        } catch (InterruptedException e) {
            LOG.error("Get tree", e);
        }
    }

    public void onCommandSuccess(Command command) {
        if (command.getType() == CommandType.GET_TREE) {
            LOG.info(command.getData());
        }
    }


}
