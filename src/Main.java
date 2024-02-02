
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of players: ");

        int numPlayers = scanner.nextInt();

        CardGame game = new OldMaidGame(numPlayers);

        game.startGame();
        scanner.close();
    }
}

