package exp5;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class UserTableModel extends AbstractTableModel {
    private final List<Student> students;
    private final List<Runner> runners;
    private final String[] cols = {"类型", "ID/姓名", "电话", "专业/状态"};

    public UserTableModel(List<Student> s, List<Runner> r) {
        this.students = s; this.runners = r;
    }

    public void refresh() { fireTableDataChanged(); }

    @Override public int getRowCount() { return students.size() + runners.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public Object getValueAt(int r, int c) {
        if (r < students.size()) {
            Student s = students.get(r);
            return switch (c) {
                case 0 -> "学生";
                case 1 -> s.getName();
                case 2 -> s.getPhone();
                case 3 -> "专业";
                default -> "";
            };
        } else {
            Runner rr = runners.get(r - students.size());
            return switch (c) {
                case 0 -> "跑腿员";
                case 1 -> rr.getName();
                case 2 -> rr.getPhone();
                case 3 -> rr.getStatus();
                default -> "";
            };
        }
    }
}