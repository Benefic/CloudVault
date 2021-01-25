package ru.abenefic.cloudvault.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.abenefic.cloudvault.client.Launcher;
import ru.abenefic.cloudvault.client.network.Connection;
import ru.abenefic.cloudvault.client.support.Context;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthDialogController {

    private static final Logger LOG = LogManager.getLogger(AuthDialogController.class);

    private Stage settingsDialogStage;
    private Launcher launcher;

    public ImageView imageView;
    public Pane container;
    public Button btnLogin;
    public Hyperlink hlSettings;
    public TextField fldLogin;
    public PasswordField fldPassword;
    public Hyperlink hlRegister;

    public void prepare(Launcher launcher) {
        this.launcher = launcher;
        try (InputStream input = getClass().getResourceAsStream("security-vault.jpg")) {
            Image image = new Image(input);
            imageView.setImage(image);
        } catch (IOException e) {
            LOG.error("Image load error:", e);
        }

        //TODO remove
        fldLogin.setText("user1");
        fldPassword.setText("112358");
        try {
            login(null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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

    public void login(ActionEvent event) throws NoSuchAlgorithmException {
        prepareContext();
        try {
            new Connection().login(this);
        } catch (InterruptedException e) {
            LOG.error("register", e);
        }
    }

    public void register(ActionEvent event) throws NoSuchAlgorithmException {
        prepareContext();
        try {
            new Connection().register(this);
        } catch (InterruptedException e) {
            LOG.error("register", e);
        }
    }

    private void prepareContext() throws NoSuchAlgorithmException {
        Context.current().setLogin(fldLogin.getText());
        String password = fldPassword.getText();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String passwordHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();

        Context.current().setPassword(passwordHash);
    }

    public void closeSettings() {
        settingsDialogStage.close();
    }

    public void fireError(String error) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Ошибка регистрации на сервере!");
            alert.setContentText(error);
            alert.showAndWait();
        });
    }

    public void loginSuccess() {
        Platform.runLater(() -> {
            launcher.openVault();
        });
    }
}
