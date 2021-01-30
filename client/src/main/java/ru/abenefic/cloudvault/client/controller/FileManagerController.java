package ru.abenefic.cloudvault.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.network.Connection;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.CommandType;
import ru.abenefic.cloudvault.common.commands.DirectoryTree;
import ru.abenefic.cloudvault.common.commands.FileItem;
import ru.abenefic.cloudvault.common.commands.FileTreeItem;
import ru.abenefic.cloudvault.common.commands.FilesList;

import java.util.Date;

public class FileManagerController {
    private static final Logger LOG = LogManager.getLogger(FileManagerController.class);

    private final Image folderIcon = new Image(getClass().getResourceAsStream("folder.png"));
    private final Node rootIcon = new ImageView(folderIcon);
    private final TreeItem<FileTreeItem> rootNode = new TreeItem<>(new FileTreeItem("Сервер", "root"), rootIcon);
    private LogoutListener logoutListener;
    public Button btnSettings;
    public VBox tableBox;
    public TreeView<FileTreeItem> treeView;
    public Button btnExit;

    public FileManagerController prepare() {
        drawButtons();
        rootNode.setExpanded(true);
        updateTree();
        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem) -> {
            new Connection().getFilesList(this, newItem.getValue().getPath());
        });
        return this;
    }

    public void setLogoutListener(LogoutListener listener) {
        this.logoutListener = listener;
    }

    private void drawButtons() {
        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("settings.png"))));
        btnExit.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("logout.png"))));
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
            for (FileTreeItem fileTreeItem : directoryTree.getChildren()) {
                String itemPath = fileTreeItem.getPath();
                String[] pathParts = itemPath.split("\\\\");
                if (pathParts.length == 1) {
                    rootNode.getChildren().add(new TreeItem<>(fileTreeItem, new ImageView(folderIcon)));
                } else {
                    String parentName;
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pathParts.length - 1; i++) {
                        sb.append(pathParts[i]).append("\\");
                    }
                    sb.deleteCharAt(sb.lastIndexOf("\\"));
                    parentName = sb.toString();
                    TreeItem<FileTreeItem> parent = findItemByPath(rootNode, parentName);
                    ObservableList<TreeItem<FileTreeItem>> children = parent.getChildren();
                    if (children != null) {
                        children.add(new TreeItem<>(fileTreeItem, new ImageView(folderIcon)));
                    }
                }
            }
            treeView.setRoot(rootNode);
            treeView.setShowRoot(true);
            treeView.setEditable(false);

        });
    }

    private TreeItem<FileTreeItem> findItemByPath(TreeItem<FileTreeItem> root, String parentName) {
        if (root.getValue().getPath().equals(parentName)) {
            return root;
        }
        for (TreeItem<FileTreeItem> child : root.getChildren()) {
            TreeItem<FileTreeItem> item = findItemByPath(child, parentName);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    private void updateFileTable(FilesList filesList) {
        Platform.runLater(() -> {
                    updateTableView(filesList);
                }
        );
    }

    private void updateTableView(FilesList filesList) {
        tableBox.getChildren().clear();

        TableView<FileItem> tableView = new TableView<>();

        TableColumn<FileItem, String> columnFileName = new TableColumn<>("Имя");
        columnFileName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<FileItem, String> columnExtension = new TableColumn<>("Тип");
        columnExtension.setCellValueFactory(new PropertyValueFactory<>("extension"));

        TableColumn<FileItem, Date> columnDate = new TableColumn<>("Дата");
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));


        tableView.getColumns().setAll(columnFileName, columnExtension, columnDate);

        if (filesList != null) {
            SortedList<FileItem> sortedList = new SortedList<>(
                    FXCollections.observableArrayList(filesList.getList())
            );
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedList);
        }

        tableBox.getChildren().add(tableView);

    }

    public void onCommandSuccess(Command command) {
        if (command.getType() == CommandType.GET_TREE) {
            updateTreeView((DirectoryTree) command.getData());
        } else if (command.getType() == CommandType.GET_FILES) {
            updateFileTable((FilesList) command.getData());
        }
    }


    public void openSettings(ActionEvent actionEvent) {
        SettingsController.openSettings();
    }

    public void exit(ActionEvent actionEvent) {
        logoutListener.logout();
    }
}
