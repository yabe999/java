package exp;

import javax.swing.*;
import java.awt.*;

public class PlaceOrderDialog extends JDialog {
    public PlaceOrderDialog(Window owner) {
        super(owner, "下单", ModalityType.APPLICATION_MODAL);
        setSize(400, 300);
        setLocationRelativeTo(owner);

        JPanel p = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField descF = new JTextField();
        JCheckBox urgentC = new JCheckBox("紧急");
        JComboBox<Student> stuBox = new JComboBox<>(Main.students.toArray(new Student[0]));
        p.add(new JLabel("订单描述")); p.add(descF);
        p.add(new JLabel("学生")); p.add(stuBox);
        p.add(urgentC);

        JButton ok = new JButton("确定");
        ok.addActionListener(e -> {
            if (descF.getText().isBlank() || stuBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "请完整输入");
                return;
            }
            Order o = new Order(descF.getText(), (Student) stuBox.getSelectedItem(), urgentC.isSelected());
            Main.orders.add(o);
            dispose();
        });
        add(p, BorderLayout.CENTER);
        add(ok, BorderLayout.SOUTH);
    }
}