package exp5;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class OrderPanel extends JPanel {
    private final OrderTableModel model;
    private final JTable table;

    public OrderPanel() {
        super(new BorderLayout());
        model = new OrderTableModel(Main.orders);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        /* ---- 彩色状态列 ---- */
        TableColumn statusCol = table.getColumnModel().getColumn(2); // 第3列=状态
        statusCol.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof OrderStatus s) {
                    switch (s) {
                        case PENDING    -> c.setBackground(Color.YELLOW);
                        case DELIVERING -> c.setBackground(Color.ORANGE);
                        case COMPLETED  -> c.setBackground(Color.GREEN);
                        case CANCELED   -> c.setBackground(Color.RED);
                        default         -> c.setBackground(Color.WHITE);
                    }
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        statusCol.setPreferredWidth(80); // 保证看得见

        /* ---- 按钮栏 ---- */
        JPanel bar = new JPanel();
        bar.add(new JButton(new PlaceOrderAction()));
        bar.add(new JButton(new AssignOrderAction()));
        bar.add(new JButton(new CompleteOrderAction()));
        bar.add(new JButton(new CancelOrderAction()));
        bar.add(new JButton(new WithdrawOrderAction(table)));
        bar.add(new JButton(new ManualSaveAction()));
        bar.add(new JButton(new RefreshAction()));
        add(bar, BorderLayout.NORTH);

        /* ---- 日志区 ---- */
        JTextArea log = new JTextArea(3, 0);
        log.setEditable(false);
        log.setText("提示：每5min自动保存，每30s检测超时订单。");
        AutoSaveManager.logArea = log;
        TimeoutManager.logArea = log;
        add(new JScrollPane(log), BorderLayout.SOUTH);
    }

    /* ---------------- 内部动作类 ---------------- */
    private class PlaceOrderAction extends AbstractAction {
        PlaceOrderAction() { super("下单"); }
        @Override public void actionPerformed(ActionEvent e) {
            new PlaceOrderDialog(SwingUtilities.windowForComponent(OrderPanel.this)).setVisible(true);
            model.refresh();
        }
    }

    private class AssignOrderAction extends AbstractAction {
        AssignOrderAction() { super("分配订单"); }
        @Override public void actionPerformed(ActionEvent e) {
            List<Order> pending = Main.orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.PENDING)
                    .collect(Collectors.toList());
            if (pending.isEmpty()) {
                JOptionPane.showMessageDialog(OrderPanel.this, "没有待接单！");
                return;
            }
            Order order = (Order) JOptionPane.showInputDialog(
                    OrderPanel.this, "选择订单：", "分配订单",
                    JOptionPane.QUESTION_MESSAGE, null,
                    pending.toArray(), pending.get(0));
            if (order == null) return;

            List<Runner> free = Main.runners.stream()
                    .filter(r -> "空闲".equals(r.getStatus()))
                    .collect(Collectors.toList());
            if (free.isEmpty()) {
                JOptionPane.showMessageDialog(OrderPanel.this, "没有空闲跑腿员！");
                return;
            }
            Runner runner = (Runner) JOptionPane.showInputDialog(
                    OrderPanel.this, "选择跑腿员：", "分配订单",
                    JOptionPane.QUESTION_MESSAGE, null,
                    free.toArray(), free.get(0));
            if (runner == null) return;

            if (runner.takeOrder()) {
                order.setRunner(runner);
                order.changeStatus(OrderStatus.DELIVERING);
                model.refresh();
                JOptionPane.showMessageDialog(OrderPanel.this, "分配成功！");
            } else {
                JOptionPane.showMessageDialog(OrderPanel.this, "跑腿员忙碌中！");
            }
        }
    }

    /* ---------------- 完成订单（释放跑腿员） ---------------- */
    private class CompleteOrderAction extends AbstractAction {
        CompleteOrderAction() { super("完成订单"); }
        @Override public void actionPerformed(ActionEvent e) {
            List<Order> delivering = Main.orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.DELIVERING)
                    .collect(Collectors.toList());
            if (delivering.isEmpty()) {
                JOptionPane.showMessageDialog(OrderPanel.this, "没有配送中的订单！");
                return;
            }
            Order order = (Order) JOptionPane.showInputDialog(
                    OrderPanel.this, "选择要完成的订单：", "完成订单",
                    JOptionPane.QUESTION_MESSAGE, null,
                    delivering.toArray(), delivering.get(0));
            if (order == null) return;

            order.changeStatus(OrderStatus.COMPLETED);
            // 关键：释放跑腿员
            if (order.getRunner() != null) {
                order.getRunner().completeOrder();
            }
            model.refresh();
            JOptionPane.showMessageDialog(OrderPanel.this, "订单已完成！");
        }
    }

    /* ---------------- 取消订单（释放跑腿员） ---------------- */
    private class CancelOrderAction extends AbstractAction {
        CancelOrderAction() { super("取消订单"); }
        @Override public void actionPerformed(ActionEvent e) {
            List<Order> canCancel = Main.orders.stream()
                    .filter(o -> o.getStatus() == OrderStatus.PENDING
                            || o.getStatus() == OrderStatus.DELIVERING)
                    .collect(Collectors.toList());
            if (canCancel.isEmpty()) {
                JOptionPane.showMessageDialog(OrderPanel.this, "没有可取消的订单！");
                return;
            }
            Order order = (Order) JOptionPane.showInputDialog(
                    OrderPanel.this, "选择要取消的订单：", "取消订单",
                    JOptionPane.QUESTION_MESSAGE, null,
                    canCancel.toArray(), canCancel.get(0));
            if (order == null) return;

            // 若已分配，释放跑腿员
            if (order.getStatus() == OrderStatus.DELIVERING && order.getRunner() != null) {
                order.getRunner().completeOrder();
            }
            order.changeStatus(OrderStatus.CANCELED);
            model.refresh();
            JOptionPane.showMessageDialog(OrderPanel.this, "订单已取消！");
        }
    }

    /* ---------------- 撤回订单（仅 PENDING） ---------------- */
    private class WithdrawOrderAction extends AbstractAction {
        private final JTable table;
        WithdrawOrderAction(JTable table) { super("撤回订单"); this.table = table; }

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(OrderPanel.this, "请先选择要撤回的订单！");
                return;
            }
            Order order = Main.orders.get(row);
            if (order.getStatus() != OrderStatus.PENDING) {
                JOptionPane.showMessageDialog(OrderPanel.this, "只有【待接单】可撤回！");
                return;
            }
            int ok = JOptionPane.showConfirmDialog(OrderPanel.this,
                    "确认撤回订单 " + order.getOrderId() + "？",
                    "撤回确认", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;

            order.changeStatus(OrderStatus.CANCELED);
            model.refresh();
            JOptionPane.showMessageDialog(OrderPanel.this, "订单已撤回！");
        }
    }

    /* ---------------- 手动保存 ---------------- */
    private class ManualSaveAction extends AbstractAction {
        ManualSaveAction() { super("手动保存"); }
        @Override public void actionPerformed(ActionEvent e) {
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() throws Exception {
                    Main.saveData(); return null;
                }
                @Override protected void done() {
                    JOptionPane.showMessageDialog(OrderPanel.this, "保存完成！");
                }
            }.execute();
        }
    }

    /* ---------------- 刷新 ---------------- */
    private class RefreshAction extends AbstractAction {
        RefreshAction() { super("刷新"); }
        @Override public void actionPerformed(ActionEvent e) { model.refresh(); }
    }
}