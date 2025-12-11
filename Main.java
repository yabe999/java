package exp5;

import java.io.*;
import java.util.*;
public class Main {
    public static List<Student> students = new ArrayList<>();
    public static List<Runner> runners = new ArrayList<>();
    public static List<Order> orders = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadOnStartup();
        while (true) {
            System.out.println("\n===校园跑腿系统（能力层）===");
            System.out.println("1. 创建学生");
            System.out.println("2. 创建跑腿员");
            System.out.println("3. 下单（可设紧急）");
            System.out.println("4. 分配订单（紧急优先）");
            System.out.println("5. 查看订单");
            System.out.println("6. 完成订单（状态机）");
            System.out.println("7. 保存数据（序列化）");
            System.out.println("8. 加载数据（反序列化）");
            System.out.println("9. 状态机测试（JUnit）");
            System.out.println("0. 退出");
            System.out.print("请选择: ");
            switch (sc.nextLine()) {
                case "1" -> createStudent();
                case "2" -> createRunner();
                case "3" -> placeOrder();
                case "4" -> assignOrder();
                case "5" -> listOrders();
                case "6" -> completeOrder();
                case "7" -> saveData();
                case "8" -> loadOnStartup();
                case "9" -> runJUnitTest();
                case "0" -> { saveOnExit(); System.out.println("谢谢使用！"); return; }
                default -> System.out.println("无效选择！");
            }
        }
    }

    /* ---------- 以下为原第二层代码，省略展示，与之前完全一致 ---------- */
    private static void createStudent() {
        String name = readLine("姓名: ");
        String phone = readPhone();
        String major = readLine("专业: ");
        students.add(UserFactory.createStudent(name, phone, major));
        System.out.println("已创建学生: " + students.get(students.size() - 1));
    }
    private static void createRunner() {
        String name = readLine("姓名: ");
        String phone = readPhone();
        runners.add(UserFactory.createRunner(name, phone));
        System.out.println("已创建跑腿员: " + runners.get(runners.size() - 1));
    }
    private static void placeOrder() {
        if (students.isEmpty()) { System.out.println("暂无学生，请先创建"); return; }
        System.out.print("订单描述: ");
        String desc = sc.nextLine();
        for (int i = 0; i < students.size(); i++) System.out.println((i + 1) + ". " + students.get(i).getName());
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        boolean urgent = readLine("是否紧急订单(y/n): ").trim().equalsIgnoreCase("y");
        orders.add(new Order(desc, students.get(idx), urgent));
        System.out.println("下单成功！ " + orders.get(orders.size() - 1));
    }
    private static void assignOrder() {
        if (orders.isEmpty() || runners.isEmpty()) { System.out.println("订单或跑腿员为空"); return; }
        List<Order> pending = new OrderScheduler().schedule(orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).toList());
        if (pending.isEmpty()) { System.out.println("没有待接单"); return; }
        for (int i = 0; i < pending.size(); i++) System.out.println((i + 1) + ". " + pending.get(i));
        int oIdx = Integer.parseInt(sc.nextLine()) - 1;
        if (oIdx < 0 || oIdx >= pending.size()) return;
        Order order = pending.get(oIdx);
        for (int i = 0; i < runners.size(); i++) System.out.println((i + 1) + ". " + runners.get(i));
        int rIdx = Integer.parseInt(sc.nextLine()) - 1;
        if (rIdx < 0 || rIdx >= runners.size()) return;
        Runner runner = runners.get(rIdx);
        if (runner.takeOrder()) {
            order.setRunner(runner);
            order.changeStatus(OrderStatus.DELIVERING);
            System.out.println("分配成功！ " + order);
        } else System.out.println("该跑腿员忙碌中，分配失败");
    }
    private static void listOrders() {
        if (orders.isEmpty()) { System.out.println("暂无订单"); return; }
        orders.forEach(System.out::println);
    }
    private static void completeOrder() {
        if (orders.isEmpty()) { System.out.println("暂无订单"); return; }
        var delivering = orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERING).toList();
        if (delivering.isEmpty()) { System.out.println("没有配送中的订单"); return; }
        for (int i = 0; i < delivering.size(); i++) System.out.println((i + 1) + ". " + delivering.get(i));
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        if (idx < 0 || idx >= delivering.size()) return;
        Order order = delivering.get(idx);
        order.changeStatus(OrderStatus.COMPLETED);
        order.getRunner().completeOrder();
        System.out.println("订单已完成！ " + order);
    }
    public static void saveData() {
        try {
            PersistenceManager.save(students, "students.dat");
            PersistenceManager.save(runners, "runners.dat");
            PersistenceManager.save(orders, "orders.dat");
            System.out.println("数据已保存到 data/ 目录");
        } catch (Exception ex) { System.out.println("保存失败: " + ex.getMessage()); }
    }
    public static void loadOnStartup() {
        try {
            students = (List<Student>) PersistenceManager.load("students.dat");
            runners = (List<Runner>) PersistenceManager.load("runners.dat");
            orders = (List<Order>) PersistenceManager.load("orders.dat");
            System.out.println("启动时全部数据已恢复");
        } catch (Exception ex) {
            System.out.println("无历史数据，全新开始");
            students = new ArrayList<>();
            runners = new ArrayList<>();
            orders = new ArrayList<>();
        }
    }
    private static void runJUnitTest() { StateMachineTest.main(new String[0]); }
    private static void saveOnExit() {
        try { PersistenceManager.save(orders, "orders.dat"); } catch (Exception ignore) {}
    }
    private static String readLine(String prompt) { System.out.print(prompt); return sc.nextLine(); }
    private static String readPhone() {
        while (true) {
            System.out.print("电话(11位): ");
            String p = sc.nextLine();
            if (p.matches("\\d{11}")) return p;
            System.out.println("电话必须为11位数字，请重新输入！");
        }
    }
}