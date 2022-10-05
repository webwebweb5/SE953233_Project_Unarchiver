package se233.unarchiver.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import se233.unarchiver.Launcher;
import se233.unarchiver.model.Extract7z;
import se233.unarchiver.model.ExtractZip;
import se233.unarchiver.model.SevenzUnSevenz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainController3 {
    public ObservableList<String> inputContent;
    public ObservableList<Integer> inputContentIndices;
    public ObservableList<String> originalContent;
    private ZipFile zipFile;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private static PasswordField passwordField;
    private String browseFile;
    @FXML
    private ImageView browseIcon;
    @FXML
    private Button exitButton;
    @FXML
    private Button extractButton;
    @FXML
    private TextField browseField;
    @FXML
    private Label browseLabel;
    @FXML
    private Button closeButton;
    @FXML
    private Label text1;
    @FXML
    private Rectangle text2;
    private String password;
//    ExecutorService executorService = Executors.newFixedThreadPool(12);

    @FXML
    public void initialize() {
        exitButton.setOnAction(e -> {
            for (String s : inputContent) {
                originalContent.add(s);
                System.out.println(s);
            }
            try {
                new SceneController().switchSceneBackToMain(inputContent, inputContentIndices, originalContent);
            } catch (IOException ex) {
                throw new RuntimeException(ex + "Switch From Archive to Main");
            }
        });

        closeButton.setOnMouseClicked(e -> {
            text1.setVisible(false);
            text2.setVisible(false);
            closeButton.setVisible(false);
        });

        extractButton.setOnAction(e -> {
            try {
                commitExtract();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });

        browseIcon.setOnMouseClicked(e -> {
            browseLabel.setStyle(null);
            browseField.setStyle(null);
            File selectedDirectory = directoryChooser.showDialog(Launcher.stage);
            if (selectedDirectory != null) {
                browseField.setText(selectedDirectory.getAbsolutePath());
            }
        });
    }

    private void checkExtract() throws NullPointerException {
        boolean checknull = false;

        if (Objects.equals(browseField.getText(), "...") || Objects.equals(browseField.getText(), "")) {
            browseField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
            browseLabel.setStyle("-fx-text-fill:red ;");
            checknull = true;
        }

        if (checknull) {
            throw new NullPointerException();
        }
        browseFile = browseField.getText();
    }

    List<String> inputContentListZip = new ArrayList<>();
    List<String> inputContentListSevenZip = new ArrayList<>();
    List<String> inputContentZip = new ArrayList<>();
    List<String> inputContentSevenZip = new ArrayList<>();

    private void commitExtract() throws IOException {
        checkExtract();
        for (int i = 0; i < inputContent.size(); i++) {
            if (MainController.getFileExtention(inputContent.get(i)).equals("zip")) {
                inputContentZip.add((String) inputContent.get(i));
            } else if (MainController.getFileExtention(inputContent.get(i)).equals("7z")) {
                inputContentSevenZip.add((String) inputContent.get(i));
            }
        }
        System.out.println("Zip : " + inputContentZip);
        System.out.println("SevenZip : " + inputContentSevenZip);

        extractZip(inputContentZip);
        extractSevenZip(inputContentSevenZip);

    }

    private void extractSevenZip(List<String> inputContentSevenZip) {
        List<String> lp = inputContentSevenZip //path
                .stream()
                .filter(name -> name.endsWith(".7z"))
                .map(name -> name.substring(0, name.length() - ".7z".length())).toList();

        List<String> listName = inputContentSevenZip //name
                .stream()
                .filter(name -> name.endsWith(".7z"))
                .map(name -> name.substring(lp.get(0).lastIndexOf("\\") + 1, name.length() - ".7z".length())).toList();

//        Extract7z sevenZipFile = new Extract7z(inputContentSevenZip, browseFile);
//        sevenZipFile.extract();

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        if (inputContentSevenZip.size() > 1) {
            for (int i = 0; i < inputContentSevenZip.size() / 2; i++) {
                inputContentListSevenZip.add(inputContentSevenZip.get(i));
            }
            executorService.submit(new Extract7z(browseFile, listName, inputContentSevenZip));
            for (int i = inputContentSevenZip.size() / 2; i < inputContentSevenZip.size(); i++) {
                inputContentListSevenZip.add(inputContentSevenZip.get(i));
            }
            executorService.submit(new Extract7z(browseFile, listName, inputContentSevenZip));
        } else {
            executorService.submit(new Extract7z(browseFile, listName, inputContentSevenZip));
        }

        executorService.shutdown();

        System.out.println("All extract 7zip submitted.");

        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("All extract 7zip completed.");
        try {
            Launcher.OpenFileLocation(browseFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private void extractZip(List<String> inputContentZip) {
        for (int i = 0; i < inputContentZip.size(); i++) {
            zipFile = new ZipFile(inputContentZip.get(i));//, "password".toCharArray()
            try {
                System.out.println(zipFile.isEncrypted());
            } catch (ZipException ex) {
                throw new RuntimeException(ex);
            }
            try {
                if (zipFile.isEncrypted()) {
                    zipFile.removeFile(inputContentZip.get(i));
                    password();
                    password = passwordField.getText();
                    if (!password.isEmpty()) {
                        zipFile = new ZipFile(inputContentZip.get(i), password.toCharArray());
                        zipFile.extractAll(browseFile + "\\" + zipName(i));
                    } else {
                        password();
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        if (inputContentZip.size() > 2) {
            for (int i = 0; i < inputContentZip.size() / 2; i++) {
                inputContentListZip.add(inputContentZip.get(i));
            }
            executorService.submit(new ExtractZip(inputContentListZip, browseFile));
            for (int i = inputContentZip.size() / 2; i < inputContentZip.size(); i++) {
                inputContentListZip.add(inputContentZip.get(i));
            }
            executorService.submit(new ExtractZip(inputContentListZip, browseFile));
        } else {
            executorService.submit(new ExtractZip(inputContentZip, browseFile));
        }

        executorService.shutdown();

        System.out.println("All extract zip submitted.");

        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("All extract zip completed.");
        try {
            Launcher.OpenFileLocation(browseFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void password() throws IOException {
        Dialog<String> dialog = new Dialog<>();
        ButtonType loginButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");

        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        dialog.setTitle("Password require");
        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private String zipName(int index) {
        List<String> lp = inputContent.stream()
                .filter(name -> name.endsWith(".zip"))
                .map(name -> name.substring(0, name.length() - ".zip".length())).toList();

        List<String> listName = inputContent.stream()
                .filter(name -> name.endsWith(".zip"))
                .map(name -> name.substring(lp.get(0).lastIndexOf("\\") + 1, name.length() - ".zip".length())).toList();

        return listName.get(index);
    }

    public void receiveData(ObservableList<String> inputContent, ObservableList<Integer> inputContentIndices, ObservableList<String> originalContent) {
        this.inputContent = inputContent;
        this.inputContentIndices = inputContentIndices;
        this.originalContent = originalContent;
    }
}
