package ru.abenefic.cloudvault.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

public class AuthDialogController {

    private static final Logger LOG = LogManager.getLogger(AuthDialogController.class);

    public ImageView imageView;
    public Pane container;
    public Button btnLogin;
    public Button btnRegister;
    public Hyperlink hlSettings;

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

    }

    public void register(ActionEvent event) {

    }
}
