package exp;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeoutManager implements Runnable {
    public static JTextArea logArea;
    @Override public void run() {
        try {
            long count = 0;
            for (Order o : Main.orders) {
                if (o.getStatus() == OrderStatus.PENDING &&
                        ChronoUnit.MINUTES.between(o.getCreateTime(), LocalDateTime.now()) >= 30) {
                    o.changeStatus(OrderStatus.CANCELED);
                    count++;
                }
            }
            if (count > 0) {
                final long c = count;
                SwingUtilities.invokeLater(() -> logArea.append(LocalDateTime.now() + " 超时取消 " + c + " 单\n"));
            }
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> logArea.append("超时检测异常: " + ex.getMessage() + "\n"));
        }
    }
}