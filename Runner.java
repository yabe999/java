package exp;

import java.io.Serializable;

public class Runner implements Serializable {
    private final String name;
    private final String phone;
    private String status = "空闲";   // 空闲 / 忙碌

    public Runner(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public String getPhone() {
        return phone;
    }
    public boolean takeOrder() {
        if ("忙碌".equals(status)) return false;
        status = "忙碌";
        return true;
    }
    public void completeOrder() {
        status = "空闲";
    }
    @Override
    public String toString() {
        return name + "[" + status + "]";
    }
}