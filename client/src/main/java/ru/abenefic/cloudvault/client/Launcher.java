package ru.abenefic.cloudvault.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.abenefic.cloudvault.client.controller.AuthDialogController;

public class Launcher extends Application {

    private Stage primaryStage;
    private Stage authDialogStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
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
        authDialogStage.show();

        AuthDialogController authController = authLoader.getController();
        authController.prepare();
    }
}
