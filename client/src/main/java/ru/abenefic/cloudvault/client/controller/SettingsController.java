package ru.abenefic.cloudvault.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.nio.file.Paths;

public class SettingsController {

    private static final Logger LOG = LogManager.getLogger(SettingsController.class);


    public TextField fldHost;
    public TextField fldPort;
    public Button btnSave;
    public Button btnDirectoryChoose;
    public TextField fldDirectory;

    private SettingsSaveListener owner;

    public static void openSettings() {
        try {
            FXMLLoader settingsLoader = new FXMLLoader();

            settingsLoader.setLocation(Launcher.class.getResource("view/settingsDialog.fxml"));
            Parent settingsDialogPanel = settingsLoader.load();
            Stage settingsDialogStage = new Stage();

            settingsDialogStage.setTitle("Настройки");
            settingsDialogStage.initModality(Modality.WINDOW_MODAL);
            settingsDialogStage.setResizable(false);
            Scene scene = new Scene(settingsDialogPanel);
            settingsDialogStage.setScene(scene);
            settingsDialogStage.show();

            SettingsController settingsController = settingsLoader.getController();
            settingsController.prepare().onSave(settingsDialogStage::close);

        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public SettingsController prepare() {
        Context context = Context.current();
        fldPort.setText(String.valueOf(context.getServerPort()));
        fldHost.setText(context.getServerHost());
        fldPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}")) {
                fldPort.setText(oldValue);
            }
        });
        fldDirectory.setText(context.getUserHome().toString());
        return this;
    }

    public void onSave(SettingsSaveListener listener) {
        this.owner = listener;
    }

    public void save(ActionEvent event) {
        Context context = Context.current();
        context.setServerHost(fldHost.getText());
        context.setServerPort(Integer.parseInt(fldPort.getText()));
        context.setUserHome(Paths.get(fldDirectory.getText()));
        context.saveSettings();
        owner.onSave();
    }

    public void selectDirectory(ActionEvent actionEvent) {
        Context context = Context.current();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(context.getUserHome().toFile());
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            fldDirectory.setText(selectedDirectory.getAbsolutePath());
        }
    }
}
