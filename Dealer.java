import java.util.ArrayList;

/*
 * Description: The purpose of this class is to give the cards to the players and table
 * Name: Filipe Andre de Matos Bicho
 * Last update: 27/10/2017
 */
public class Dealer {
	
	// Give cards to the players, cards are given alternately to each player
	public void giveCards(Deck deck, ArrayList<Cards> player1, ArrayList<Cards> player2)
	{
		player1.add(deck.getCard());
		player2.add(deck.getCard());
		player1.add(deck.getCard());
		player2.add(deck.getCard());
	}
	
	// Flop: Give the 3 cards to the table
	public void giveFlop(Deck deck, ArrayList<Cards> table)
	{
		//Burn one card
		deck.getCard();
		
		//Add 3 cards
		for(int i=0; i < 3; i++)
			table.add(deck.getCard());
	}
	
	// Turn && river: Give one card to the table
	public void giveOneCard(Deck deck, ArrayList<Cards> table)
	{
		//Burn one card
		deck.getCard();
		
		//Add 1 card
		table.add(deck.getCard());
	}

}
