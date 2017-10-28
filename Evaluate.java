import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * Description: This class aims to look in which Poker ranking a hand belongs to.
 * Name: Filipe Andre de Matos Bicho
 * Last update: 27/10/2017
 */
public class Evaluate {

	// ArrayList to stores the final hand (Player cards + Table cards - 5 cards)
	private ArrayList<Cards> hand = new ArrayList<>();
	
	// ArrayList to stores all cards (Player cards + Table cards - 5 to 7 cards)
	private ArrayList<Cards> cards = new ArrayList<>();
	
	// ArrayList to store cards rank
	ArrayList<Integer> rank = new ArrayList<>();
	
	// ArrayList to store cards suit
	ArrayList<Integer> suit = new ArrayList<>();
	
	// Declare HashMap to store rank repetitions
	HashMap<Integer, Integer> rankRepetitions = new HashMap<Integer, Integer>();
	
	// Declare HashMap to store suit repetitions
	HashMap<Integer, Integer> suitRepetitions = new HashMap<Integer, Integer>();
	
	final String RANK = "rank";
	final String SUIT = "suit";
	
	// Save Result
	String result = "";
	
	// Main method to analyze in which rank a hand belongs to
	public int evaluateHand (ArrayList<Cards> player, ArrayList<Cards> table)
	{
		// Join player and table cards
		joinCards(player, table);
		
		// Sort card by rank
		Collections.sort(cards, Cards.sortRank);
		
		// Initialize cards rank
		for(Cards c : cards)
			rank.add(c.getRank());
		
		// Initialize cards suit
		for(Cards c : cards)
			suit.add(c.getSuit());
		
		// Get rank and suit repetition
		getRepetitions();
		
		if(isRoyalStraightFlush())
			return 10;
		
		if(isStraightFlush())
			return 9;
		
		if(isFourOfAKind())
			return 8;
		
		if(isFullHouse())
			return 7;
		
		if(isFlush())
			return 6;
		
		if(isStraight())
			return 5;
		
		if(isThreeOfAKind())
			return 4;
			
		if(isTwoPair())
			return 3;
		
		if(isPair())
			return 2;
		
		if(isHighCard())
			return 1;

		
		return 0;
	}
	
	// Put player cards and table cards together
	public void joinCards(ArrayList<Cards> player, ArrayList<Cards> table)
	{
		cards.addAll(0, player);
		cards.addAll(cards.size(), table);	
	}
	
	// Royal Straight Flush
	public boolean isRoyalStraightFlush()
	{
		// Check if has a Straight Flush
		if(isStraightFlush())
		{
			if(Integer.valueOf(hand.get(0).getRank()).equals(0))
			{
				result = "Royal Straight Flush";
				return true;
			}
			else
			{
				hand.clear();
				return false;
			}
		}
		else
			return false;
	}
	
	// Straight Flush
	public boolean isStraightFlush() 
	{
		// Check if has a straight
		if(isStraight())
		{
			// ArrayList to store straight suit
			ArrayList<Integer> suitStraight = new ArrayList<>();	
			// Declare HashMap to store suit repetitions
			HashMap<Integer, Integer> straightRepetitions = new HashMap<Integer, Integer>();
			
			// Initialize other cards rank
			for(Cards c : hand)
				suitStraight.add(c.getSuit());
					
			// Get suit repetitions
			for(Integer count : suitStraight)
				straightRepetitions.put(count, Collections.frequency(suitStraight, count));
			
			// Check for a straight
			if(straightRepetitions.containsValue(5))
			{
				result = "Straight Flush";
				return true;
			}
			else
			{
				hand.clear();
				return false;
			}
		}
		else
			return false;
		
	}
	
	// Four of a kind
	public Boolean isFourOfAKind()
	{
		if(rankRepetitions.containsValue(4))
		{
			//Hand gets the fourOfAKind	
			setHandCards(getKey(4,rankRepetitions).get(0), RANK, cards);		
			// Get kicker card
			kicker();
			result = "Four Of A Kind";
		}
		
		return rankRepetitions.containsValue(4);
	}
	
