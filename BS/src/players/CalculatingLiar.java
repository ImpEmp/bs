package players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import controller.Card;
import controller.Controller;
import controller.Player;

public class CalculatingLiar extends Player {
    private final List<Integer> knownCardsOnDeck = new ArrayList<>();
    private int lastDeckSize = 0;
	String[] list = {"Finland", "Russia", "Latvia", "Lithuania", "Bendy", "Lebenon", "Hanibal", "Nixon", "Philipe"};
	Random r = new Random();
	String name =list[r.nextInt(list.length)];
    @Override
    protected List<Card> getMove(int card, Controller controller) {
        Card[] hand = getHand();
        List<Card> ret =  new ArrayList<Card>();
        for (Card c : hand) {
            if (c.getNumber() == card) {
                ret.add(c);
            }
        }
        // makes it so that the #of cards played is never zero
        if (ret.size() == 0) {
            ret.add(calculateWorstCard(card));
        }

        update(controller);
// adds the cards played to the internal pile count aproximation
        for (Card c : ret) {
            knownCardsOnDeck.add(c.getNumber());
        }
        lastDeckSize = controller.getDiscardPileSize() + ret.size();
        return ret;
    }

    @Override
    protected boolean bs(Player player, int card, int numberOfCards,
            Controller controller) {
        Card[] hand = getHand();
        int myCards = 0;
        for (Card c : hand) {
            if (c.getNumber() == card)
                myCards++;
        }       
        update(controller);
        // instantiated and updats the known cards on the deck
        for (Integer number : knownCardsOnDeck) {
            if (number == card) {
                myCards++;
            }
        }
// the cases where the computer will call it (it only calls when it is for sure lying)
        return player.handSize() == 0
                || numberOfCards > 4
                || myCards + numberOfCards > 4
                || (player.handSize() < 5 && handSize() == 1);
    }

    @Override
    protected void initialize(Controller controller) {
        knownCardsOnDeck.clear();
        lastDeckSize = 0;
    }

    @Override
    // updates the known values to see what has changed
    protected void update(Controller controller) {
        if (lastDeckSize > controller.getDiscardPileSize()) {
            knownCardsOnDeck.clear();
            lastDeckSize = controller.getDiscardPileSize();
        } else {
            lastDeckSize = controller.getDiscardPileSize();
        }
    }
// sorts the cards in the order they will be used in.
    private Card calculateWorstCard(int currentCard) {
        List<Integer> cardOrder = new ArrayList<>();

        int nextCard = currentCard;
        do {
            cardOrder.add(nextCard);
            nextCard = (nextCard + 4) % 13;
        } while (nextCard != currentCard);
        Collections.reverse(cardOrder);

        Card[] hand = getHand();
        for (Integer number : cardOrder) {
            for (Card card : hand) {
                if (card.getNumber() == number) {
                    return card;
                }
            }
        }
        //never happens
        return null;
    }

    @Override
    // chooses a random name from a list
    public String toString() {

        return name;
    }
}