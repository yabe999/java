package exp;

import javax.swing.*;
import java.awt.*;

public class StarRenderer extends JLabel implements ListCellRenderer<Runner> {
    @Override
    public Component getListCellRendererComponent(JList<? extends Runner> list,
                                                  Runner value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setText(value.getName());
        setIcon(new ImageIcon("star.png")); // 占位
        return this;
    }
}