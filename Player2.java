package players;

import java.util.ArrayList;
import java.util.List;

import controller.*;

public class Player2 extends Player {
	protected List<Card> getMove(int card, Controller controller) {
		Card[] hand = getHand();
		List<Card> ret =  new ArrayList<Card>();
		for (Card c : hand) {
			if (c.getNumber() == card) {
				ret.add(c);
			}
		}
		if (ret.size() == 0) {
			ret.add(hand[0]);
			if (hand.length > 1) ret.add(hand[1]);
		}
		return ret;
	}

	@Override
	protected boolean bs(Player player, int card, int numberOfCards,
			Controller controller) {
		return false;
	}
	
	public String toString() {
		return "Player 3";
	}
}