package exp;

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

    @Override
    public Object getValueAt(int r, int c) {
        Order o = data.get(r);
        return switch (c) {
            case 0 -> o.getOrderId();
            case 1 -> o.getDesc();
            case 2 -> switch (o.getStatus()) {          // ← 中文状态
                case PENDING    -> "待接单";
                case DELIVERING -> "配送中";
                case COMPLETED  -> "已完成";
                case CANCELED   -> "已取消";
                case TIMEOUT    -> "超时";
                default         -> o.getStatus().toString();
            };
            case 3 -> o.getStudent().getName();
            case 4 -> o.getRunner() == null ? "" : o.getRunner().getName();
            case 5 -> o.isUrgent() ? "是" : "否";
            case 6 -> o.getCreateTime().toString();
            default -> "";
        };
    }
}