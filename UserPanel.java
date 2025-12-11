package exp5;

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
        bar.add(new JButton(new ManualSaveAction())); // ← 新增
        bar.add(new JButton(new RefreshAction()));
        add(bar, BorderLayout.NORTH);
    }

    /* ---------------- 新增学生 ---------------- */
    private class NewStudentAction extends AbstractAction {
        NewStudentAction() { super("新增学生"); }
        @Override public void actionPerformed(ActionEvent e) {
            String n = input("姓名", true);
            if (n == null) return;
            String p = input("电话（11位）", false, "\\d{11}", "必须为 11 位数字！");
            if (p == null) return;
            String m = input("专业", true);
            if (m == null) return;

            Main.students.add(UserFactory.createStudent(n, p, m));
            model.refresh();
            JOptionPane.showMessageDialog(UserPanel.this, "学生创建成功！");
        }
    }

    /* ---------------- 新增跑腿员 ---------------- */
    private class NewRunnerAction extends AbstractAction {
        NewRunnerAction() { super("新增跑腿员"); }
        @Override public void actionPerformed(ActionEvent e) {
            String n = input("姓名", true);
            if (n == null) return;
            String p = input("电话（11位）", false, "\\d{11}", "必须为 11 位数字！");
            if (p == null) return;

            Main.runners.add(UserFactory.createRunner(n, p));
            model.refresh();
            JOptionPane.showMessageDialog(UserPanel.this, "跑腿员创建成功！");
        }
    }

    /* ---------------- 手动保存 ---------------- */
    private class ManualSaveAction extends AbstractAction {
        ManualSaveAction() { super("手动保存"); }
        @Override public void actionPerformed(ActionEvent e) {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    Main.saveData(); // 保存全部（学生+跑腿员+订单）
                    return null;
                }
                @Override protected void done() {
                    JOptionPane.showMessageDialog(UserPanel.this, "保存完成！");
                }
            }.execute();
        }
    }

    /* ---------------- 刷新 ---------------- */
    private class RefreshAction extends AbstractAction {
        RefreshAction() { super("刷新"); }
        @Override public void actionPerformed(ActionEvent e) { model.refresh(); }
    }

    /* ---------------- 通用输入循环 ---------------- */
    private String input(String field, boolean nonEmpty) {
        return input(field, nonEmpty, null, null);
    }

    private String input(String field, boolean nonEmpty, String regex, String errMsg) {
        while (true) {
            String val = JOptionPane.showInputDialog(field);
            if (val == null) return null;          // 用户点取消
            if (nonEmpty && val.isBlank()) {
                JOptionPane.showMessageDialog(this, field + "不能为空！");
                continue;
            }
            if (regex != null && !val.matches(regex)) {
                JOptionPane.showMessageDialog(this, errMsg);
                continue;
            }
            return val;
        }
    }
}