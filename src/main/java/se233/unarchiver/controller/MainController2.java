package se233.unarchiver.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import net.lingala.zip4j.exception.ZipException;
import se233.unarchiver.Launcher;
import se233.unarchiver.model.SevenzUnSevenz;
import se233.unarchiver.model.ZipUnZip;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController2 {
    @FXML
    private Button exitButton;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField passwordField;
    @FXML
    private PasswordField passwordHidden;
    @FXML
    private CheckBox checkBox2;
    @FXML
    private TextField browseField;
    @FXML
    private Button archiveButton;
    @FXML
    private Label archiveLabel;
    @FXML
    private ImageView browseIcon;
    @FXML
    private Label browseLabel;
    @FXML
    private TextField nameField;
    @FXML
    private Label passwordLabel;
    @FXML
    private ScrollPane scrollPaneResponse;
    @FXML
    private ImageView imageZip;
//    @FXML
//    private ImageView imageTar;
    @FXML
    private ImageView image7z;
    @FXML
    private Label archiveFormat;
    private String nameFile;
    private String passwordFile = "";
    private String browseFile;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    public ObservableList<String> inputContent;
    public ObservableList<Integer> inputContentIndices;
    public ObservableList<String> originalContent;
    private final Alert alert = new Alert(Alert.AlertType.NONE);
    private Glow glowRealOn = new Glow(0.5);
    private Glow glowTempON = new Glow(0.4);
    private Glow glowTempOFF = new Glow(0);
    private String archiveType = "";

    @FXML
    private void initialize() {

        // back to main scene
        exitButton.setOnAction(e -> {
            try {
                originalContent.addAll(inputContent);
                new SceneController().switchSceneBackToMain(inputContent, inputContentIndices, originalContent);
            } catch (IOException ex) {
                throw new RuntimeException(ex + "Switch From Archive to Main");
            }
        });

        // checkbox
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            passwordLabel.setStyle(null);
            passwordField.setStyle(null);
            passwordHidden.setStyle(null);
            if (newValue) {
                if (!Objects.equals(archiveType, "7z")) {
                    passwordField.setDisable(false);
                    passwordHidden.setDisable(false);
                    checkBox2.setDisable(false);
                    passwordLabel.setDisable(false);
                }
            } else {
                passwordField.setDisable(true);
                passwordHidden.setDisable(true);
                checkBox2.setDisable(true);
                passwordLabel.setDisable(true);
            }
        });

        // use directoryChooser to choose directory
        browseIcon.setOnMouseClicked(e -> {
            browseLabel.setStyle(null);
            browseField.setStyle(null);
            File selectedDirectory = directoryChooser.showDialog(Launcher.stage);
            if (selectedDirectory != null) {
                browseField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        // nameField style null
        nameField.setOnMouseClicked(e -> {
            nameField.setStyle(null);
            archiveLabel.setStyle(null);
        });

        // passwordField style null
        passwordField.setOnMouseClicked(e -> {
            passwordField.setStyle(null);
            passwordLabel.setStyle(null);
        });

        // archive zip and 7z
        archiveButton.setOnAction(e -> {

            if (Objects.equals(archiveType, "")) {
                archiveTypeMessageAlert("Choose archive type first.(Click the archive type image)");

            } else {
                try {
                    commitArchive();
                    ZipUnZip sampleZip = new ZipUnZip(inputContent, nameFile, passwordFile, browseFile, checkBox.isSelected());
                    SevenzUnSevenz sample7z = new SevenzUnSevenz(inputContent, nameFile, browseFile);
                    try {
                        if (Objects.equals(archiveType, "zip")) {
                            sampleZip.compressed();
                        }
                        if (Objects.equals(archiveType, "7z")) {
                            sample7z.compressed();
                        }
                    } catch (ZipException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException er) {
                        System.out.println("Something bad happens to system file");
                    } catch (Exception er) {
                        er.printStackTrace();
                    }
                    new SceneController().switchSceneBackToMain(inputContent, inputContentIndices, originalContent);
                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
        });

        // glow effect and choose archiveType
        imageZip.setOnMouseEntered(event -> {
            if (imageZip.getEffect() != glowRealOn) {
                imageZip.setEffect(glowTempON);
            }
        });
        imageZip.setOnMouseExited(event -> {
            if (imageZip.getEffect() == glowTempON) {
                imageZip.setEffect(glowTempOFF);
            }
        });
        imageZip.setOnMouseClicked(event -> {
            if (imageZip.getEffect() == glowRealOn) {
                imageZip.setEffect(null);
                archiveType = "";
                return;
            }
            imageZip.setEffect(glowRealOn);
//            imageTar.setEffect(null);
            image7z.setEffect(null);
            archiveType = "zip";
            archiveFormat.setText("Create Archive: " + archiveType);
        });

//        imageTar.setOnMouseEntered(event -> {
//            if (imageTar.getEffect() != glowRealOn) {
//                imageTar.setEffect(glowTempON);
//            }
//        });
//        imageTar.setOnMouseExited(event -> {
//            if (imageTar.getEffect() == glowTempON) {
//                imageTar.setEffect(glowTempOFF);
//            }
//        });
//        imageTar.setOnMouseClicked(event -> {
//            if (imageTar.getEffect() == glowRealOn) {
//                imageTar.setEffect(null);
//                archiveType = "";
//                return;
//            }
//            imageTar.setEffect(glowRealOn);
//            imageZip.setEffect(null);
//            image7z.setEffect(null);
//            archiveType = "";
//            archiveFormat.setText("Create Archive: " + archiveType);
//        });

        image7z.setOnMouseEntered(event -> {
            if (image7z.getEffect() != glowRealOn) {
                image7z.setEffect(glowTempON);
            }
        });
        image7z.setOnMouseExited(event -> {
            if (image7z.getEffect() == glowTempON) {
                image7z.setEffect(glowTempOFF);
            }
        });
        image7z.setOnMouseClicked(event -> {
            if (image7z.getEffect() == glowRealOn) {
                image7z.setEffect(null);
                archiveType = "";
                return;
            }
            image7z.setEffect(glowRealOn);
            imageZip.setEffect(null);
//            imageTar.setEffect(null);
            archiveType = "7z";
            archiveFormat.setText("Create Archive: " + archiveType);
        });

        // show password
        checkBox2.setOnAction(e -> {
            if (checkBox2.isSelected()) {
                passwordField.setText(passwordHidden.getText());
                passwordField.setVisible(true);
                passwordHidden.setVisible(false);
            } else {
                passwordHidden.setText(passwordField.getText());
                passwordHidden.setVisible(true);
                passwordField.setVisible(false);
            }
        });
    }

    public void archiveTypeMessageAlert(String msg) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    private void commitArchive() throws NullPointerException {
        boolean checknull = false;

        if (checkBox.isSelected()) {

            if (Objects.equals(passwordHidden.getText(), "")) {
                passwordHidden.setText(passwordField.getText());
            } else {
                passwordField.setText(passwordHidden.getText());
            }

            if (Objects.equals(passwordField.getText(), "")) { //if passwordField is null
                passwordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
                passwordLabel.setStyle("-fx-text-fill:red ;");
                checknull = true;
            } else { //if passwordField is not null
                System.out.println("Password: " + passwordField.getText());
            }

            if (Objects.equals(passwordHidden.getText(), "")) {
                passwordHidden.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
                checknull = true;
            } else {
                System.out.println("Confirm Password: " + passwordHidden.getText());
            }

        }

        if (Objects.equals(nameField.getText(), "")) {
            nameField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
            archiveLabel.setStyle("-fx-text-fill:red ;");
            checknull = true;
        } else {
            nameFile = nameField.getText();
        }

        if (Objects.equals(browseField.getText(), "...") || Objects.equals(browseField.getText(), "")) {
            browseField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
            browseLabel.setStyle("-fx-text-fill:red ;");
            checknull = true;
        } else {
            browseFile = browseField.getText();
        }
        if (checknull) {
            throw new NullPointerException();
        }
        passwordFile = passwordField.getText();
        nameFile = nameField.getText();
        browseFile = browseField.getText();
    }

    public void receiveData(ObservableList<String> inputContent, ObservableList<Integer> inputContentIndices, ObservableList<String> originalContent) {
        this.inputContent = inputContent;
        this.inputContentIndices = inputContentIndices;
        this.originalContent = originalContent;

        StringBuilder list = new StringBuilder();
        for (String m : this.inputContent) {
            list.append(m).append(", ");
            list.append("\n");
        }
        int pos = list.lastIndexOf(",");
        String selection = list.substring(0, pos);
        scrollPaneResponse.setContent(new Text("You have selected " + this.inputContent.size() + " Item : \n" + selection));
    }

}
