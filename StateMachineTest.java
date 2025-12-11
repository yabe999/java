package exp5;

public class StateMachineTest {
    public static void main(String[] args) {
        System.out.println("==== 状态机矩阵测试（无 JUnit）====");

        // T1: Pending → Delivering
        Order o1 = new Order("奶茶", new Student("S1", "Alice", "123****1234", "CS"), false);
        o1.changeStatus(OrderStatus.DELIVERING);
        assert o1.getStatus() == OrderStatus.DELIVERING : "T1 失败";

        // T2: Pending → Canceled
        Order o2 = new Order("咖啡", new Student("S2", "Bob", "123****5678", "SE"), false);
        o2.changeStatus(OrderStatus.CANCELED);
        assert o2.getStatus() == OrderStatus.CANCELED : "T2 失败";

        // T3: Pending → Completed（应抛异常）
        try {
            o1.changeStatus(OrderStatus.COMPLETED); // 已完成不能回退
            assert false : "T3 应抛异常";
        } catch (InvalidOrderStateException e) {
            System.out.println("T3 异常正确: " + e.getMessage());
        }

        // T4: Completed → Any（应抛异常）
        try {
            o1.changeStatus(OrderStatus.PENDING);
            assert false : "T4 应抛异常";
        } catch (InvalidOrderStateException e) {
            System.out.println("T4 异常正确: " + e.getMessage());
        }

        System.out.println("✅ 所有状态机测试通过！");
    }
}