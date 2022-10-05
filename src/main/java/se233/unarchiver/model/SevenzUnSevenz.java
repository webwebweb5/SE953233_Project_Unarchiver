package se233.unarchiver.model;

import javafx.collections.ObservableList;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SevenzUnSevenz {
    private ObservableList<String> listPath;
    private final String name;
    private final String location;
    public String sumLocation;
    public String fileToExtract;

    public SevenzUnSevenz(ObservableList<String> listPath, String nameFile, String locationFile) {
        this.listPath = listPath;
        this.location = locationFile;
        this.name = nameFile;

        System.out.println("Name : " + this.name);
        System.out.println("Location : " + this.location);

        sumLocation = (this.location + "\\" + this.name + ".7z");
    }

    public void compressed() throws IOException {
        List<File> filesToAdd = new ArrayList<>();
        for (int i = 0; i < listPath.size(); i++) {
            filesToAdd.add(new File((String) listPath.get(i)));
        }
        System.out.println("Enter WillCompress");
        sumLocation = (this.location + "\\" + this.name + ".7z");
        System.out.println(sumLocation);
        System.out.println(filesToAdd);
        compress(sumLocation, filesToAdd);
    }

    public static void compress(String name, List<File> files) throws IOException {
        try (SevenZOutputFile out = new SevenZOutputFile(new File(name))) {
            for (File file : files) {
                System.out.println("file" + file);
                addToArchiveCompression(out, file, "");
            }
        }
    }

    private static void addToArchiveCompression(SevenZOutputFile out, File file, String dir) throws IOException {
        String name = dir + file.getName();
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (basicFileAttributes.isRegularFile()) {
            SevenZArchiveEntry entry = out.createArchiveEntry(file, name);
            out.putArchiveEntry(entry);

            FileInputStream in = new FileInputStream(file);
            byte[] b = new byte[1024];
            int count = 0;
            while ((count = in.read(b)) > 0) {
                out.write(b, 0, count);
            }
            out.closeArchiveEntry();

        } else if (basicFileAttributes.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addToArchiveCompression(out, child, name);
                }
            }
        } else {
            System.out.println(file.getName() + " is not supported");
        }
    }
}
