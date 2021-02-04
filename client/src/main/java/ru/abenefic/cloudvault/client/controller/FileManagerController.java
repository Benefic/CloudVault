package ru.abenefic.cloudvault.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.network.Connection;
import ru.abenefic.cloudvault.client.support.Context;
import ru.abenefic.cloudvault.common.Command;
import ru.abenefic.cloudvault.common.NetworkCommand;
import ru.abenefic.cloudvault.common.commands.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.ResourceBundle;


/**
 * Главный экран
 */
public class FileManagerController implements Initializable {
    private static final Logger LOG = LogManager.getLogger(FileManagerController.class);

    private final Image folderIcon = new Image(getClass().getResourceAsStream("folder.png"));
    private final Node rootIcon = new ImageView(folderIcon);
    private final TreeItem<FileTreeItem> rootNode = new TreeItem<>(new FileTreeItem("Сервер", ""), rootIcon);

    public Button btnUpload;
    public Button btnDownload;
    public Button btnOpenFile;
    public Button btnOpenFolder;
    public Button btnSettings;
    public VBox tableBox;
    public TreeView<FileTreeItem> treeView;
    public Button btnExit;
    public ToolBar tbFileButtons;
    public TableView<FileItem> tableView;
    public ProgressBar progressBar;

    private String currentFolder = "./";
    private String currentFile;
    private LogoutListener logoutListener;

    // для прогресса выгрузки
    private float fileSize;
    private int iterator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // переопределяем pipeline - команды слушаем теперь тут
        Connection.getInstance()
                .onCommand(this::onCommandSuccess);
        drawButtons();
        rootNode.setExpanded(true);
        updateTree();
        treeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldItem, newItem)
                -> {
            currentFolder = newItem.getValue().getPath();
            currentFile = null;
            btnDownload.setDisable(true);
            Connection.getInstance().getFilesList(currentFolder);
        });
        initTableView();
    }

    private void initTableView() {
        TableColumn<FileItem, String> columnFileName = new TableColumn<>("Имя");
        columnFileName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<FileItem, String> columnExtension = new TableColumn<>("Тип");
        columnExtension.setCellValueFactory(new PropertyValueFactory<>("extension"));

        TableColumn<FileItem, Date> columnDate = new TableColumn<>("Дата");
        columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<FileItem, Boolean> columnExist = new TableColumn<>("Скачан");
        columnExist.setCellValueFactory(new PropertyValueFactory<>("exist"));

        tableView.getColumns().setAll(columnFileName, columnExtension, columnDate, columnExist);

        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isFolder()) {
                    currentFile = null;
                    btnDownload.setDisable(true);
                } else {
                    currentFile = newValue.getName();
                    btnDownload.setDisable(false);
                }
            }
        });
    }

    public void setLogoutListener(LogoutListener listener) {
        // для открытия обратно окна авторизации при выходе пользователя
        this.logoutListener = listener;
    }

    private void drawButtons() {
        btnSettings.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("settings.png"))));
        btnExit.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("logout.png"))));
    }

    private void updateTree() {
        try {
            Connection.getInstance().getDirectoryTree();
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
                    ObservableList<TreeItem<FileTreeItem>> children;
                    if (parent != null) {
                        children = parent.getChildren();
                        if (children != null) {
                            children.add(new TreeItem<>(fileTreeItem, new ImageView(folderIcon)));
                        }
                    }
                }
            }
            treeView.setRoot(rootNode);
            treeView.setShowRoot(true);
            treeView.setEditable(false);
            // в корне могут быть не только папки, но и каталоги
            Connection.getInstance().getFilesList(currentFolder);
        });
    }

    // поиск родителя для рекурсивной отрисовки дерева
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

    // обновляем таблицу файлов при выборе папки в дереве из результатов запроса к серверу
    private void updateFileTable(FilesList filesList) {
        Path userHome = Context.current().getUserHome();

        for (FileItem fileItem : filesList.getList()) {
            fileItem.setExist(Files.exists(userHome.resolve(currentFolder).resolve(fileItem.getName())));
        }
        Platform.runLater(() -> updateTableView(filesList));
    }

    private void updateTableView(FilesList filesList) {

        tableView.getItems().clear();

        if (filesList != null) {
            SortedList<FileItem> sortedList = new SortedList<>(
                    FXCollections.observableArrayList(filesList.getList())
            );
            sortedList.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.getItems().addAll(sortedList);
        }
    }

    // обрабатываем результаты запросов здесь
    public void onCommandSuccess(NetworkCommand networkCommand) {
        Command command = (Command) networkCommand;
        switch (command.getType()) {
            case GET_FILES -> updateFileTable((FilesList) command.getData());
            case GET_TREE -> updateTreeView((DirectoryTree) command.getData());
            case FILE_TRANSFER -> writeFilePart((FilePart) command.getData());
            case FILE_TRANSFER_RESULT -> {
                iterator++;
                if (iterator * FilePart.partSize >= fileSize) {
                    progressBar.setProgress(0);
                    tbFileButtons.setDisable(false);
                    Connection.getInstance().getFilesList(currentFolder);
                } else {
                    float progress = ((float) (FilePart.partSize * iterator)) / fileSize;
                    progressBar.setProgress(progress);
                }
            }
            case REMOVE_FILE -> Connection.getInstance().getFilesList(currentFolder);

        }

    }

    private void writeFilePart(FilePart data) {

        if (data.isEnd()) {
            // это просто маркер что весь файл передан. включаем кнопку обратно
            Platform.runLater(() -> {
                tbFileButtons.setDisable(false);
                progressBar.setProgress(data.getProgress());
                Connection.getInstance().getFilesList(currentFolder);
            });
        } else {
            Platform.runLater(() -> progressBar.setProgress(data.getProgress()));
            Path filePath = Context.current().getUserHome().resolve(currentFolder).resolve(data.getFileName());
            try {
                Files.write(filePath, data.getData(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }


    public void openSettings() {
        SettingsController.openSettings();
    }

    public void exit() {
        logoutListener.logout();
    }

    public void upload() {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            tbFileButtons.setDisable(true);
            iterator = 0;
            fileSize = file.length();
            Connection.getInstance().uploadFile(file.getAbsolutePath(), currentFolder);
        }
    }

    public void download() throws IOException {
        // выключаем кнопку скачивания, пока процесс однопоточный...
        tbFileButtons.setDisable(true);
        Path path = Path.of(String.valueOf(Context.current().getUserHome()), currentFolder);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
        Connection.getInstance().getFile(Path.of(currentFolder, currentFile).toString());
    }

    public void openFile() {
        if (Files.exists(Paths.get(String.valueOf(Context.current().getUserHome()), currentFolder, currentFile))) {
            try {
                Desktop.getDesktop().open(Paths.get(String.valueOf(Context.current().getUserHome()), currentFolder, currentFile).toFile());
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось открыть файл");
                alert.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Сперва надо файл скачать");
            alert.show();
        }
    }

    public void openFolder() {
        try {
            Desktop.getDesktop().open(Paths.get(
                    String.valueOf(Context.current().getUserHome()),
                    currentFolder).toFile());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось открыть папку");
            alert.show();
        }

    }

    public void removeFile() {

    }

    public void renameFile() {

    }
}
