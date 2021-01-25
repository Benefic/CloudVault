package ru.abenefic.cloudvault.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ru.abenefic.cloudvault.client.support.Context;

public class SettingsController {
    public TextField fldHost;
    public TextField fldPort;
    public Button btnSave;

    private AuthDialogController owner;

    public void prepare(AuthDialogController owner) {
        this.owner = owner;
        fldPort.setText(String.valueOf(Context.current().getServerPort()));
        fldHost.setText(Context.current().getServerHost());
        fldPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}")) {
                fldPort.setText(oldValue);
            }
        });
    }

    public void save(ActionEvent event) {
        Context.current().setServerHost(fldHost.getText());
        Context.current().setServerPort(Integer.parseInt(fldPort.getText()));
        owner.closeSettings();
    }
}
