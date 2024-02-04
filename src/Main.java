import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int numPlayers = 0;

            while (true) {
                try {
                    System.out.print("Enter the number of players : ");
                    numPlayers = scanner.nextInt();

                    if (numPlayers >= 2) {
                        break;
                    } else {
                        System.out.println("Please enter a number of players greater than or equal to 2.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                    scanner.nextLine();
                }
            }

            CardGame game = new OldMaidGame(numPlayers);
            game.startGame();
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
