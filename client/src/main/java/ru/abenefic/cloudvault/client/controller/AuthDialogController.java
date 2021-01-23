package ru.abenefic.cloudvault.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.Launcher;

import java.io.IOException;
import java.io.InputStream;

public class AuthDialogController {

    private static final Logger LOG = LogManager.getLogger(AuthDialogController.class);

    public ImageView imageView;
    public Pane container;
    public Button btnLogin;
    public Hyperlink hlSettings;
    public TextField fldLogin;
    public PasswordField fldPassword;
    public Hyperlink hlRegister;

    private Stage settingsDialogStage;

    public void prepare() {
        try (InputStream input = getClass().getResourceAsStream("security-vault.jpg")) {
            Image image = new Image(input);
            imageView.setImage(image);
        } catch (IOException e) {
            LOG.error("Image load error:", e);
        }

    }

    public void login(ActionEvent event) {

    }

    public void openSettings(ActionEvent event) {

        try {

            FXMLLoader settingsLoader = new FXMLLoader();

            settingsLoader.setLocation(Launcher.class.getResource("view/settingsDialog.fxml"));
            Parent settingsDialogPanel = settingsLoader.load();
            settingsDialogStage = new Stage();

            settingsDialogStage.setTitle("Cloud Vault");
            settingsDialogStage.initModality(Modality.WINDOW_MODAL);
            settingsDialogStage.setResizable(false);
            Scene scene = new Scene(settingsDialogPanel);
            settingsDialogStage.setScene(scene);
            settingsDialogStage.show();

            SettingsController settingsController = settingsLoader.getController();
            settingsController.prepare(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(ActionEvent event) {

    }

    public void closeSettings() {
        settingsDialogStage.close();
    }
}
