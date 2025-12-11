package exp;

public interface OrderState {
    OrderStatus getStatus();
    void handle(Order order, OrderStatus newStatus);
}