	// Full House
	public Boolean isFullHouse()
	{
		// if has a three of a kind
		if(isThreeOfAKind())
		{		
			// three store the three of a kind
			ArrayList<Cards> three = new ArrayList<>();
			three.add(hand.get(0));
			three.add(hand.get(1));
			three.add(hand.get(2));
			
			hand.clear();
			
			// Store all the keys that contains a pair
			ArrayList<Integer> keys = new ArrayList<>();
			// three store the three of a kind
			ArrayList<Cards> pair = new ArrayList<>();
			
			// If has another three of a kind
			if(getKey(3,rankRepetitions).size() > 1)
			{
				// Get the second three of a kind
				keys = getKey(3,rankRepetitions);
				
				// Hand gets all the pairs
				for(Integer key : keys)
					setHandCards(key, RANK, cards);
				
				if(Integer.valueOf(three.get(0).getRank()).equals(0))
				{
					pair.add(hand.get(3));
					pair.add(hand.get(4));
				}
				else
				{
					pair.add(hand.get(0));
					pair.add(hand.get(1));
				}
				
				hand.clear();
				
				hand.addAll(three);
				hand.addAll(pair);
				
				result = "Full House";
				
				return true;
			}
			else
			{
				// Get all the pairs
				keys = getKey(2,rankRepetitions);
				
				// Hand gets all the pairs
				for(Integer key : keys)
					setHandCards(key, RANK, cards);

				
				// If has 2 pairs
				if(Integer.valueOf(hand.size()).equals(4))
				{
					// Check if has a pair of Aces
					if(Integer.valueOf(hand.get(0).getRank()).equals(0))
					{
						// Get the Ace pair
						pair.add(hand.get(0));
						pair.add(hand.get(0));
					}
					else
					{
						// Get the Highest pair
						pair.add(hand.get(2));
						pair.add(hand.get(3));
					}
					
					hand.clear();
					
					// Add the fullhouse
					hand.addAll(three);
					hand.addAll(pair);
					
					result = "Full House";
					return true;
				}
				// If has 1 pair
				else if(Integer.valueOf(hand.size()).equals(4))
				{
					// Get the pair
					pair.add(hand.get(0));
					pair.add(hand.get(1));
					
					hand.clear();
					
					// Add the fullhouse
					hand.addAll(three);
					hand.addAll(pair);
					
					result = "Full House";
					return true;
				}
				else
				{
					hand.clear();
					return false;
							
				}			
			}		
		}
		else 
			return false;
	}
	
	// Flush
	public Boolean isFlush()
	{
		
		// Check if it is flush with 7 cards
		if(suitRepetitions.containsValue(7))
		{
			//Hand gets the flush
			setHandCards(getKey(7,suitRepetitions).get(0), SUIT, cards);
			
			//Remove the 2 lowest cards
			//Check if has an Ace
			if(Integer.valueOf(hand.get(0).getRank()).equals(0))
			{
				hand.remove(1);
				hand.remove(1);
			}
			else
			{
				hand.remove(0);
				hand.remove(0);
			}
			
			result = "Flush";

			return true;
		}	
		// Check if it is flush with 6 cards
		else if(suitRepetitions.containsValue(6))
		{
			//Hand gets the flush
			setHandCards(getKey(6,suitRepetitions).get(0), SUIT, cards);
			
			//Remove the lower card
			//Check if has an Ace
			if(Integer.valueOf(hand.get(0).getRank()).equals(0))
				hand.remove(1);
			else
				hand.remove(0);
			
			result = "Flush";
			
			return true;
		}	
		// Check if it is flush with 5 cards
		else if(suitRepetitions.containsValue(5))
		{
			//Hand gets the flush
			setHandCards(getKey(5,suitRepetitions).get(0), SUIT, cards);
			
			result = "Flush";
			return true;
		}	
		else
			return false;
	}
	
	// Straight
	public Boolean isStraight()
	{
		int straight = 0;
		int max = 0;
		int index = 0;
		// Search for sequential cards
		for(int i = 0; i < cards.size()-1; i++)
		{			
			if(Integer.valueOf(cards.get(i).getRank()).equals(cards.get(i+1).getRank()-1))
			{
				straight++;
				if(max < straight)
				{
					max = straight;
					index = i+1;
				}
					
			}	
			else
				straight = 0;		
		}
		
		// If last card is a K and has a straight till 10 and the first card is an Ace then exists straight 10 - Ace
		if(max >= 3 &&
				Integer.valueOf(cards.get(index).getRank()).equals(12)
				&& Integer.valueOf(cards.get(0).getRank()).equals(0))
		{
			hand.add(cards.get(0));
			hand.add(cards.get(index));
			hand.add(cards.get(index-1));
			hand.add(cards.get(index-2));
			hand.add(cards.get(index-3));
			result = "Straight";
			return true;
		}
		// If has a normal straight without an Ace
		else if(max >= 4)
		{
			hand.add(cards.get(index));
			hand.add(cards.get(index-1));
			hand.add(cards.get(index-2));
			hand.add(cards.get(index-3));
			hand.add(cards.get(index-4));
			result = "Straight";
			return true;
		}
		else
			return false;
	}
	
	// Three of a kind
	public Boolean isThreeOfAKind()
	{
		// If has a Three Of A Kind
		if(rankRepetitions.containsValue(3))
		{
			// Store all the keys that contains a three of a kind
			ArrayList<Integer> keys = new ArrayList<>();
			
			// Get all the three of a kinds
			keys = getKey(3,rankRepetitions);
			
			// If has more than 1 three of a kind
			if(keys.size() > 1)
			{
				// Hand gets all the pair
				for(Integer key : keys)
					setHandCards(key, RANK, cards);
				
				// Check if has a Three of Aces
				if(Integer.valueOf(hand.get(0).getRank()).equals(0))
				{
					// Remove the lowest three of a kind
					hand.remove(3);
					hand.remove(3);
					hand.remove(3);
					kicker();
					kicker();
					result = "Three Of A Kind";
					return true;
				}
				else
				{
					// Remove the lowest three of a kind
					hand.remove(0);
					hand.remove(0);
					hand.remove(0);
					kicker();
					kicker();
					result = "Three Of A Kind";
					return true;
				}			
			}
			else
			{
				//Hand gets the three of kind	
				setHandCards(getKey(3,rankRepetitions).get(0), RANK, cards);
				// Gets 2 Kickers
				kicker();
				kicker();
				result = "Three Of A Kind";
				return true;
			}
		}
		else
			return false;
		
	}

