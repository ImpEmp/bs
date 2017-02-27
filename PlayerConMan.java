package players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import controller.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
public class PlayerConMan extends Player {
 	String[] list = {"G-man", "HAL", "XERXES", "Thanis", "Farnser", "Milton", "Germany", "FACE"};
 	Random r = new Random();
 	String name =list[r.nextInt(list.length)];
 private enum Location {
  PLAYER_0,
  PLAYER_1,
  PLAYER_2,
  PLAYER_3,
  DISCARD,
  UNKNOWN
 };

 private class MyCard {

  private final int number;
  private Location location;
  private double confidence;
  protected Card card;

  public MyCard(int x) {
   this.number = x;
   location = Location.UNKNOWN;
   confidence = 1.0;
  }

  @Override
  public String toString() {
   if (confidence > 0.75) {
    return "" + number;
   } else if (confidence > 0.25) {
    return number + "*";
   } else {
    return number + "_";
   }
  }
 }
 
 private final ArrayList < ArrayList < MyCard >> theDeck = new ArrayList();
 private Location myLocation;
 private ArrayList < Player > players;
 private final ArrayList < MyCard > myHand = new ArrayList();
 private final HashMap < Location, Integer > sizes = new HashMap();
 private ArrayList < Integer > lies = new ArrayList();
 private ArrayList < Integer > truths = new ArrayList();


 // Constructor
 public PlayerConMan() {
  for (int i = 0; i < 13; ++i) {
   ArrayList < MyCard > set = new ArrayList();
   for (int j = 0; j < 4; ++j) {
    set.add(new MyCard(i));
   }
   theDeck.add(set);
  }
  sizes.put(Location.PLAYER_0, 13);
  sizes.put(Location.PLAYER_1, 13);
  sizes.put(Location.PLAYER_2, 13);
  sizes.put(Location.PLAYER_3, 13);
  sizes.put(Location.DISCARD, 13);
  sizes.put(Location.UNKNOWN, 39);
 }

 //Gets the MyCard for this card, updating a MyCard with the lowest confidence if not already created
 private MyCard getCard(Card c) {
  ArrayList < MyCard > set = theDeck.get(c.getNumber());
  MyCard unknown = null;
  double confidence = 1.0;
  for (MyCard m: set) {
   if (m.card == c) {
    return m;
   }
   if (m.card == null) {
    if (m.location == Location.UNKNOWN) {
     unknown = m;
     confidence = 0.0;
    } else if (m.confidence < confidence || unknown == null) {
     unknown = m;
     confidence = m.confidence;
    }
   }
  }
  unknown.card = c;
  return unknown;
 }

 //Returns the Location of a player
 private Location getLocation(Player p) {
  return Location.values()[players.indexOf(p)];
 }

 @Override
 protected void initialize(Controller controller) {
  super.initialize(controller);
  players = new ArrayList(controller.getPlayers());
  for (Player p: players) {
   if (p == this) {
    myLocation = getLocation(p);
   }
  }
  for (Location loc: Location.values()) {
   sizes.put(loc, 0);
  }
 }

 private ArrayList < Integer > [] getTruthesAndLies(Player player, int card, ArrayList < MyCard > myHand) {
  //Determine our next plays
  int offset = players.indexOf(player);
  int myOffset = players.indexOf(this);
  int nextCard = (card + (myOffset - offset + 4) % 4) % 13;
  ArrayList < Integer > truths = new ArrayList();
  ArrayList < Integer > lies = new ArrayList();
  ArrayList < MyCard > cardsLeft = new ArrayList(myHand);
  while (!cardsLeft.isEmpty()) {
   boolean isLie = true;
   Iterator < MyCard > it = cardsLeft.iterator();
   while (it.hasNext()) {
    MyCard m = it.next();
    if (m.number == nextCard) {
     it.remove();
     isLie = false;
    }
   }
   if (isLie) {
    lies.add(nextCard);
   } else {
    truths.add(nextCard);
   }
   nextCard = (nextCard + 4) % 13;
  }
  return new ArrayList[] {
   truths,
   lies
  };
    
 }

