package players;
//not fully working at the moment
import java.util.ArrayList;
import java.util.List;

import controller.*;

public class SawyerPowel extends Player{
    @Override
    protected List<Card> requestCards(int card, Controller controller) {
        Card[] hand = getHand();
        List<Card> ret =  new ArrayList<Card>();
    if( canWinHonestly(card) ) { //Hoarded enough cards that I won't have to bs ever again, time to win.
      for (Card c : hand) {
            if (c.getNumber() == card) {
                ret.add(c);
            }
        }
    }
    else { // Don't have the cards I'll need in the future. Play my entire hand. Either get more cards or instantly win.
      for (Card c : hand) {
                ret.add(c);
      }
    }
        return ret;
    }

    @Override
    protected boolean bs(Player player, int card, int numberOfCards, Controller controller) {
    //Don't call unless I have to, don't want to lose a random card
        return (player.handSize() <= numberOfCards);
    }

  @Override
    public String toString() {
        return "Hoarder";
    }

  private boolean canWinHonestly(int card) {
    Card[] hand = getHand();
    List<Integer> remainingCards = new ArrayList<Integer>();
    for (Card c : hand) {
      remainingCards.add(c.getNumber());
    }
    while( remainingCards.size() > 0 ) {
      if(remainingCards.contains(card)) {
        remainingCards.remove((Integer) card);
        card = (card + 4) % 13;
      }
      else {
        return false;
      }
    }
    return true;
  }

}
