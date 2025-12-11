package exp5;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class OrderTableModel extends AbstractTableModel {
    private final List<Order> data;
    private final String[] cols = {"订单号", "描述", "状态", "学生", "跑腿员", "紧急", "创建时间"};

    public OrderTableModel(List<Order> data) { this.data = data; }

    public void refresh() { fireTableDataChanged(); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public Object getValueAt(int r, int c) {
        Order o = data.get(r);
        return switch (c) {
            case 0 -> o.getOrderId();
            case 1 -> o.getDesc();
            case 2 -> o.getStatus();
            case 3 -> o.getStudent().getName();
            case 4 -> o.getRunner() == null ? "" : o.getRunner().getName();
            case 5 -> o.isUrgent() ? "是" : "否";
            case 6 -> o.getCreateTime().toString();
            default -> "";
        };
    }
}