import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
    private final int maxChairCount;
    private final Lock lock;
    private final Condition cutInProgress;
    private final Condition barberAsleep;
    private final long cutTime;
    private boolean isOccupied;
    private int currentCount;
    private boolean shopOpen;


    public BarberShop(int maxChairCount) {
        this.maxChairCount = maxChairCount;
        this.lock = new ReentrantLock();
        cutInProgress = lock.newCondition();
        barberAsleep = lock.newCondition();
        cutTime = 400;
        isOccupied = false;
        currentCount = 0;
        shopOpen = true;
        new Thread(() -> {
            try {
                while (true) {
                    lock.lock();
                    if(!shopOpen) {
                        break;
                    }
                    if(isOccupied) {
                        cutInProgress.await(cutTime, TimeUnit.MILLISECONDS);
                        System.out.println("FINISHED");
                        if(currentCount == 0) {
                            isOccupied = false;
                        } else {
                            currentCount--;
                        }
                    } else {
                        System.out.printf("Thread: %s - asleep%n", Thread.currentThread().getName());
                        barberAsleep.await();
                        System.out.printf("Thread: %s - Waiking%n", Thread.currentThread().getName());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }, "barber").start();
    }

    public void enter() {
        lock.lock();
        String name = Thread.currentThread().getName();
        if(maxChairCount == currentCount) {
            System.out.printf("Thread: %s shop already full%n", name);
            lock.unlock();
            return;
        }
        if(!isOccupied) {
            isOccupied = true;
            System.out.printf("Thread: %s first to enter. Waking barber%n", name);
            barberAsleep.signalAll();
        } else {
            System.out.printf("Thread: %s Waiting in room%n", name);
            currentCount++;
        }
        lock.unlock();
    }

    public void close() {
        lock.lock();
        shopOpen = false;
        lock.unlock();
    }
}
