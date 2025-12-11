package exp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class UserPanel extends JPanel {
    private final UserTableModel model;
    private final JTable table;          // 提升为成员变量，方便删除动作引用

    public UserPanel() {
        super(new BorderLayout());
        model = new UserTableModel(Main.students, Main.runners);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bar = new JPanel();
        bar.add(new JButton(new NewStudentAction()));
        bar.add(new JButton(new NewRunnerAction()));
        bar.add(new JButton(new DeleteUserAction()));   // ← 新增注销按钮
        bar.add(new JButton(new ManualSaveAction()));
        bar.add(new JButton(new RefreshAction()));
        add(bar, BorderLayout.NORTH);
    }

    /* ---------------- 新增学生 ---------------- */
    private class NewStudentAction extends AbstractAction {
        NewStudentAction() { super("新增学生"); }
        @Override
        public void actionPerformed(ActionEvent e) {
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
        @Override
        public void actionPerformed(ActionEvent e) {
            String n = input("姓名", true);
            if (n == null) return;
            String p = input("电话（11位）", false, "\\d{11}", "必须为 11 位数字！");
            if (p == null) return;
            Main.runners.add(UserFactory.createRunner(n, p));
            model.refresh();
            JOptionPane.showMessageDialog(UserPanel.this, "跑腿员创建成功！");
        }
    }

    /* ---------------- 注销（删除）用户 ---------------- */
    private class DeleteUserAction extends AbstractAction {
        DeleteUserAction() { super("删除用户"); }
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(UserPanel.this, "请先选择要删除的用户！");
                return;
            }
            String type = table.getValueAt(row, 0).toString();
            String name = table.getValueAt(row, 1).toString();

            /* ---------- 进行中订单检测 ---------- */
            long running = Main.orders.stream()
                    .filter(o -> {
                        if (type.equals("学生")) {
                            return o.getStudent().getName().equals(name)
                                    && (o.getStatus() == OrderStatus.PENDING
                                    || o.getStatus() == OrderStatus.DELIVERING);
                        }
                        if (type.equals("跑腿员")) {
                            return o.getRunner() != null
                                    && o.getRunner().getName().equals(name)
                                    && o.getStatus() == OrderStatus.DELIVERING;
                        }
                        return false;
                    })
                    .count();

            if (running > 0) {
                JOptionPane.showMessageDialog(UserPanel.this,
                        "该" + type + "仍存在【进行中】订单，无法删除！\n请先完成或取消相关订单。");
                return;
            }

            int ok = JOptionPane.showConfirmDialog(UserPanel.this,
                    "确认删除 " + type + "：" + name + "？", "删除确认",
                    JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            model.removeRow(row);
            JOptionPane.showMessageDialog(UserPanel.this, "已删除！");
        }
    }

    /* ---------------- 手动保存 ---------------- */
    private class ManualSaveAction extends AbstractAction {
        ManualSaveAction() { super("手动保存"); }
        @Override
        public void actionPerformed(ActionEvent e) {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    Main.saveData();
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

    /* ---------------- 通用输入 ---------------- */
    private String input(String field, boolean nonEmpty) {
        return input(field, nonEmpty, null, null);
    }
    private String input(String field, boolean nonEmpty, String regex, String errMsg) {
        while (true) {
            String val = JOptionPane.showInputDialog(field);
            if (val == null) return null;
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