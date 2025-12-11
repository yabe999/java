package exp;

public class CanceledState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELED;
    }

    @Override
    public void handle(Order order, OrderStatus newStatus) {
        throw new InvalidOrderStateException("已取消订单不可再转换");
    }
}