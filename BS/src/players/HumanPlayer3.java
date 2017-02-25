package players;
import java.util.ArrayList;
import java.util.Arrays;
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
    protected List<Card> getMove(int card, Controller controller) {
        Card[] hand = getHand();
       
        System.out.println("tis your turn");	
        List<Card> ret =  new ArrayList<Card>();
        int cont = 1;
        int resp = 123456789;
       int temp = 0;
       int re = 1234567;
        while(cont==1){
        	System.out.println(Arrays.toString(hand));
        	 resp = Controller.reader.nextInt();
         for (Card c : hand) {  
           if (c.getNumber() == resp) {
             ret.add(c);
             temp = 1;
             System.out.println("do you want to another card of this value?");
             re = Controller.reader.nextInt();
             if (re ==0){
        	 break;
             }
           }  
         }
         // makes it so they have to play a card
         if(temp == 1){
        	 
         }
         else if(temp == 0){
        	  while (temp == 0){
        		  System.out.println("please play an achual card");	
        		  for (Card c : hand) {  
        			  resp = Controller.reader.nextInt();
        	           if ((int)c.getNumber() == resp) {
        	             ret.add(c);
        	             temp = 0;
        	             break;
        	             
        	           }  
        	         }
        	  }  
          } 
        System.out.println("do you want to play another card");	
        cont =1;
        cont = Controller.reader.nextInt();
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
    Card[] hand = getHand();
    	System.out.println("*Do you want to call BS?");
   	System.out.println(Arrays.toString(hand));
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
        return "Dave";
    }
}