 private void updateDeck(Player player, int card, int numberOfCards, Controller controller) {
  Location loc = getLocation(player);

  //Update from BS
  if (sizes.get(Location.DISCARD) + numberOfCards != controller.getDiscardPileSize()) {

   //Move all cards from DISCARD to the losing player
   //  Losing player defaults to player playing, in the rare case of a tie
   Location losingPlayer = loc;
   Location winningPlayer = null;
   for (Player p: players) {
    Location pLoc = getLocation(p);
    int size = p.handSize();
    if (pLoc == loc) size += numberOfCards;
    if (p.handSize() > sizes.get(pLoc)) {
     losingPlayer = pLoc;
    } else if (size < sizes.get(pLoc)) {
     winningPlayer = pLoc;
    }
   }

   if (winningPlayer == null) {
    debug(losingPlayer + " lost a BS");
   } else {
    debug(losingPlayer + " lied and " + winningPlayer + " lost a card");
   }

   //Move the cards from the discard to the player
   ArrayList < MyCard > winnersHand = new ArrayList();
   for (ArrayList < MyCard > set: theDeck) {
    for (MyCard m: set) {
     if (m.location == Location.DISCARD) {
      if (losingPlayer == myLocation) {
       //If we lost, update the discard cards to unknown;
       //  They'll be updated when we look at our hand
       m.location = Location.UNKNOWN;
       m.confidence = 1.0;
      } else {
       //Move to the losing player
       m.location = losingPlayer;
      }
     } else if (m.location == myLocation && winningPlayer == myLocation) {
      //Update our old cards to the discard pile, in case we won
      m.location = Location.DISCARD;
      m.confidence = 1.0;
     } else if (m.location == winningPlayer) {
      //Add the card to the winner's hand for later processing
      winnersHand.add(m);
     }
    }
   }

   //If someone else won, adjust the probabilities on their cards (I was going to use this for consecutave games or other such things)
   if (winningPlayer != myLocation && winningPlayer != null) {
    int winningSize = players.get(winningPlayer.ordinal()).handSize();
    if (winningPlayer == loc) winningSize += numberOfCards;
    for (MyCard m: winnersHand) {
     m.confidence *= 1 - (1 / winningSize);
    }
   }

  }
  sizes.put(Location.DISCARD, controller.getDiscardPileSize());
  //Update player handSize
  for (Player p: players) {
   sizes.put(getLocation(p), p.handSize());
  }


  //Detect if my hand size has changed
  if (myHand.size() != handSize()) {
   //Update values from my hand
   myHand.clear();
   for (Card c: getHand()) {
    MyCard m = getCard(c);
    m.location = myLocation;
    m.confidence = 1.0;
    myHand.add(m);
   }

   //Determine our next plays and attempt to not tell to minimize total lies
   ArrayList < Integer > tl[] = getTruthesAndLies(player, card, myHand);
   truths = tl[0];
   lies = tl[1];
   debug("Truthes: " + truths);
   debug("Lies: " + lies);
  }
 }


 @Override
 protected List < Card > getMove(int card, Controller controller) {
  updateDeck(this, card, 0, controller);
  @SuppressWarnings("unchecked")
  // need to parameterize
  ArrayList < Card > ret = new ArrayList();
  int pick = card;
  boolean all = true;
  if (truths.get(0) != card) {
   pick = truths.get(truths.size() - 1);
   all = false;
  }

  for (MyCard m: myHand) {
   if (m.number == pick) {
    m.location = Location.DISCARD;
    ret.add(m.card);
    if (!all) break;
   }
  }

  sizes.put(Location.DISCARD, controller.getDiscardPileSize() + ret.size());
  sizes.put(myLocation, myHand.size() - ret.size());
  printTheDeck();

  return ret;
 }

