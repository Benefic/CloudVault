package ru.abenefic.cloudvault.client.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.Launcher;
import ru.abenefic.cloudvault.client.network.Connection;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.CommandType;
import ru.abenefic.cloudvault.common.commands.DirectoryTree;

public class FileManagerController {
    private static final Logger LOG = LogManager.getLogger(FileManagerController.class);

    private final Image folderIcon = new Image(getClass().getResourceAsStream("folder.png"));
    private Launcher mainApp;
    private final Node rootIcon = new ImageView(folderIcon);
    private final TreeItem<DirectoryTree.TreeItem> rootNode = new TreeItem<>(new DirectoryTree.TreeItem("Сервер", "root"), rootIcon);
    public TreeView<DirectoryTree.TreeItem> treeView;

    public void prepare(Launcher authDialogController) {
        mainApp = authDialogController;
        rootNode.setExpanded(true);
        updateTree();
    }

    private void updateTree() {
        try {
            new Connection().getDirectoryTree(this);
        } catch (InterruptedException e) {
            LOG.error("Get tree", e);
        }
    }

    private void updateTreeView(DirectoryTree directoryTree) {
        Platform.runLater(() -> {
            for (DirectoryTree.TreeItem treeItem : directoryTree.getChildren()) {
                String itemPath = treeItem.getPath();
                String[] pathParts = itemPath.split("\\\\");
                if (pathParts.length == 1) {
                    rootNode.getChildren().add(new TreeItem<>(treeItem, new ImageView(folderIcon)));
                } else {
                    String parentName;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pathParts.length - 1; i++) {
                        sb.append(pathParts[i]).append("\\");
                    }
                    sb.deleteCharAt(sb.lastIndexOf("\\"));
                    parentName = sb.toString();
                    TreeItem<DirectoryTree.TreeItem> parent = findItemByPath(rootNode, parentName);
                    ObservableList<TreeItem<DirectoryTree.TreeItem>> children = parent.getChildren();
                    if (children != null) {
                        children.add(new TreeItem<>(treeItem, new ImageView(folderIcon)));
                    }
                }
            }
            treeView.setRoot(rootNode);
            treeView.setShowRoot(true);
            treeView.setEditable(false);

        });
    }

    private TreeItem<DirectoryTree.TreeItem> findItemByPath(TreeItem<DirectoryTree.TreeItem> root, String parentName) {
        if (root.getValue().getPath().equals(parentName)) {
            return root;
        }
        for (TreeItem<DirectoryTree.TreeItem> child : root.getChildren()) {
            TreeItem<DirectoryTree.TreeItem> item = findItemByPath(child, parentName);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public void onCommandSuccess(Command command) {
        if (command.getType() == CommandType.GET_TREE) {
            updateTreeView((DirectoryTree) command.getData());
        }
    }


}
