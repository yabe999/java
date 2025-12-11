package exp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OrderPanel extends JPanel {
    private final OrderTableModel model;
    private final JTable table;

    public OrderPanel() {
        super(new BorderLayout());
        model = new OrderTableModel(Main.orders);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bar = new JPanel();
        bar.add(new JButton(new PlaceOrderAction()));
        bar.add(new JButton(new RefreshAction()));
        bar.add(new JButton(new WithdrawOrderAction(table)));
        bar.add(new JButton(new ManualSaveAction()));
        add(bar, BorderLayout.NORTH);

        JTextArea log = new JTextArea(3, 0);
        log.setEditable(false);
        log.setText("提示：每5min自动保存，每30s检测超时订单。");
        AutoSaveManager.logArea = log;
        TimeoutManager.logArea = log;
        add(new JScrollPane(log), BorderLayout.SOUTH);
    }

    private class PlaceOrderAction extends AbstractAction {
        PlaceOrderAction() { super("下单"); }
        @Override public void actionPerformed(ActionEvent e) {
            new PlaceOrderDialog(SwingUtilities.windowForComponent(OrderPanel.this)).setVisible(true);
            model.refresh();
        }
    }

    private class RefreshAction extends AbstractAction {
        RefreshAction() { super("刷新"); }
        @Override public void actionPerformed(ActionEvent e) { model.refresh(); }
    }

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
}