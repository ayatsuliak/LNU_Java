import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
public class BallAnimation {
    private Circle ball;
    private Pane pane;
    private volatile boolean paused = false;
    private Thread animationThread;
    private double x, y;
    private double radius;
    private double speed;
    private double velocityX;
    private double velocityY;
    public BallAnimation(Pane pane, double x, double y, double radius, double speed, double velocityX, double velocityY) {
        this.pane = pane;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }
    public Circle initializeBall() {
        ball = new Circle(x, y, radius);
        ball.setFill(Color.RED);
        pane.getChildren().add(ball);
        return ball;
    }
    public Pane getPane() {
        return pane;
    }
    public void pauseAnimation() {
        paused = true;
    }
    public void resumeAnimation() {
        paused = false;
        synchronized (animationThread) {
            animationThread.notify();
        }
    }
    public void stopAnimation() {
        if (animationThread != null && animationThread.isAlive()) {
            animationThread.interrupt();
        }
    }
    public void startAnimation() {animationThread = new Thread(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (animationThread) {
                while (paused) {
                    try {
                        animationThread.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            double newX = ball.getCenterX() + speed * velocityX * 0.016;
            double newY = ball.getCenterY() + speed * velocityY * 0.016;

            if (newX - radius < 0 || newX + radius > pane.getWidth()) {
                velocityX = -velocityX;
            }
            if (newY - radius < 0 || newY + radius > pane.getHeight()) {
                velocityY = -velocityY;
            }

            Platform.runLater(() -> {
                ball.setCenterX(newX);
                ball.setCenterY(newY);
            });

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    });

        animationThread.start();
    }
}