import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Player extends Thread {
    private final String name;
    private final List<Card> hand;
    private final List<Player> players;
    private int currentPlayerIndex;
    private final Semaphore turnSemaphore;

    public Player(String name, List<Player> players, Semaphore turnSemaphore) {
        super(name);
        this.name = name;
        this.players = players;
        hand = new ArrayList<>();
        currentPlayerIndex = players.indexOf(this);
        this.turnSemaphore = turnSemaphore;
    }

    @Override
    public void run() {
        while (!allHandsEmptyExceptOne()) {
            try {
                turnSemaphore.acquire();
                checkMatchingPair();
                Player nextPlayer = getNextPlayerInTurnOrder();
                Card card = takeRandomCard(nextPlayer);
                if (card != null) {
                    checkMatchingPair();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                turnSemaphore.release();
            }
        }
    }

    private Player getNextPlayerInTurnOrder() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return players.get(currentPlayerIndex);
    }

    private void printAllHands() {
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand: " + player.getHand());
        }
        System.out.println();
    }

    private Card takeRandomCard(Player player) {
        if (!player.equals(this) && !player.getHand().isEmpty()) {
            List<Card> playerHand = player.getHand();
            Collections.shuffle(playerHand);
            Card randomCard = playerHand.remove(0);
            hand.add(randomCard);
            System.out.println(name + " took a card from " + player.getName() + " which is " + randomCard);
            return randomCard;
        }
        return null;
    }

    private void checkMatchingPair() {
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                Card card1 = hand.get(i);
                Card card2 = hand.get(j);

                if (card1.isMatchingPair(card2)) {
                    hand.remove(card1);
                    hand.remove(card2);

                    System.out.println(name + " discarded matching pair: " + card1 + " and " + card2);
                    printAllHands();
                    i--;
                    break;
                }
            }
        }
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public List<Card> getHand() {
        return hand;
    }

    private boolean allHandsEmptyExceptOne() {
        int nonEmptyHands = 0;

        for (Player player : players) {
            if (!player.getHand().isEmpty()) {
                nonEmptyHands++;
            }
        }

        return nonEmptyHands == 1;
    }
}

