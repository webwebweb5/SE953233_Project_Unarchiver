package se233.unarchiver.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se233.unarchiver.Launcher;

import java.io.IOException;

public class SceneController {
    private final Stage stageBasic = Launcher.stage;
    private Parent root;
    public void switchSceneMainToArchive(ObservableList<String> outputContent,ObservableList<Integer> outputContentIndices, ObservableList<String> originalContent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("main-view-scene2.fxml"));
        root = loader.load();
        MainController2 scene2controller = loader.getController();
        scene2controller.receiveData(outputContent,outputContentIndices,originalContent);

        Scene scene2 = new Scene(root);
        scene2.getStylesheets().add(Launcher.class.getResource("style/style.css").toExternalForm());
        stageBasic.setScene(scene2);
        stageBasic.show();
    }

    public void switchSceneBackToMain(ObservableList<String> outputContent,ObservableList<Integer> outputContentIndices, ObservableList<String> originalContent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("main-view-scene1.fxml"));
        root = loader.load();
        MainController scene1controller = loader.getController();
        scene1controller.receiveData(outputContent,outputContentIndices,originalContent);

        Scene scene1 = new Scene(root);
        scene1.getStylesheets().add(Launcher.class.getResource("style/style.css").toExternalForm());
        stageBasic.setScene(scene1);
        stageBasic.show();
    }

    public void switchSceneMainToExtract(ObservableList<String> outputContent,ObservableList<Integer> outputContentIndices, ObservableList<String> originalContent) throws IOException {
        FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("main-view-scene3.fxml"));
        root = loader.load();
        MainController3 scene3controller = loader.getController();
        scene3controller.receiveData(outputContent,outputContentIndices,originalContent);

        Scene scene1 = new Scene(root);
        scene1.getStylesheets().add(Launcher.class.getResource("style/style.css").toExternalForm());
        stageBasic.setScene(scene1);
        stageBasic.show();
    }
}
