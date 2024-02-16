import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.Iterator;
class Player implements Runnable {
    private final String name;
    private final List<Card> hand;
    private final List<Player> players;
    private final Object gameLock;
    private static volatile int currentPlayerIndex = 0;
    private static volatile boolean gameOngoing = true;
    public Player(String name, List<Player> players, Semaphore turnSemaphore, Object gameLock) {
        this.name = name;
        this.players = players;
        this.hand = new ArrayList<>();
        this.gameLock = gameLock;

    }

    @Override
    public void run() {
        while (gameOngoing) {
            if(players.size()>1) {
                takeTurn();
            }
            else {break;}
        }
    }

    private void takeTurn() {
        synchronized (gameLock) {
            try {
                while (!isPlayerTurn()) {
                    if (players.size() > 1) {
                        gameLock.wait();
                    } else {
                        break;
                    }
                }

                Player nextPlayer = getNextPlayerInTurnOrder();

                if (hand.isEmpty()) {
                    players.remove(this);
                    System.out.println(name + " has an empty hand and is the winner in the game!");
                } else {
                    takeRandomCard(nextPlayer);
                    checkMatchingPair();
                }
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                gameLock.notifyAll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }



    private boolean isPlayerTurn() {
        return players.indexOf(this) == currentPlayerIndex;
    }
    private Player getNextPlayerInTurnOrder() {
        return players.get((currentPlayerIndex + 1) % players.size());
    }

    private void takeRandomCard(Player player) {
        if (!player.equals(this) && !player.getHand().isEmpty()) {
            List<Card> playerHand = player.getHand();
            Collections.shuffle(playerHand);

            Iterator<Card> iterator = playerHand.iterator();
            if (iterator.hasNext()) {
                Card randomCard = iterator.next();
                iterator.remove();
                hand.add(randomCard);
                System.out.println(name + " took a card from " + player.getName() + " which is " + randomCard);
            }
        }
    }

    private void checkMatchingPair() {
        for (int i = 0; i <= hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                Card card1 = hand.get(i);
                Card card2 = hand.get(j);

                if (card1.isMatchingPair(card2)) {
                    hand.remove(card1);
                    hand.remove(card2);

                    System.out.println(name + " discarded matching pair: " + card1 + " and " + card2);

                    i--;
                    break;
                }
            }
        }
        printAllHands();
    }

   public void receiveCard(Card card) {
        hand.add(card);
    }
    private void printAllHands() {
        for (Player player : players) {
            System.out.println(player.getName() + "'s hand: " + player.getHand());
        }
        System.out.println();
    }


    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

}
