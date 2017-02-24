package players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Calendar;
import controller.Card;
import controller.Controller;
import controller.Player;
import java.util.Random;

public class Drunkard extends Player {
	Random rand = new Random();
    private final List<Integer> knownCardsOnDeck = new ArrayList<>();
    private int lastDeckSize = 0;
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int minutes = cal.get(Calendar.MINUTE);

    @Override
    protected List<Card> getMove(int card, Controller controller) {
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
    protected boolean bs(Player player, int card, int numberOfCards,
            Controller controller) {
        Card[] hand = getHand();
        int myCards = 0;
        for (Card c : hand) {
            if (c.getNumber() == card)
                myCards++;
        }       
        update(controller);
        for (Integer number : knownCardsOnDeck) {
            if (number == card) {
                myCards++;
            }
        }
// need to add 
        // between 14-16 50% wrong
        //0-4 75% wrong
        // 22-24 40%
        // 25% chance everywhere else
        
        boolean potato = player.handSize() == 0
                || numberOfCards > 4
                || myCards + numberOfCards > 4
                || (player.handSize() < 5 && handSize() == 1);; 
        double x = Math.random();
        if (hour>=22 && hour<=24){
        	if(x<.40){
        		if (potato) {
        		    potato = false;
        		} else {
        		    potato = true;
        		}
        	}
        	
        }
        else if (hour>=0 && hour<=4){
        	if(x<.75){
        		if (potato) {
        		    potato = false;
        		} else {
        		    potato = true;
        		}
        	}
        	
        }
        else if (hour>=14 && hour<=16){
        	if(x<.5){
        		if (potato) {
        		    potato = false;
        		} else {
        		    potato = true;
        		}
        	}
        	
        }
        else{
        	if(x<.25){
        		if (potato) {
        		    potato = false;
        		} else {
        		    potato = true;
        		}
        	}
        }
        	
        return potato;
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
        return "drnckrd";
    }
}