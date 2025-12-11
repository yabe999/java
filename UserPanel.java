package exp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserPanel extends JPanel {
    private final UserTableModel model;

    public UserPanel() {
        super(new BorderLayout());
        model = new UserTableModel(Main.students, Main.runners);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bar = new JPanel();
        bar.add(new JButton(new NewStudentAction()));
        bar.add(new JButton(new NewRunnerAction()));
        bar.add(new JButton(new RefreshAction()));
        add(bar, BorderLayout.NORTH);
    }

    private class NewStudentAction extends AbstractAction {
        NewStudentAction() { super("新增学生"); }
        @Override public void actionPerformed(ActionEvent e) {
            String n = JOptionPane.showInputDialog("姓名");
            if (n == null || n.isBlank()) return;
            String p = JOptionPane.showInputDialog("电话（11位）");
            if (p == null || !p.matches("\\d{11}")) return;
            String m = JOptionPane.showInputDialog("专业");
            if (m == null) return;
            Main.students.add(UserFactory.createStudent(n, p, m));
            model.refresh();
        }
    }

    private class NewRunnerAction extends AbstractAction {
        NewRunnerAction() { super("新增跑腿员"); }
        @Override public void actionPerformed(ActionEvent e) {
            String n = JOptionPane.showInputDialog("姓名");
            if (n == null || n.isBlank()) return;
            String p = JOptionPane.showInputDialog("电话（11位）");
            if (p == null || !p.matches("\\d{11}")) return;
            Main.runners.add(UserFactory.createRunner(n, p));
            model.refresh();
        }
    }

    private class RefreshAction extends AbstractAction {
        RefreshAction() { super("刷新"); }
        @Override public void actionPerformed(ActionEvent e) { model.refresh(); }
    }
}