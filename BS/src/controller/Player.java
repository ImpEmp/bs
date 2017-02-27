package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Collections;

public abstract class Player {
	private List<Card> hand = new ArrayList<>();	
	protected abstract List<Card> getMove(int card, Controller controller);
	protected abstract boolean bs(Player player, int card, int numberOfCards, Controller controller);
	protected void update(Controller controller) {	
	}	
	protected void initialize(Controller controller) {		
	}
	protected Card[] getHand() {
		Card[] returnValue = new Card[hand.size()];
		hand.toArray(returnValue);
		return returnValue;
	}	
	protected Card[] sortHand() {
		Card[] returnValue = new Card[hand.size()];
		//int[] array = new int[hand.size()];
	    Card[] bar = hand.toArray(returnValue);
	    Arrays.sort(bar);
		return bar;
	}	
	void drawCards(List<Card> cards) {
		hand.addAll(cards);
	}	
	Card removeRandomCard(Random rnd) {
		int card = rnd.nextInt(hand.size());
		Card returnCard = hand.get(card);
		hand.remove(card);
		return returnCard;
	}
	void removeCards(Set<Card> cards) {
		for (Card card : cards) {
			hand.remove(card);
		}	
	}
	public int handSize() {
		return hand.size();
	}
	public String toString() {
		return this.getClass().getCanonicalName();
	}
	String handAsString() {
		return hand.toString();
	}
}
