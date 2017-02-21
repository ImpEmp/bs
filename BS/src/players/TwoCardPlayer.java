package players;

import java.util.ArrayList;
import java.util.List;

import controller.*;

public class TwoCardPlayer extends Player {

    /* (non-Javadoc)
     * @see controller.Player#requestCards(int, controller.Controller)
     */
    @Override
    // this is having trobuls as it can not get cards
    protected List<Card> requestCards(int card, Controller controller) {
        Card[] hand = getHand();
        List<Card> ret =  new ArrayList<Card>();
        for (Card c : hand) {
            if (c.getNumber() == card) {
                ret.add(c);
            }
        }
        int i=0;
//		while (ret.size() < 2 && i < cards.length) {
//			if (c.getNumber() != card) {
//				ret.add(hand[i]);
//			}
//			i++;
//		}
		return ret;
	}
    Card[] hand = getHand();
    @Override
    protected boolean bs(Player player, int card, int numberOfCards, Controller controller) {
        Card[] hand = getHand();
        int myCards = 0;//How meny of that card do I have.
        for (Card c : hand) {
            if (c.getNumber() == card) {
                myCards += 1;
            }
        }
        return numberOfCards+myCards >= 4;
        //for that to work, he would have to have all the other cards of that number.
    }

    public String toString() {
        //Why would we admit to lying?
        return "Truthful Player";
    }
}