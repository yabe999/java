package exp5;

import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String desc;
    private Student student;
    private Runner runner;
    private OrderStatus status;
    private boolean urgent;          // 紧急标志
    private LocalDateTime createTime;

    /* ---------------- 构造 & ID 生成 ---------------- */
    public Order(String desc, Student student, boolean urgent) {
        this.createTime = LocalDateTime.now();
        this.desc = desc;
        this.student = student;
        this.urgent = urgent;
        this.orderId = generateId();
        this.status = OrderStatus.PENDING;
        this.runner = null;
    }
    private String generateId() {
        return createTime.toString().replaceAll("\\D", "") +
                ThreadLocalRandom.current().nextInt(100, 999);
    }

    /* ---------------- 状态机核心 ---------------- */
    public void changeStatus(OrderStatus newStatus) {
        if (status == newStatus) return;
        switch (status) {
            case PENDING -> {
                if (newStatus == OrderStatus.DELIVERING || newStatus == OrderStatus.CANCELED) {
                    setStatus(newStatus);
                } else {
                    throw new InvalidOrderStateException("Pending → " + newStatus + " 非法");
                }
            }
            case DELIVERING -> {
                if (newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELED) {
                    setStatus(newStatus);
                } else {
                    throw new InvalidOrderStateException("Delivering → " + newStatus + " 非法");
                }
            }
            case COMPLETED, CANCELED, TIMEOUT ->
                    throw new InvalidOrderStateException("终态不可再转换");
        }
    }

    /* ---------------- 调度相关 ---------------- */
    public boolean isUrgent() { return urgent; }
    public LocalDateTime getCreateTime() { return createTime; }

    /* ---------------- 持久化 ---------------- */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // 电话脱敏：只写掩码
        out.writeUTF(student.getPhone().substring(0, 3) + "****" + student.getPhone().substring(7));
    }

    /* ---------------- GET / SET ---------------- */
    public String getOrderId() { return orderId; }
    public String getDesc() { return desc; }
    public Student getStudent() { return student; }
    public Runner getRunner() { return runner; }
    public OrderStatus getStatus() { return status; }
    public void setRunner(Runner runner) { this.runner = runner; }
    void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "订单[" + orderId + "] " + desc + "（" + status + "）"
                + " 客户:" + student.getName()
                + (runner == null ? "" : " 跑腿:" + runner.getName())
                + (urgent ? " [紧急]" : "");
    }
}