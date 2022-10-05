package se233.unarchiver.model;

import javafx.collections.ObservableList;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import se233.unarchiver.Launcher;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ZipUnZip {
    private final ObservableList<String> listPath;
    private final String name;
    private String password;
    private final String location;
    private boolean checkBoxSelect;
    public String sumLocation;

    public ZipUnZip(ObservableList<String> listPath, String nameFile, String passwordFile, String locationFile, boolean checkBoxSelect) {
        this.listPath = listPath;
        this.name = nameFile;
        this.password = passwordFile;
        this.location = locationFile;
        this.checkBoxSelect = checkBoxSelect;

        System.out.println("Name : " + this.name);
        System.out.println("Password : " + this.password);
        System.out.println("Location : " + this.location);
        System.out.println("Checkbox select : " + this.checkBoxSelect);

        sumLocation = (this.location + "/" + this.name + ".zip");
    }

    public void compressed() throws IOException {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(false);

        if (Objects.nonNull(password) && checkBoxSelect) {
            zipParameters.setEncryptFiles(true);
        }
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);

        List<File> filesToAdd = new ArrayList<>();

        for (String s : listPath) {
            filesToAdd.add(new File(s));
        }

        ZipFile zipFile = new ZipFile(sumLocation, password.toCharArray());
        zipFile.addFiles(filesToAdd, zipParameters);

        for (File dir : filesToAdd) {
            if (dir.isDirectory()) {
                zipFile.addFolder(dir, zipParameters);
            }
        }

        zipFile.close();

        Launcher.OpenFileLocation(sumLocation);
    }

    public ObservableList<String> getListPath() {
        return listPath;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getSumLocation() {
        return sumLocation;
    }
}
