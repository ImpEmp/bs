package controller;

public class Card
{
	// this is the achual carrds themselfs
	private int n;

	Card(int n)
	{
		this.n = n;
	}

	public int getNumber()
	{
		return n;
	}

	public String toString()
	{
		return toString(n);
	}
	
	public static String toString(int card)
	{
		if(card == 0)
			return "A";
		else if(card == 10)
			return "J";
		else if(card == 11)
			return "Q";
		else if(card == 12)
			return "K";
		else
			return String.valueOf(card + 1);
	}
}
