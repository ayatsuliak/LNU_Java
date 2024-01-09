import java.util.Random;
import java.util.Scanner;
public class First
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of rows: ");
        int rows = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter number of columns: ");
        int cols = Integer.parseInt(scanner.nextLine());

        int[][] matrix = generateRandomMatrix(rows, cols); // Генеруємо випадкову матрицю

        // Виводимо початкову матрицю
        System.out.println("\tStart matrix:");
        printMatrix(matrix);

        int[] vector = calculateMaxSumDigitsVector(matrix);

        // Виводимо вектор
        System.out.println("\n\tA vector where each element is the maximum sum of the digits of the line:");
        printVector(vector);
    }

    // Генерує випадкову матрицю
    public static int[][] generateRandomMatrix(int rows, int cols)
    {
        int[][] matrix = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(100); // Генеруємо випадкове число від 0 до 99
            }
        }

        return matrix;
    }

    // Обчислює вектор, де кожен елемент - максимум суми цифр рядка матриці
    public static int[] calculateMaxSumDigitsVector(int[][] matrix)
    {
        int rows = matrix.length;
        int[] vector = new int[rows];

        for (int i = 0; i < rows; i++) {
            int maxSum = 0;

            for (int j = 0; j < matrix[i].length; j++) {
                maxSum = Math.max(maxSum, sumOfDigits(matrix[i][j]));
            }

            vector[i] = maxSum;
        }

        return vector;
    }

    // Обчислює суму цифр числа
    public static int sumOfDigits(int number)
    {
        int sum = 0;

        while (number > 0) {
            sum += number % 10;
            number /= 10;
        }

        return sum;
    }

    // Виводить матрицю на екран
    public static void printMatrix(int[][] matrix)
    {
        for (int[] row : matrix) {
            for (int element : row) {
                System.out.print(element + "\t");
            }
            System.out.println();
        }
    }

    // Виводить вектор на екран
    public static void printVector(int[] vector)
    {
        for (int element : vector) {
            System.out.print(element + "\t");
        }
    }
}
