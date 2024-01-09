import javafx.application.Platform;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TextGeneration extends Thread {
    private String text;
    private volatile boolean paused = false;
    private Thread textThread;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    public TextGeneration(String text) {
        this.text = text;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public void pauseText() {
        paused = true;
    }
    public void resumeText() {
        paused = false;
        lock.lock();
        try {
            condition.signal(); // Use signal() instead of notify()
        } finally {
            lock.unlock();
        }
    }
    public void stopText() {
        if (textThread != null && textThread.isAlive()) {
            textThread.interrupt();
        }
    }
    public void startText() {
        textThread = new Thread(() -> {
            int screenWidth = 800; // змініть на ширину вашого вікна
            int textPosition = screenWidth;

            while (!Thread.interrupted()) {
                lock.lock();
                try {
                    while (paused) {
                        try {
                            condition.await(); // Use await() instead of wait()
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } finally {
                    lock.unlock();
                }

                //int finalTextPosition = textPosition;
                Platform.runLater(() -> {
                    System.out.println(text + " ");
                });

                textPosition--;
                if (textPosition + text.length() < 0) {
                    textPosition = screenWidth;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        textThread.start();
    }
}
