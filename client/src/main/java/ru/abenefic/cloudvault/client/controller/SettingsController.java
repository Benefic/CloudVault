package ru.abenefic.cloudvault.client.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.Launcher;
import ru.abenefic.cloudvault.client.support.Context;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;


/**
 * окно настроек
 */
public class SettingsController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(SettingsController.class);

    public TextField fldHost;
    public TextField fldPort;
    public Button btnSave;
    public Button btnDirectoryChoose;
    public TextField fldDirectory;
    public CheckBox cbAutoUpload;

    private SettingsSaveListener owner;

    public static void openSettings() {
        try {
            FXMLLoader settingsLoader = new FXMLLoader();

            settingsLoader.setLocation(Launcher.class.getResource("view/settingsDialog.fxml"));
            Parent settingsDialogPanel = settingsLoader.load();
            Stage settingsDialogStage = new Stage();

            settingsDialogStage.setTitle("Настройки");
            settingsDialogStage.initModality(Modality.APPLICATION_MODAL);
            settingsDialogStage.setResizable(false);
            Scene scene = new Scene(settingsDialogPanel);
            settingsDialogStage.setScene(scene);
            settingsDialogStage.show();

            SettingsController settingsController = settingsLoader.getController();
            settingsController.onSave(settingsDialogStage::close);

        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // читаем из контекста в поля ввода
        Context context = Context.current();
        fldPort.setText(String.valueOf(context.getServerPort()));
        fldHost.setText(context.getServerHost());
        fldPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}")) {
                fldPort.setText(oldValue);
            }
        });
        fldDirectory.setText(context.getUserHome().toString());
        cbAutoUpload.setSelected(context.isAutoUpload());
    }

    public void onSave(SettingsSaveListener listener) {
        this.owner = listener;
    }

    public void save() {
        // сохраняем значения в контексте и сразу записываем в файл
        Context context = Context.current();
        context.setServerHost(fldHost.getText());
        context.setServerPort(Integer.parseInt(fldPort.getText()));
        context.setUserHome(Paths.get(fldDirectory.getText()));
        context.setAutoUpload(cbAutoUpload.isSelected());
        context.saveSettings();
        owner.onSave();
    }

    public void selectDirectory() {
        Context context = Context.current();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(context.getUserHome().toFile());
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            fldDirectory.setText(selectedDirectory.getAbsolutePath());
        }
    }


}
