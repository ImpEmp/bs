package players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import controller.Card;
import controller.Controller;
import controller.Player;
// need to find where it calls bs

public class HumanPlayer3 extends Player
{
	private final List<Integer> knownCardsOnDeck = new ArrayList<>();
	private int lastDeckSize = 0;

	@Override
	protected List<Card> getMove(int card, Controller controller)
	{
		Card[] hand = getHand();

		System.out.println("tis your turn");
		List<Card> ret = new ArrayList<Card>();
		// int resp = 123456789;
		// int temp = 0;
		// int re = 1234567;
		boolean next = true;
		int trash = 0;
		while (next)
		{
			try
			{
				System.out.println("What card would you like to put down? (It is expected that you play " + Card.toString(card) + ")");
				
				System.out.print("Hand: [");
				for(int count = 0; count < hand.length; count++)
				{
					if(trash > 0 && count == hand.length - trash)
						System.out.print("already place down: ");
					System.out.print(hand[count] + ((count == hand.length - 1) ? "]\n" : ", "));
				}
				
				String resp = Controller.reader.next();
				boolean found = false;
				for (int i = 0; i < hand.length - trash; i++)
				{
					if (hand[i].toString().equals(resp))
					{
						ret.add(hand[i]);
						
						final int l = hand.length - trash - 1;
						Card c = hand[i];
						hand[i] = hand[l];
						hand[l] = c;
						trash++;
						
						while (true)
						{
							System.out.println("Would you like to enter another card?");
							resp = Controller.reader.next();
							if (resp.toLowerCase().matches("y|(yes)|1"))
							{
								next = true;
								break;
							}

							if (resp.toLowerCase().matches("n|(no)|0"))
							{
								next = false;
								break;
							}

							System.out.println("I didn't understand that answer. Can you enter it again?");
						}
						
						found = true;
					}
				}
				
				if(found)
					continue;
				
				System.out.println("You do not appear to have that card in your hand.");
			}
			catch (Exception e)
			{
				System.out.println("OOPS! I ran into an issue. Let's go back a step.");
			}
			// System.out.println(Arrays.toString(hand));
			// resp = Controller.reader.nextInt();
			// for (Card c : hand)
			// {
			// if (c.getNumber() == resp)
			// {
			// ret.add(c);
			// temp = 1;
			// System.out.println("do you want to another card of this value?");
			// re = Controller.reader.nextInt();
			// if (re == 0)
			// {
			// break;
			// }
			// }
			// }
			// // makes it so they have to play a card
			// if (temp == 1)
			// {
			//
			// }
			// else if (temp == 0)
			// {
			// while (temp == 0)
			// {
			// System.out.println("please play an achual card");
			// for (Card c : hand)
			// {
			//
			// if ((int) c.getNumber() == resp)
			// {
			// ret.add(c);
			// temp = 0;
			// break;
			//
			// }
			// }
			// }
			// }
			// System.out.println("do you want to play another card");
			// cont = 1;
			// cont = Controller.reader.nextInt();
			// }
			//
			// update(controller);
			//
			// for (Card c : ret)
			// {
			// knownCardsOnDeck.add(c.getNumber());
			// }
			// lastDeckSize = controller.getDiscardPileSize() + ret.size();
			// return ret;
		}
		
		return ret;
	}

	@Override
	// bs??
	protected boolean bs(Player player, int card, int numberOfCards, Controller controller)
	{
		Card[] hand = getHand();
		System.out.println("*Do you want to call BS?");
		System.out.println(Arrays.toString(hand));
		int resp = Controller.reader.nextInt();
		if (resp == 1)
			return true;
		else if (resp == 0)
			return false;
		else
		{
			System.out.println("I didn't understand that.");
			return bs(player, card, numberOfCards, controller);
		}
	}

	@Override
	protected void initialize(Controller controller)
	{
		knownCardsOnDeck.clear();
		lastDeckSize = 0;
	}

	@Override
	protected void update(Controller controller)
	{
		if (lastDeckSize > controller.getDiscardPileSize())
		{
			knownCardsOnDeck.clear();
			lastDeckSize = controller.getDiscardPileSize();
		}
		else
		{
			lastDeckSize = controller.getDiscardPileSize();
		}
	}

	@Override
	public String toString()
	{
		return "Dave";
	}
}