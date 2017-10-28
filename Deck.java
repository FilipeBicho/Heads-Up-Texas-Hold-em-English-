import java.util.ArrayList;
import java.util.Random;

/*
 * Description: Create a deck of 52 cards and shuffle them 
 * Get the card from the top of the deck
 * Name: Filipe Andre de Matos Bicho
 * Last update: 27/10/2017
 */
public class Deck {

	/* ArrayList because the array changes size constantly
	*  and to get the first card of the deck through the index takes O(1) 
	*/
	private ArrayList<Cards> card;
	
	//Constructor to initialize the deck and shuffle them
	Deck()
	{
		card = new ArrayList<>();
		Random random = new Random();
		
		// Initialize Cards
		for(int rank = 0; rank < 13; rank++)
		{
			for(int suit = 0; suit < 4; suit++)
				card.add(new Cards(rank, suit));
		}
		
		// Fisher-Yates algorithm to shuffle the cards
		for(int i = card.size()-1; i > 0; i--)
		{
			// Random a number between 0 and i+1
			int index = random.nextInt(i+1);
			
			// Temp gets the card that is at index
			Cards temp = card.get(index);
			
			// Change card from index with card at i
			card.set(index, card.get(i));
			
			// Change card at i with temp card
			card.set(i, temp);
		}
	}
	
	// Get a card from the top of the deck and delete it
	public Cards getCard()
	{
		return card.remove(card.size()-1);
	}
}
