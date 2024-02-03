import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

public class OldMaidGame implements CardGame {
    private final List<Player> players;
    private final List<Card> deck;
    private final Semaphore turnSemaphore;

    public OldMaidGame(int numPlayers) {
        players = new ArrayList<>();
        deck = initializeDeck();
        turnSemaphore = new Semaphore(1);
        initializePlayers(numPlayers);
        distributeCards();
    }

    private List<Card> initializeDeck() {
        List<Card> newDeck = new ArrayList<>();

        String[] cardTypes = {"Spades", "Clubs", "Diamonds", "Hearts"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        String[] colors = {"Black", "Black", "Red", "Red"};

        for (int i = 0; i < cardTypes.length; i++) {
            for (int j = 0; j < values.length; j++) {
                newDeck.add(new Card(values[j], cardTypes[i], colors[i]));
            }
        }
        newDeck.add(new Card("Joker", "None", "Colorless"));

        Collections.shuffle(newDeck);

        return newDeck;
    }

    private void initializePlayers(int numPlayers) {
        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("Player " + i, players, turnSemaphore));
        }
    }
    private void distributeCards() {
        Iterator<Card> deckIterator = deck.iterator();
        int playerIndex = 0;

        while (deckIterator.hasNext()) {
            Player currentPlayer = players.get(playerIndex);
            Card currentCard = deckIterator.next();
            currentPlayer.receiveCard(currentCard);
            deckIterator.remove();
            playerIndex = (playerIndex + 1) % players.size();
        }

        System.out.println("----------- Cards number after distribution ----------- ");
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand size: " + player.getHand().size());
        }

        System.out.println("------- Hand of each Player after distribution ------- ");
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand: " + player.getHand());
        }
        System.out.println("--------------------------------------------");
    }

    @Override
    public void startGame() {
        for (Player player : players) {
            player.start();
        }

        try {
            for (Player player : players) {
                player.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        displayResults();
    }

    @Override
    public void displayResults() {
        Player loser = null;
        List<Player> winners = new ArrayList<>();

        for (Player player : players) {
            if (hasJoker(player)) {
                loser = player;
            } else {
                winners.add(player);
            }
        }

        if (loser != null) {
            System.out.println();
            System.out.println(loser.getName() + " is the loser! They did not have the Joker.");
        } else {
            System.out.println("No player has the Joker!");
        }

        System.out.print("Winners:");
        for (Player winner : winners) {
            System.out.print(" "+winner.getName());
        }
        System.out.println();
    }


    private boolean hasJoker(Player player) {
        for (Card card : player.getHand()) {
            if ("Joker".equals(card.getValue())) {
                return true;
            }
        }
        return false;
    }
}

