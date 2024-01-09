import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene mainScene = new Scene(root, 665, 50);
        primaryStage.setScene(mainScene);

        Button startBallButton = new Button("Start Ball Animation");
        Button startCalculationButton = new Button("Start Complex Calculation");
        Button startTextButton = new Button("Start Text Generation");

        startBallButton.setOnAction(event -> {
            Stage ballStage = new Stage();
            BallAnimation ballAnimation = new BallAnimation(new Pane(), 30, 30, 30, 140, 2, 2);
            ballAnimation.initializeBall();
            Scene ballScene = new Scene(ballAnimation.getPane(), 400, 300);
            ballStage.setScene(ballScene);
            ballStage.show();

            Button pauseBallButton = new Button("Pause");
            Button resumeBallButton = new Button("Resume");
            Button stopBallButton = new Button("Stop");

            pauseBallButton.setOnAction(e -> {
                ballAnimation.pauseAnimation();
            });

            resumeBallButton.setOnAction(e -> {
                ballAnimation.resumeAnimation();
            });

            stopBallButton.setOnAction(e -> {
                ballAnimation.stopAnimation();
                ballStage.close();
            });

            HBox ballButtons = new HBox(pauseBallButton, resumeBallButton, stopBallButton);
            ballButtons.setLayoutX(10);
            ballButtons.setLayoutY(10);
            ((Pane) ballScene.getRoot()).getChildren().addAll(ballButtons);

            Thread ballThread = new Thread(() -> {
                ballAnimation.startAnimation();
            });
            ballThread.start();
            ballStage.setTitle("Ball form");
        });

        startCalculationButton.setOnAction(event -> {
            Stage calculationStage = new Stage();
            ComplexCalculations complexCalculations = new ComplexCalculations(1000);
            Scene calculationScene = new Scene(new Pane(), 400, 200);
            calculationStage.setScene(calculationScene);
            calculationStage.show();

            Button pauseCalculationButton = new Button("Pause");
            Button resumeCalculationButton = new Button("Resume");
            Button stopCalculationButton = new Button("Stop");

            pauseCalculationButton.setOnAction(e -> {
                complexCalculations.pauseCalculation();
            });

            resumeCalculationButton.setOnAction(e -> {
                complexCalculations.resumeCalculation();
            });

            stopCalculationButton.setOnAction(e -> {
                complexCalculations.stopCalculation();
                calculationStage.close();
            });

//            calculationStage.setOnCloseRequest(e -> {
//                complexCalculations.stopCalculation();
//            });

            HBox calculationButtons = new HBox(pauseCalculationButton, resumeCalculationButton, stopCalculationButton);
            calculationButtons.setLayoutX(10);
            calculationButtons.setLayoutY(10);
            ((Pane) calculationScene.getRoot()).getChildren().addAll(calculationButtons);

            Thread calculationThread = new Thread(() -> {
                complexCalculations.startCalculation();
            });
            calculationThread.start();
            calculationStage.setTitle("Calculation form");
        });

        startTextButton.setOnAction(event -> {
            Stage textStage = new Stage();
            TextGeneration textGeneration = new TextGeneration("Floating Text");
            Scene textScene = new Scene(new Pane(), 800, 50);
            textStage.setScene(textScene);
            textStage.show();

            Button pauseTextButton = new Button("Pause");
            Button resumeTextButton = new Button("Resume");
            Button stopTextButton = new Button("Stop");

            pauseTextButton.setOnAction(e -> {
                textGeneration.pauseText();
            });

            resumeTextButton.setOnAction(e -> {
                textGeneration.resumeText();
            });

            stopTextButton.setOnAction(e -> {
                textGeneration.stopText();
                textStage.close();
            });

            HBox textButtons = new HBox(pauseTextButton, resumeTextButton, stopTextButton);
            textButtons.setLayoutX(10);
            textButtons.setLayoutY(10);
            ((Pane) textScene.getRoot()).getChildren().addAll(textButtons);

            Thread textThread = new Thread(() -> {
                textGeneration.startText();
            });
            textThread.start();
            textStage.setTitle("Text form");
        });

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> {
            primaryStage.close();
            Platform.exit();
        });

        HBox buttons = new HBox(startBallButton, startCalculationButton, startTextButton, exitButton);
        buttons.setLayoutX(10);
        buttons.setLayoutY(10);
        root.getChildren().addAll(buttons);

        primaryStage.setTitle("Main");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}