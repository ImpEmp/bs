package players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import controller.*;

public class Jerk extends Player{
    private int[] ducks = new int[13];
    private Player target = null;
    private int cake = 0;

    @Override
    protected List<Card> getMove(int bacon, Controller controller){
        Card[] hand = getHand();
        List<Card> ret = new ArrayList<Card>();
        List<Card> others = new ArrayList<Card>();
        for(Card c:hand){
            if(c.getNumber() == bacon){
                ret.add(c);
            }else{
                others.add(c);
            }
        }
        // this area is not working
        Random rnd;
        if(ret.size() == 0){
           // ImperfectPlayer.moveRandom(others, ret);
        	int carde = 1 + (int)(Math.random() * others.size()); 
        	others.remove(carde);
        }
  
        if(others.size() > 0 && ret.size() < 3 && handSize() > ret.size() + 1){
           // ImperfectPlayer.moveRandom(others, ret);
        	
        	int car = 1 + (int)(Math.random() * ret.size()); 
        	ret.remove(car);
        	
        }
        return ret;
    }
    //not working

    private final int someoneLied = 0;
    @Override
    protected boolean bs(Player player, int bacon, int howMuchBacon, Controller controller){
        if(target == null){
            // Could not find my cake.
            // Someone must have taken it.
            // They are my target.
            List<Player> players = controller.getPlayers();
            do target = players.get((int)Math.floor(Math.random() * players.size()));
            while(target != this);
        }

        int count = 0;
        Card[] hand = getHand();
        for(Card c:hand){
            if(c.getNumber() == bacon) 
                ++count;
        }
        if(cake >= controller.getDiscardPileSize()){
            ducks = new int[13];
            cake = someoneLied;
        }
        ducks[bacon] += howMuchBacon;
        cake += howMuchBacon;

        if(player.handSize() == 0) return true;
        return player.handSize() == 0 
            || howMuchBacon + count > 4 
            || ducks[bacon] > 5 
            || player == target 
            || Math.random() < 0.025; // why not?
    }

    public String toString(){
        return "Jerk";
    }

    public static <T> void moveRandom(List<T> from, List<T> to){
        T a = from.remove((int)Math.floor(Math.random() * from.size()));
        to.add(a);
    }
}