package exp5;

public interface OrderState {
    OrderStatus getStatus();
    void handle(Order order, OrderStatus newStatus);
}