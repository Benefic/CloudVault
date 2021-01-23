package ru.abenefic.cloudvault.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ru.abenefic.cloudvault.client.support.Config;

public class SettingsController {
    public TextField fldHost;
    public TextField fldPort;
    public Button btnSave;

    private AuthDialogController owner;

    public void prepare(AuthDialogController owner) {
        this.owner = owner;
        fldPort.setText(String.valueOf(Config.current().getSERVER_PORT()));
        fldHost.setText(Config.current().getSERVER_HOST());
        fldPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}")) {
                fldPort.setText(oldValue);
            }
        });
    }

    public void save(ActionEvent event) {
        Config.current().setSERVER_HOST(fldHost.getText());
        Config.current().setSERVER_PORT(Integer.parseInt(fldPort.getText()));
        owner.closeSettings();
    }
}
