import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GraphPlotter extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Color graphColor = Color.RED;
    private int graphStroke = 2; // Товщина ліній для функції
    private int axisStroke = 2; // Товщина ліній для осей координат
    private int graphStyle = BasicStroke.CAP_ROUND;

    private double scaleX = 1.0;
    private double scaleY = 1.0;

    private final double a = 9.0; // Параметр a
    private final double m = 0.5; // Параметр m
    private final double maxTheta = 10 * Math.PI; // Максимальний кут (10 обертань)
    private final double dTheta = 0.01; // Крок зміни кута

    public GraphPlotter() {
        setTitle("Graph Plotter");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new GraphComponent());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScale();
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // When mouse is clicked, change the color, style, and thickness of the graph lines
                Random rand = new Random();
                graphColor = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                graphStroke = rand.nextInt(5) + 1;
                int[] availableCapStyles = {BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE};
                int randomCapStyle = availableCapStyles[rand.nextInt(availableCapStyles.length)];
                graphStyle = randomCapStyle;
                repaint();
            }
        });
    }

    class GraphComponent extends JComponent {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Calculate centerX and centerY based on the size of the component
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Draw the function graph with the updated style and color
            drawFunction(g, centerX, centerY);

            // Draw the axes
            drawAxes(g, centerX, centerY);

            // Draw the author and variant number in the top left corner
            int fontSize = 13;
            int fontStyle = Font.BOLD;
            Font font = new Font("Arial", fontStyle, fontSize);
            g.setFont(font);
            g.drawString("Author: Andrii Yatsuliak", 10, 20);
            g.drawString("Variant: 17", 10, 40); // Змінимо висоту, щоб тексти були різного розміру
        }

        private void drawFunction(Graphics g, int centerX, int centerY) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(graphColor);
            g2d.setStroke(new BasicStroke(graphStroke, graphStyle, BasicStroke.JOIN_ROUND));

            double prevX = centerX + a * Math.pow(Math.PI, m) * Math.cos(m * 0);
            double prevY = centerY - a * Math.pow(Math.PI, m) * Math.sin(m * 0);

            for (double theta = 0; theta <= maxTheta; theta += dTheta) {
                double r = Math.pow(a, m) * Math.cos(m * theta);

                // Масштабування координат відповідно до нового розміру вікна
                double x = centerX + 40*r * Math.cos(theta) * scaleX;
                double y = centerY - 40*r * Math.sin(theta) * scaleY;

                g2d.drawLine((int) prevX, (int) prevY, (int) x, (int) y);

                prevX = x;
                prevY = y;
            }
        }

        private void drawAxes(Graphics g, int centerX, int centerY) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(axisStroke)); // Встановлення товщини ліній для осей координат

            int xAxisY = centerY;
            int yAxisX = centerX;

            g2d.drawLine(0, xAxisY, getWidth(), xAxisY); // X-axis
            g2d.drawLine(yAxisX, 0, yAxisX, getHeight()); // Y-axis
        }
    }

    private void updateScale() {
        scaleX = (double) getWidth() / WIDTH;
        scaleY = (double) getHeight() / HEIGHT;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GraphPlotter plotter = new GraphPlotter();
            plotter.setVisible(true);
        });
    }
}










