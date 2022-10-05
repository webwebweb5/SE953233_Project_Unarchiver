package se233.unarchiver.model;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.*;
import java.util.List;

public class Extract7z implements Runnable {
    private final List<String> listName;
    private final String location;
    public List<String> fileToExtract;

    public Extract7z(String location, List<String> listName, List<String> fileToExtract) {
        this.location = location;
        this.listName = listName;
        this.fileToExtract = fileToExtract;
    }

    @Override
    public void run() {
        for (int i = 0; i < fileToExtract.size(); i++) {
            System.out.println("start task: " + (i+1));
            try {
                File destination = new File(location + "\\" + listName.get(i));
                SevenZFile sevenZFile = new SevenZFile(new File(fileToExtract.get(i)));
                SevenZArchiveEntry entry;
                while ((entry = sevenZFile.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    File curFile = new File(destination, entry.getName());
                    File parent = curFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    FileOutputStream out = new FileOutputStream(curFile);
                    byte[] content = new byte[(int) entry.getSize()];
                    sevenZFile.read(content, 0, content.length);
                    out.write(content);
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
