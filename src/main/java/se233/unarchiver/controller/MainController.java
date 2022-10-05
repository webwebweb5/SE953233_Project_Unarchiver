package se233.unarchiver.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import se233.unarchiver.Launcher;

import java.io.File;
import java.util.List;

public class MainController {
    @FXML
    private Button archiveButton; //commit to archive button
    @FXML
    private Button extractButton;
    @FXML
    public ListView<String> inputListView;
    @FXML
    private Button searchButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button deleteAllButton;
    @FXML
    private Button selectAllButton;
    private final Alert alert = new Alert(Alert.AlertType.NONE);
    private final FileChooser fileChooser = new FileChooser();
    @FXML
    private ImageView dropFileImage;
    @FXML
    private Label dropFileLabel;
    @FXML
    private Label countLabel;
    @FXML
    private Label zomsanLogo;
    @FXML
    private ImageView Zomsan1, Zomsan2, Zomsan3;

    public void initialize() {
        // Drag file(s) over ListView
        inputListView.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();

            if (db.hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY);
                dropFileLabel.setVisible(true);
                dropFileImage.setVisible(true);
                inputListView.setOpacity(0.5);
            } else {
                e.consume();
            }
        });

        // Drag file(s) exit ListView
        inputListView.setOnDragExited(event -> {
            dropFileLabel.setVisible(false);
            dropFileImage.setVisible(false);
            inputListView.setOpacity(1);
        });

        // Can select multiple files in listView
        inputListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Drop file(s) into ListView
        inputListView.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath;
                int total_files = db.getFiles().size();
                for (int i = 0; i < total_files; i++) {
                    File file = db.getFiles().get(i);
                    filePath = file.getAbsolutePath();
                    inputListView.getItems().add(filePath);
                }
            }
            countLabel.setText("Total file(s) : " + inputListView.getItems().size());
            e.setDropCompleted(success);
            e.consume();
        });

        // Select multiple files and unselect file.
        inputListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };

            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (cell.isEmpty()) {
                    return;
                }

                int index = cell.getIndex();

                if (inputListView.getSelectionModel().getSelectedIndices().contains(index)) {
                    inputListView.getSelectionModel().clearSelection(index);
                } else {
                    inputListView.getSelectionModel().select(index);
                }

                inputListView.requestFocus();

                e.consume();
            });
            return cell;
        });

        // archiveButton -> store data and switch to scene 2
        archiveButton.setOnAction(e ->
        {
            try {
                commitArchive(inputListView);
            } catch (IndexOutOfBoundsException er) {
                exceptionMessageAlert(er, "Need To Select File First");
            } catch (NullPointerException er) {
                exceptionMessage(er, "Can't Popup Another Scenes");
            } catch (Exception er) {
                exceptionMessage(er, "Something Went Wrong");
            }
        });

        // extractButton -> store data and switch to scene 3
        extractButton.setOnAction(e -> {
            try {
                commitExtract(inputListView);
            } catch (IndexOutOfBoundsException er) {
                exceptionMessageAlert(er, "Need To Select File First");
            } catch (NullPointerException er) {
                exceptionMessage(er, "Can't Popup Another Scenes");
            } catch (Exception er) {
                exceptionMessage(er, "Something Went Wrong");
            }
        });

        // deleteAllButton -> delete all items in ListView
        deleteAllButton.setOnAction(e -> {
            try {
                listviewDeleteAllItems(inputListView);
            } catch (Exception er) {
                exceptionMessageAlert(er, "Something Went Wrong");
            }
        });

        // selectAllButton -> select all items in ListView
        selectAllButton.setOnAction(e -> listviewSelectAllItems());

        // deleteButton -> delete selected item(s) in ListView
        deleteButton.setOnAction(e -> {
            try {
                listviewDeleteItems(inputListView);
            } catch (IndexOutOfBoundsException er) {
                exceptionMessageAlert(er, "Need To Select File First");
            } catch (Exception er) {
                exceptionMessageAlert(er, "Something Went Wrong");
            }
        });

        // searchButton -> select item(s) by using fileChooser to choose file in file explorer
        searchButton.setOnAction(e -> {
            try {
                listViewSearchItems(inputListView);
            } catch (Exception ignored) {
            }
            dropFileLabel.setVisible(false);
            dropFileImage.setVisible(false);
        });

        // just toggle image
        zomsanLogo.setOnMouseClicked(e -> {
            if (!Zomsan1.isVisible()) {
                Zomsan1.setVisible(true);
                Zomsan2.setVisible(true);
                Zomsan3.setVisible(true);
            } else {
                Zomsan1.setVisible(false);
                Zomsan2.setVisible(false);
                Zomsan3.setVisible(false);
            }
        });
    }

    // use fileChooser to chose item in file explorer
    private void listViewSearchItems(ListView<String> sampleListView) {
        fileChooser.setTitle("Search Files");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));

        List<File> selectedFile = fileChooser.showOpenMultipleDialog(Launcher.stage);

        for (File file : selectedFile) {
            sampleListView.getItems().add(file.getAbsolutePath());
        }

        countLabel.setText("Total file(s) : " + inputListView.getItems().size());
    }

    // delete item by selecting index.
    public void listviewDeleteItems(ListView<String> sampleListView) {
        ObservableList<Integer> selectedIndices;
        selectedIndices = getSelectedLVIndices(sampleListView);

        if (selectedIndices.size() == 0) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = selectedIndices.size() - 1; i >= 0; i--) {
            sampleListView.getItems().remove((int) selectedIndices.get(i));
        }

        getSelectedLVIndices(sampleListView).clear();
        countLabel.setText("Total file(s) : " + inputListView.getItems().size());
    }

    // for deleteAllButton
    public void listviewDeleteAllItems(ListView<String> sampleListView) {
        if (sampleListView.getItems().size() > 0) {
            sampleListView.getItems().subList(0, sampleListView.getItems().size()).clear();
        }

        getSelectedLVIndices(sampleListView).clear();
        countLabel.setText("Total file(s): " + inputListView.getItems().size());
    }

    // for selectAllButton
    public void listviewSelectAllItems() {
        for (int i = 0; i < inputListView.getItems().size(); i++) {
            inputListView.getSelectionModel().select(i);
        }
    }

    // get index of each listview items
    private ObservableList<Integer> getSelectedLVIndices(ListView<String> sampleListView) {
        return sampleListView.getSelectionModel().getSelectedIndices();
    }

    // get item in listview
    private ObservableList<String> getSelectedLVItems(ListView<String> sampleListView) {
        return sampleListView.getSelectionModel().getSelectedItems();
    }

    // get All item in listview
    private ObservableList<String> getAllLVItems(ListView<String> sampleListView) {
        return sampleListView.getItems();
    }

    // send data to archive scene
    private void commitArchive(ListView<String> sampleListView) {

        ObservableList<String> outputContent = getSelectedLVItems(sampleListView);
        ObservableList<Integer> outputContentIndices = getSelectedLVIndices(sampleListView);
        ObservableList<String> originalContent = getAllLVItems(sampleListView);


        if (outputContent.size() == 0) {
            throw new IndexOutOfBoundsException();
        }

        if (confirmation("Are you sure you want to archive these files?")) {
            try {
                new SceneController().switchSceneMainToArchive(outputContent, outputContentIndices, originalContent);
            } catch (Exception er) {
                er.printStackTrace();
            }

        }
    }

    // send data to extract scene
    private void commitExtract(ListView<String> sampleListView) {
        ObservableList<String> selectedItems;
        selectedItems = getSelectedLVItems(sampleListView);

        ObservableList<String> outputContent = getSelectedLVItems(sampleListView);
        ObservableList<Integer> outputContentIndices = getSelectedLVIndices(sampleListView);
        ObservableList<String> originalContent = getAllLVItems(sampleListView);


        if (selectedItems.size() == 0) {
            throw new IndexOutOfBoundsException();
        }

        if (confirmation("Are you sure you want to archive these files?")) {
            try {
                new SceneController().switchSceneMainToExtract(outputContent, outputContentIndices, originalContent);
            } catch (Exception er) {
                er.printStackTrace();
            }

        }

    }

    // Alert
    public void exceptionMessageAlert(Exception er, String msg) {
        System.out.println("Exception occurs : " + er);
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    public void exceptionMessage(Exception er, String msg) {
        System.out.println("Exception occurs : " + er);
        System.out.print(" " + msg);
    }

    public boolean confirmation(String msg) {
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.setContentText(msg);
        alert.showAndWait();

        return alert.getResult() == ButtonType.OK;
    }

    private void setListView(ObservableList<String> originalContent) {
        for (String s : originalContent) {
            inputListView.getItems().add(s);
        }
    }

    // data flow
    public void receiveData(ObservableList<String> inputContent, ObservableList<Integer> inputContentIndices, ObservableList<String> originalContent) {
        setListView(originalContent);

        for (int i = inputContentIndices.size() - 1; i >= 0; i--) {
            inputListView.getItems().remove((int) inputContentIndices.get(i));
        }

        countLabel.setText("Total file(s) : " + inputListView.getItems().size());
        dropFileLabel.setVisible(false);
        dropFileImage.setVisible(false);
    }

    public static String getFileExtention(String filepath) {
        return FilenameUtils.getExtension(filepath).toLowerCase();
    }
}
