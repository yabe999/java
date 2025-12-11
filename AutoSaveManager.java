package exp;

import javax.swing.*;
import java.time.LocalDateTime;

public class AutoSaveManager implements Runnable {
    public static JTextArea logArea;
    @Override public void run() {
        try {
            Main.saveData();
            SwingUtilities.invokeLater(() -> logArea.append(LocalDateTime.now() + " 自动保存成功\n"));
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> logArea.append("自动保存失败: " + ex.getMessage() + "\n"));
        }
    }
}