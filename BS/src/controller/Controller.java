package controller;
//line 139 is bs
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import players.*;


public class Controller {
	public final static Scanner reader = new Scanner(System.in);
	private List<Card> discardPile;
	private List<Player> players;
	public Random rnd = new Random();
	private int card;
	private static final Class<?>[] playerClasses = {// the set of players
		CalculatingLiar.class, HumanPlayer3.class,/****Jerk.class, Player2.class,FiveCardThinkAhead.class,TwoCardPlayer.class,Drunkard.class,Player4.class,SawyerPowel.class*****/RiskTaker.class, PlayerConMan.class,
	};	
	public static void main(String[] args) {
		Controller c = new Controller();
		Map<Class<?>, Integer> scores = new HashMap<Class<?>, Integer>();
		for (Class<?> playerClass : playerClasses) {
			scores.put(playerClass, 0);
		}
		// while a player has not won the game continues
		for (int i = 0; i < 1000; ++i) {
			Player winner = c.playGame(false);
			Class<?> winnerClass = winner.getClass();
			scores.put(winnerClass, scores.get(winnerClass) + 1); 
		}
		System.out.println(scores);
	}
	// where it inishis the players into the game
	private List<Class<?>> getPlayerClasses() {
		List<Class<?>> playerClassList = Arrays.asList(playerClasses);
		Collections.shuffle(playerClassList, rnd);
		return playerClassList;
	}
	//setting up the game
	public Player playGame(boolean toLog) {
		//	boolean potato;
		System.out.println("would you like to see the printout (one for yes, zero for no): ");
		try {
			int n = reader.nextInt();
// the print out selector
			if (n == 1){
			toLog = true;	
			}
			else if (n == 0){	
				toLog = false;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
  // toLog= true;
		players = new ArrayList<>();
		discardPile = new ArrayList<>();
		for (Class<?> playerClass : getPlayerClasses()) {
			try {
				players.add((Player) playerClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
			if (players.size() == 4) break;
		}
		List<Card> deck = new ArrayList<Card>(52);
		for (int i = 0; i < 13; ++i) {
			for (int j = 0; j < 4; ++j) {
				deck.add(new Card(i));
			}
		}
		// shuffle the deck
		Collections.shuffle(deck, rnd);
		//drawing the cards
		for (int i = 0; i < 4; ++i) {
			Player p = players.get(i);
			p.drawCards(deck.subList(i * 13, (i + 1) * 13));
		}
		for (Player p : players) {
			p.initialize(this);
		}
		//inishilizing the players
		if (toLog) {
			System.out.println(this.asString());
		}
		for (int i = 0; i < 1000; ++i) {
			runRound(players, toLog);
			
			if (toLog) {
				System.out.println(this.asString());
			}
			Player winner = getWinner();
			if (winner != null) {
				if (toLog) {
					System.out.println(winner + " won!");
				}
				return winner;
			}
		}
		return null;
	}
	// both gets the winner and determans when someone has won
	private Player getWinner() {
		for (Player p : players) {
			if (p.handSize() == 0) return p;
		}
		return null;
	}
	// where it actually runs
	private void runRound(List<Player> players, boolean toLog) {
		for (Player player : players) {
			//Iterator<String> it = player.iterator();
			Set<Card> cardsPlayed = new HashSet<Card>(player.getMove(card, this));
		//	System.out.println(" Iterating over HashSet in Java current object: " + Arrays.toString( player.cardsPlayed()));
			if (cardsPlayed.size() == 0) throw new RuntimeException("Player " + player + " played zero cards this is illigal");
			boolean isBS = false;
			for (Card c : cardsPlayed) {//marker to say if he is lying or not
				if (c.getNumber() != card) {
					isBS = true;
					break;
				}
			}
			
			if (toLog) {
				System.out.print(player + " played " + cardsPlayed.size() + " cards of " + card);
				// uncomment for debug mode 
				//if (isBS) System.out.print(" (which are actually " + cardsPlayed + ")");
				System.out.println();
				StringBuilder str = new StringBuilder();
				for (Player p : players) {
					System.out.print(p);
					System.out.print(": ");
					//str.append(p.handAsString());
					System.out.print(" " + p.handSize() + " cards");
					System.out.print('\n');
				}
				System.out.print("Discard pile: ");
				//System.out.print(discardPile);
				System.out.print(" " + discardPile.size() + " cards");
				System.out.print('\n');
			}
			
			
			player.removeCards(cardsPlayed);
			discardPile.addAll(cardsPlayed);
			
			List<Player> bs = new ArrayList<>();
			for (Player p : players) {
				if (p == player) continue;
				if (p.bs(player, card, cardsPlayed.size(), this)) {
					bs.add(p);
				}
			}
			if (bs.size() != 0) {
				//decides who is the player that is the active bs'er
				Player playerThinksBS = bs.get(rnd.nextInt(bs.size()));
				
				if (toLog) {
					System.out.println(playerThinksBS + " called BS");
				}
				// Doling out the cards to the deserving owner
				if (isBS) {
					player.drawCards(discardPile);
					discardPile.clear();
					discardPile.add(playerThinksBS.removeRandomCard(rnd));
				} else {
					playerThinksBS.drawCards(discardPile);
					discardPile.clear();
				}
			}
			card++;
			card %= 13;
			if (getWinner() != null) {
				break;
			}
		}
		for (Player p : players) {
			p.update(this);
		}
	}

	public int getDiscardPileSize() {
		return discardPile.size();
	}
	public List<Player> getPlayers() {
		return players;
	}
	private String asString() {
		StringBuilder str = new StringBuilder();
		for (Player p : players) {
			str.append(p);
			str.append(": ");
			//str.append(p.handAsString());
			str.append(" " + p.handSize() + " cards");
			str.append('\n');
		}
		str.append("Discard pile: ");
		//str.append(discardPile);
		str.append(" " + discardPile.size() + " cards");
		str.append('\n');
		return str.toString();
		
	}
}
