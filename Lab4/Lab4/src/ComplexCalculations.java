import javafx.application.Platform;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class ComplexCalculations {
    private int number;
    private volatile boolean paused = false;
    private Thread calculationThread;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    public ComplexCalculations(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    private void performComplexCalculation(int num) {
        long upperLimit = num;
        StringBuilder result = new StringBuilder();

        for (long i = 2; i <= upperLimit; i++) {
            if (paused) {
                lock.lock();
                try {
                    while (paused) {
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return; // Exit if interrupted while waiting
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }

            if (isPrime(i)) {
                result.append(i).append(", ");

                // Вивести просте число на екрані
                long finalI = i;
                Platform.runLater(() -> System.out.println("Prime number: " + finalI));
                try {
                    Thread.sleep(500); // adjust the speed of the floating text
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    return; // Exit if interrupted during sleep
                }
            }
        }

        // Вивести "The End" на екрані
        Platform.runLater(() -> System.out.println("The End"));
        pauseCalculation();
    }
    private boolean isPrime(long number) {
        if (Thread.interrupted()) {
            return false; // Вихід, якщо потік був перерваний
        }
        if (number < 2) return false;
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
    public void pauseCalculation() {
        paused = true;
    }
    public void resumeCalculation() {
        lock.lock();
        try {
            paused = false;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    public void stopCalculation() {
        if (calculationThread != null && calculationThread.isAlive()) {
            calculationThread.interrupt();
        }
    }
    public void startCalculation() {
        calculationThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                lock.lock();
                try {
                    while (paused) {
                        try {
                            condition.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return; // Виходимо з потоку, якщо отримали InterruptedException
                        }
                    }
                } finally {
                    lock.unlock();
                }

                performComplexCalculation(number);

                // Other actions if needed
                try {
                    Thread.sleep(1000); // Example delay of 1 second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return; // Виходимо з потоку, якщо отримали InterruptedException
                }
            }

            // Output "The End" when calculation is complete
            Platform.runLater(() -> System.out.println("The End"));
        });
        calculationThread.start();
    }
}
