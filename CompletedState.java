package exp;

public class CompletedState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.COMPLETED;
    }

    @Override
    public void handle(Order order, OrderStatus newStatus) {
        throw new InvalidOrderStateException("已完成订单不可再转换");
    }
}