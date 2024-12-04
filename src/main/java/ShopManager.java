import java.util.Random;

public class ShopManager {
    private static final Random random = new Random();
    private static final BarberShop shop = new BarberShop(4);
    public static void main(String[] args) throws InterruptedException {
        for(int i=0;i<50;i++) {
            add("Customer-"+i);
        }
    }

    private static void add(String name) throws InterruptedException {
        Thread th = new Thread(() -> {
            long time = random.nextInt(70) * 10;
            System.out.printf("Thread: %s - next customer after%s%n", name, time);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            shop.enter();
        }, name);
        th.start();
        th.join();
    }
}
