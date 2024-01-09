import java.util.Scanner;
public class Second
{
    public static void main(String[] args) {
        // Створення об'єкта Scanner для зчитування введення користувача
        Scanner scanner = new Scanner(System.in);

        // Запит користувачу ввести слова, розділені комами
        System.out.print("Enter words separated by commas: ");
        String input = scanner.nextLine();

        // Розділити введений рядок на масив слів за допомогою коми
        String[] words = input.split(",");

        // Перетворити масив слів на масив без зайвих пробілів
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].trim();
        }

        String[] new_words = new String[words.length];

        // Заміна g на th
        for(int i = 0; i < words.length; i++)
        {
            new_words[i] = words[i].replace("g", "th");
        }

        // Вивести отриманий масив слів
        System.out.println("\tAn array of words:");
        for (String word : new_words) {
            System.out.println(word);
        }

        // Закрити об'єкт Scanner
        scanner.close();
    }
}