 @Override
 protected boolean bs(Player player, int card, int numberOfCards, Controller controller) {
  updateDeck(player, card, numberOfCards, controller);
  Location loc = getLocation(player);

  //Get total number of unknown cards and total number of cards the player must have
  int handSize = player.handSize() + numberOfCards;
  @SuppressWarnings("unchecked")
  ArrayList < MyCard > playerHand = new ArrayList();
  @SuppressWarnings("unchecked")
  ArrayList < MyCard > discardPile = new ArrayList();
  double totalUnknown = 0;
  double playerUnknown = handSize;
  double cardsHeld = 0;
  double cardsNotHeld = 0;
  for (ArrayList < MyCard > set: theDeck) {
   for (MyCard m: set) {
    if (m.location == Location.UNKNOWN) {
     totalUnknown++;
     // attempt to determane the location of cards using confidence in plays
    } else if (m.location == loc) {
     playerHand.add(m);
     playerUnknown -= m.confidence;
     totalUnknown += 1.0 - m.confidence;
     if (m.number == card) {
      cardsHeld += m.confidence;
     }
    } else {
     if (m.location == Location.DISCARD) {
      discardPile.add(m);
     }
     totalUnknown += 1.0 - m.confidence;
     if (m.number == card) {
      cardsNotHeld += m.confidence;
     }
    }
   }
  }

  boolean callBS = false;
  double prob;
  int possible = (int) Math.round(4 - cardsNotHeld);
  int needed = (int) Math.round(numberOfCards - cardsHeld);
  if (needed > possible) {
   //Player can't possibly have the cards ∴ call it 
   prob = 0.0;
   debug("impossible");
   callBS = true;
  } else if (needed <= 0) {
   //Player guaranteed to have the cards∴ dnt call it 
   prob = 1.0;
   debug("guaranteed");
  } else {
   //The probability that player has needed or more of the possible cards
   double successes = 0;
   for (int i = (int) needed; i <= (int) possible; i++) {
    successes += choose(possible, i) * choose(totalUnknown - possible, playerUnknown - i);
   }
   double outcomes = choose(totalUnknown, playerUnknown);
   prob = successes / outcomes;
   if (Double.isNaN(prob)) {
    prob = 0;
    callBS = true;
   }
   debug("prob = " + new DecimalFormat("0.000").format(prob));
  }

  //Update which cards they may have put down
  //  Assume they put down as many as they could truthfully(need to watch out for human player)
  int cardsMoved = 0;
  Iterator < MyCard > it = playerHand.iterator();
  while (it.hasNext()) {
   MyCard m = it.next();
   if (m.number == card) {
    it.remove();
    m.location = Location.DISCARD;
    discardPile.add(m);
    cardsMoved++;
    if (cardsMoved >= numberOfCards) {
     break;
    }
   }
  }

  //They just put unknowns into play
  //  Adjust existing probabilities and move our lowest confidence cards to the discard
  if (cardsMoved < numberOfCards) {
   //  Reduce the confidence of all remaining cards, in case they lied
   //  Assumes they lie at random
   double cardsLeft = handSize - cardsMoved;
   double cardsNeeded = numberOfCards - cardsMoved;
   double probChosen = 1 * choose(cardsLeft - 1, cardsNeeded - 1) / choose(cardsLeft, cardsNeeded);
   if (Double.compare(cardsLeft, cardsNeeded) == 0) {
    //They're gonna win, call their bluff
	   // its a no loss situation eather they win or they get the entire discard pile
    callBS = true;
    for (MyCard m: playerHand) {
     m.location = Location.DISCARD;
    }
   } else {
    for (MyCard m: playerHand) {
     m.confidence *= (1 - probChosen) * (1 - prob) + prob;
    }
   }

   //  Move any UNKNOWN cards they could have played, assuming they told the truth
   Collections.sort(theDeck.get(card), new Comparator < MyCard > () {
    @Override
    public int compare(MyCard o1, MyCard o2) {
     double p1 = o1.confidence - (o1.location == Location.UNKNOWN ? 10 : 0);
     double p2 = o2.confidence - (o2.location == Location.UNKNOWN ? 10 : 0);
     return (int) Math.signum(p1 - p2);
    }
   });
   // Attempts to determine the total cards that have been put into play that we do not know what they do
   for (MyCard m: theDeck.get(card)) {
    if (m.location == Location.UNKNOWN || m.confidence < prob) {
     m.location = Location.DISCARD;
     m.confidence = prob;
     cardsMoved++;
     discardPile.add(m);
     if (cardsMoved >= numberOfCards) break;
    }
   }
  }

  //Get the confidence of the discardPile
  // Assesses the most licly contents of the discard pile
  double discardPileConfidence = 1.0;
  for (MyCard m: discardPile) {
   discardPileConfidence *= m.confidence;
  }
  discardPileConfidence *= Math.pow(0.5, controller.getDiscardPileSize() - discardPile.size());

  //Call BS if the cards in the discard pile consists only of cards we need / will play
  // Acquires cards it deans necessary
  if (discardPileConfidence > 0.5 && discardPile.size() == controller.getDiscardPileSize()) {
   double truthCount = 0;
   double lieCount = 0;
   double unknownCount = 0;
   for (MyCard m: discardPile) {
    if (truths.contains(m.number)) {
     truthCount += m.confidence;
     unknownCount += 1 - m.confidence;
    } else if (lies.contains(m.number)) {
     lieCount += m.confidence;
     unknownCount += 1 - m.confidence;
    } else {
     unknownCount += 1;
     break;
    }
   }
   if (lieCount > 0 && unknownCount < 1) {
	   // states that is is acquiring cards 
    debug("Strategic BS");
    callBS = true;
   }
  }

  //What's the worst that could happen
  // This is talying the confidence levels and getting them to have confidence in his own plays
  //Test the decks' 
  ArrayList < MyCard > worstHand = new ArrayList < MyCard > (myHand);
  worstHand.addAll(discardPile);
  ArrayList < Integer > loseCase[] = getTruthesAndLies(player, card, worstHand);
  int winPlaysLeft = truths.size() + lies.size();
  int losePlaysLeft = loseCase[0].size() + loseCase[1].size();
  double randomPlaysLeft = Math.max(losePlaysLeft, 7);
  double expectedPlaysLeft = losePlaysLeft * discardPileConfidence + randomPlaysLeft * (1 - discardPileConfidence);
  double threshold = 0.0 - (expectedPlaysLeft - winPlaysLeft) / 13.0;
  debug("winPlaysLeft = " + winPlaysLeft);
  debug("expectedPlaysLeft   = " + expectedPlaysLeft);
  debug("Threshold    = " + threshold);
// determents the limit when he calls bs
  if (lies.isEmpty()) {
   threshold /= 2;
  }
  callBS = callBS || prob < threshold;

  printTheDeck();
  return callBS;
 }

