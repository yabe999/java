package exp5;

public enum OrderStatus {
    PENDING,      // 待接单
    DELIVERING,   // 配送中
    COMPLETED,    // 已完成
    CANCELED,     // 已取消
    TIMEOUT       // 超时自动取消
}