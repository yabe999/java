package exp5;

import java.util.*;

public class OrderScheduler {
    /**
     * 紧急+时间双条件排序：
     * 1. 紧急订单在前
     * 2. 同紧急程度按创建时间升序
     */
    public List<Order> schedule(List<Order> orders) {
        List<Order> list = new ArrayList<>(orders);
        list.sort((o1, o2) -> {
            int urgent = Boolean.compare(o2.isUrgent(), o1.isUrgent()); // 紧急在前
            if (urgent != 0) return urgent;
            return o1.getCreateTime().compareTo(o2.getCreateTime());   // 时间升序
        });
        return list;
    }

    /**
     * 返回最合适的 Runner（目前：空闲即可）
     */
    public Runner assignRunner(List<Runner> runners) {
        return runners.stream()
                .filter(r -> "空闲".equals(r.getStatus()))
                .findFirst()
                .orElse(null);
    }
}