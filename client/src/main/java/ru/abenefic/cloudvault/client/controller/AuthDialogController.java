package ru.abenefic.cloudvault.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

    private Launcher launcher;

    public CheckBox cbSavePassword;
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

        Context context = Context.current();
        fldLogin.setText(context.getLogin());
        cbSavePassword.setSelected(context.isSavePassword());
        if (context.isSavePassword() && !context.getPassword().isBlank() && !context.getLogin().isBlank()) {
            try {
                loginOnServer();
            } catch (Exception e) {
                LOG.error("Login failed", e);
            }
        }

    }

    public void openSettings(ActionEvent event) {
        SettingsController.openSettings();
    }

    public void login(ActionEvent event) throws NoSuchAlgorithmException {
        prepareContext();
        loginOnServer();
    }

    private void loginOnServer() {
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
        Context context = Context.current();
        context.setLogin(fldLogin.getText());
        String password = fldPassword.getText();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String passwordHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();

        context.setPassword(passwordHash);
        context.setSavePassword(cbSavePassword.isSelected());
        context.saveSettings();
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
            fldPassword.setText("");
            launcher.openVault();
        });
    }
}
