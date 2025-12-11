package exp;

import java.io.*;
import java.nio.file.*;

public class PersistenceManager {
    private static final String DIR = "data";

    public static void save(Object obj, String fileName) throws IOException {
        Path path = Paths.get(DIR, fileName);
        Files.createDirectories(path.getParent());
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(path))) {
            oos.writeObject(obj);
        }
        System.out.println("已保存: " + path);
    }

    public static Object load(String fileName) throws IOException, ClassNotFoundException {
        Path path = Paths.get(DIR, fileName);
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(path))) {
            return ois.readObject();
        }
    }
}