	// Two Pairs
	public Boolean isTwoPair()
	{
		// Store all the keys that contains a pair
		ArrayList<Integer> keys = new ArrayList<>();
		
		// Get all the pairs
		keys = getKey(2,rankRepetitions);

		// Check if has more than a pair
		if(keys.size() > 1)
		{
			// Hand gets all the pair
			for(Integer key : keys)
				setHandCards(key, RANK, cards);
			
			// If it has 2 pairs just add a kicker
			if(Integer.valueOf(keys.size()).equals(2))
				kicker();
			// If it has 3 pairs then have to delete 2 and check if one pair is an Ace
			else 
			{
				
				// Check if has a pair of Aces
				if(Integer.valueOf(hand.get(0).getRank()).equals(0))
				{
					// Remove the lowest pair
					hand.remove(2);
					hand.remove(2);			
					kicker();
				}
				else
				{
					// Remove the lowest pair
					hand.remove(0);
					hand.remove(0);	
					kicker();
				}
			}
			
			result = "Two Pair";
			
			return true;
		}
		else
			return false;
	}
	
	// One Pair
	public Boolean isPair()
	{
		if(rankRepetitions.containsValue(2))
		{
			//Hand gets the three of kind	
			setHandCards(getKey(2,rankRepetitions).get(0),RANK,cards);
			
			// Gets 3 Kickers
			kicker();
			kicker();
			kicker();
			result = "Pair";
			return true;
		}
		else
			return false;	
	}

	// High Card
	public Boolean isHighCard() 
	{
		// Check if cards has an Ace
		if(Integer.valueOf(cards.get(0).getRank()).equals(0))
		{
			hand.add(cards.get(0));
			hand.add(cards.get(cards.size()-1));
			hand.add(cards.get(cards.size()-2));
			hand.add(cards.get(cards.size()-3));
			hand.add(cards.get(cards.size()-4));
		}
		else
		{
			hand.add(cards.get(cards.size()-1));
			hand.add(cards.get(cards.size()-2));
			hand.add(cards.get(cards.size()-3));
			hand.add(cards.get(cards.size()-4));
			hand.add(cards.get(cards.size()-5));
		}
		
		result = "High Card";
		
		return true;
	}
	
	/*
	 *  Method to discovery repetition on the cards
	 *  Method important to check pair, 2 pair, 3 of kind, 4 of a kind and flush
	 */
	public void getRepetitions()
	{
		// Get rank repetitions
		for(Integer count : rank)
			rankRepetitions.put(count, Collections.frequency(rank, count));
		
		// Get suit repetitions
		for(Integer count : suit)
			suitRepetitions.put(count, Collections.frequency(suit, count));		
	}
	
	// Get the rank of the card that has repetitions
	@SuppressWarnings("rawtypes")
	public ArrayList<Integer> getKey(int value, HashMap<Integer,Integer> repetitions)
	{
		ArrayList<Integer> key= new ArrayList<>();
		for(Map.Entry elem: repetitions.entrySet())
		{
			if(Integer.valueOf(value).equals(elem.getValue()))
				key.add((Integer) elem.getKey());
		}	
		
		return key;
	}

	// Method to get cards that match the rank
	
	// Set the hand cards
	public void setHandCards(int key, String type, ArrayList<Cards> cards)
	{
		
		if(type.equals("suit"))
		{
			for(Cards card : cards)
			{
				if(Integer.valueOf(key).equals(card.getSuit()))
					hand.add(card);
			}
		}
		else if(type.equals("rank"))
		{
			for(Cards card : cards)
			{
				if(Integer.valueOf(key).equals(card.getRank()))
					hand.add(card);
			}
		}
		else
			throw new java.lang.Error("Error defining hand cards - setHandCards()");
		
	}

	// Method to get a kicker card
	public void kicker()
	{
		ArrayList<Cards> others = new ArrayList<>();
		
		for(Cards card : cards)
		{
			if(!hand.contains(card))
				others.add(card);
		}
				
		// Check if has an Ace
		// If not gets the card with highest value
		if(Integer.valueOf(others.get(0).getRank()).equals(0))
			hand.add(others.get(0));
		else
			hand.add(others.get(others.size()-1));
	}

	// Return the String with the result
	public String getResult()
	{
		return result;
	}
	
	// Return the final hand
	public ArrayList<Cards> getHand()
	{
		return hand;
	}

	// Reset cards
	public void reset()
	{
		hand.clear();
		cards.clear();
		rank.clear();
		suit.clear();
		rankRepetitions.clear();
		suitRepetitions.clear();
	}
}
