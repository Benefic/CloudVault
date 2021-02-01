package ru.abenefic.cloudvault.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.abenefic.cloudvault.client.controller.AuthDialogController;
import ru.abenefic.cloudvault.client.controller.FileManagerController;
import ru.abenefic.cloudvault.client.network.Connection;
import ru.abenefic.cloudvault.client.support.Context;

import java.io.IOException;

public class Launcher extends Application {

    private Stage authDialogStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread connection = new Thread(this::connect);
        connection.setDaemon(true);
        connection.start();
        primaryStage.setOnCloseRequest(value -> Connection.getInstance().shutdown());


        FXMLLoader authLoader = new FXMLLoader();

        authLoader.setLocation(getClass().getResource("view/authDialog.fxml"));
        Parent authDialogPanel = authLoader.load();
        authDialogStage = new Stage();

        authDialogStage.setTitle("Cloud Vault");
        authDialogStage.initModality(Modality.WINDOW_MODAL);
        authDialogStage.initOwner(primaryStage);
        authDialogStage.setResizable(false);
        Scene scene = new Scene(authDialogPanel);
        authDialogStage.setScene(scene);
        authDialogStage.setOnCloseRequest(value -> Connection.getInstance().shutdown());
        authDialogStage.show();

        AuthDialogController authController = authLoader.getController();
        authController.prepare(this);
    }

    private void connect() {
        Connection.getInstance()
                .onConnected(() -> {
                })
                .onCommand(command -> {
                })
                .connect();
    }

    public void openVault() {
        try {

            authDialogStage.hide();

            FXMLLoader mainViewLoader = new FXMLLoader();

            mainViewLoader.setLocation(Launcher.class.getResource("view/cloudVault.fxml"));
            Parent mainViewDialogPanel = mainViewLoader.load();
            Stage mainViewDialogStage = new Stage();

            mainViewDialogStage.setTitle(Context.current().getLogin());
            mainViewDialogStage.initModality(Modality.NONE);
            mainViewDialogStage.setResizable(true);
            Scene scene = new Scene(mainViewDialogPanel);
            mainViewDialogStage.setScene(scene);
            mainViewDialogStage.setOnCloseRequest(value -> Connection.getInstance().shutdown());

            mainViewDialogStage.show();

            FileManagerController fileManagerController = mainViewLoader.getController();
            fileManagerController.setLogoutListener(() -> {
                mainViewDialogStage.setOnCloseRequest(null);
                mainViewDialogStage.close();
                authDialogStage.show();
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
