package controller;

public class Card {
	// this is the achual carrds themselfs
	private int n;
	Card(int n) {
		this.n = n;
	}
	public int getNumber() {
		return n;
	}
	public String toString() {
		return String.valueOf(n);
	}
}
