package exp;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class WithdrawOrderAction extends AbstractAction {
    private final JTable table;
    public WithdrawOrderAction(JTable table) {
        super("撤回订单");
        this.table = table;
    }
    @Override public void actionPerformed(ActionEvent e) {
        int r = table.getSelectedRow();
        if (r == -1) return;
        Order o = Main.orders.get(r);
        if (o.getStatus() != OrderStatus.PENDING) {
            JOptionPane.showMessageDialog(table, "仅待接单可撤回！");
            return;
        }
        o.changeStatus(OrderStatus.CANCELED);
        ((OrderTableModel) table.getModel()).refresh();
    }
}