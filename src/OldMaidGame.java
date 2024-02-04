import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
class OldMaidGame implements CardGame {
    private final List<Player> players;
    private final List<Card> deck;
    private final Object gameLock = new Object();//My synchronization Object

    public OldMaidGame(int numPlayers) {
        players = new ArrayList<>();
        deck = initializeDeck();
        initializePlayers(numPlayers);
        distributeCards();
    }
    Object getGameLock() {
        return gameLock;
    }
    private List<Card> initializeDeck() {
        List<Card> newDeck = new ArrayList<>();
        String[] cardTypes = {"Spades", "Clubs", "Diamonds", "Hearts"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
        String[] colors = {"Black", "Black", "Red", "Red"};

        for (int i = 0; i < cardTypes.length; i++) {
            for (String value : values) {
                newDeck.add(new Card(value, cardTypes[i], colors[i]));
            }
        }
        newDeck.add(new Card("Joker", "None", "Colorless"));

        Collections.shuffle(newDeck);
        return newDeck;
    }

    private void initializePlayers(int numPlayers) {
        Semaphore turnSemaphore = new Semaphore(0);
        for (int i = 1; i <= numPlayers; i++) {
            players.add(new Player("Player " + i, players, turnSemaphore, getGameLock()));
        }
    }

    @Override
    public void startGame() {
        Thread[] playerThreads = new Thread[players.size()];

        for (int i = 0; i < players.size(); i++) {
            playerThreads[i] = new Thread(players.get(i), "PlayerThread-" + (i + 1));
            playerThreads[i].start();
        }

        try {
            for (Thread thread : playerThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        synchronized (gameLock) {
            gameLock.notifyAll();
        }

        displayResults();
    }

    @Override
    public void displayResults() {
        if (players.size() == 1) {
            Player Loser = players.get(0);
            System.out.println(Loser.getName() + " is the Loser! The last player remaining with Joker.");
        }
            System.out.println();
    }

    private void distributeCards() {
        int playerIndex = 0;

        for (Card card : deck) {
            Player currentPlayer = players.get(playerIndex);
            currentPlayer.receiveCard(card);
            playerIndex = (playerIndex + 1) % players.size();
        }

        displayDistribution();
    }

    private void displayDistribution() {
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

}