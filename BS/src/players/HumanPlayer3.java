package players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import controller.Card;
import controller.Controller;
import controller.Player;
// need to find where it calls bs

public class HumanPlayer3 extends Player {
    private final List<Integer> knownCardsOnDeck = new ArrayList<>();
    private int lastDeckSize = 0;

    @Override
    protected List<Card> requestCards(int card, Controller controller) {
        Card[] hand = getHand();
        List<Card> ret =  new ArrayList<Card>();
        for (Card c : hand) {
            if (c.getNumber() == card) {
                ret.add(c);
            }
        }
        if (ret.size() == 0) {
            ret.add(calculateWorstCard(card));
        }

        update(controller);

        for (Card c : ret) {
            knownCardsOnDeck.add(c.getNumber());
        }
        lastDeckSize = controller.getDiscardPileSize() + ret.size();
        return ret;
    }

    @Override
    //bs??
    protected boolean bs(Player player, int card, int numberOfCards,
            Controller controller) {
//        Card[] hand = getHand();
//        int myCards = 0;
//        for (Card c : hand) {
//            if (c.getNumber() == card)
//                myCards++;
//        }       
//        update(controller);
//        for (Integer number : knownCardsOnDeck) {
//            if (number == card) {
//                myCards++;
//            }
//        }
//
//        return player.handSize() == 0
//                || numberOfCards > 4
//                || myCards + numberOfCards > 4
//                || (player.handSize() < 5 && handSize() == 1);
    	System.out.println("*Do you want to call BS?");
    	int resp = Controller.reader.nextInt();
    	if(resp == 1)
    		return true;
    	else if(resp == 0)
    		return false;
    	else
    	{
    		System.out.println("I didn't understand that.");
    		return bs(player, card, numberOfCards, controller);
    	}
    }

    @Override
    protected void initialize(Controller controller) {
        knownCardsOnDeck.clear();
        lastDeckSize = 0;
    }

    @Override
    protected void update(Controller controller) {
        if (lastDeckSize > controller.getDiscardPileSize()) {
            knownCardsOnDeck.clear();
            lastDeckSize = controller.getDiscardPileSize();
        } else {
            lastDeckSize = controller.getDiscardPileSize();
        }
    }

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
    public String toString() {
        return "human";
    }
}