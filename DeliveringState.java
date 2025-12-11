package exp5;

public class DeliveringState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DELIVERING;
    }

    @Override
    public void handle(Order order, OrderStatus newStatus) {   // ← 无空格
        switch (newStatus) {
            case COMPLETED, CANCELED -> order.setStatus(newStatus);
            default -> throw new InvalidOrderStateException(
                    "Delivering → " + newStatus + " 非法");
        }
    }
}