 static double logGamma(double x) {
  double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
  double ser = 1.0 + 76.18009173 / (x + 0) - 86.50532033 / (x + 1) + 24.01409822 / (x + 2) - 1.231739516 / (x + 3) + 0.00120858003 / (x + 4) - 0.00000536382 / (x + 5);
  return tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
 }

 static double gamma(double x) {
  return Math.exp(logGamma(x));
 }

 static double factorial(double x) {
  return x * gamma(x);
 }

 static double choose(double n, double k) {
  if (Double.compare(n, k) == 0 || Double.compare(k, 0) == 0) return 1.0;
  if (k < 0 || k > n) {
   return 0.0;
  }
  return factorial(n) / (factorial(n - k) * factorial(k));
 }

//chooses a random name from a list
 public String toString() {

     return name;
 }
// prints out what it thinks is the deck into the debug condition
 public void printTheDeck() {
  HashMap < Location, ArrayList < MyCard >> map = new HashMap();
  for (Location loc: Location.values()) {
   map.put(loc, new ArrayList());
  }
  for (ArrayList < MyCard > set: theDeck) {
   for (MyCard m: set) {
    map.get(m.location).add(m);
   }
  }
  String ret = "";
  for (Player p: players) {
   ret += p.toString() + ": " + map.get(getLocation(p)) + "\n";
  }
  ret += "Discard pile: " + map.get(Location.DISCARD) + "\n";
  ret += "Unknown: (" + map.get(Location.UNKNOWN).size() + " cards)\n";
  debug(ret);
 }
// the debugging agent
 public void debug(Object s) {

 }
}