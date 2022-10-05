package se233.unarchiver.model;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.List;

public class ExtractZip implements Runnable {
    private final List<String> listPath;
    private final String location;

    public ExtractZip(List<String> listPath, String location) {
        this.listPath = listPath;
        this.location = location;
    }

    @Override
    public void run() {

        System.out.println("Total file(s) assigned: " + listPath.size());
        // get rid .zip
        List<String> lp = listPath.stream()
                .filter(name -> name.endsWith(".zip"))
                .map(name -> name.substring(0, name.length() - ".zip".length())).toList();
        // C:\Users\phiri\Desktop\zip\passTest.zip -> passTest
        List<String> listName = listPath.stream()
                .filter(name -> name.endsWith(".zip"))
                .map(name -> name.substring(lp.get(0).lastIndexOf("\\") + 1, name.length() - ".zip".length())).toList();

        for (int i = 0; i < lp.size(); i++) {
            System.out.println("Start task: " + (i+1));
            File theDir = new File(location + "\\" + listName.get(i));
            if (!theDir.exists()) {
                theDir.mkdirs();
                System.out.println("Location : " + location + "\\" + listName.get(i));
            } else {
                System.out.println("This directory already exists");
            }

            ZipFile zipFile = new ZipFile(new File(listPath.get(i)));
            try {
                zipFile.extractAll(location  + "\\" + listName.get(i));
            } catch (ZipException e) {
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Complete task: " + (i+1));
            System.out.println("Thread Name: " + Thread.currentThread().getName());
        }
    }
}
