package exp;

public class PendingState implements OrderState {
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PENDING;
    }

    @Override
    public void handle(Order order, OrderStatus newStatus) {
        switch (newStatus) {
            case DELIVERING, CANCELED -> order.setStatus(newStatus);
            default -> throw new InvalidOrderStateException(
                    "Pending → " + newStatus + " 非法");
        }
    }
}