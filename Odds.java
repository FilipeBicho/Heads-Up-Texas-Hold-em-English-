import java.util.ArrayList;
import java.util.HashMap;

/*
 * Description: This class calculates different types of odds
 * - Odds of both players win knowing their cards
 * - Odds of a player to win not knowing opponents cards
 * - Odds of coming a card 
 * - Odds of the opponent has a good hand
 * - Odds of winning before the flop
 * Name: Filipe Andre de Matos Bicho
 * Last update: 30/10/2017
 */
public class Odds {

	// Store the unused cards of the deck
	private ArrayList<Cards> inDeck = new ArrayList<>();
	// Store all cards that came already out of the deck
	private ArrayList<Cards> outDeck = new ArrayList<>();
	// Store all table cards combinations without repetitions
	private HashMap<Integer, ArrayList<Cards>> tableCombinations = new HashMap<>();
	// Store all opponent cards combinations without repetitions
	private HashMap<Integer, ArrayList<Cards>> opponentCombinations = new HashMap<>();
	// Stores players hands 
	private ArrayList<Cards> hand1 = new ArrayList<>();
	private ArrayList<Cards> hand2 = new ArrayList<>();
	// Stores opponent cards
	private ArrayList<Cards> opponent = new ArrayList<>();
	// Object to evaluate player hands
	private Evaluate evaluate = new Evaluate();
	// Object to calculate winning hand
	private Winner calculate = new Winner();
	//Store all cards that came already out of the deck as Strings
	private ArrayList<String> strOutDeck = new ArrayList<>();
	//Store the 45 unused cards of the deck as String
	private ArrayList<String> strInDeck = new ArrayList<>();
	// Store the results of both hands
	private int[] result = new int[2];
	// Store the odds of Victory, Draw and Losing 
	private float[] odds = new float[3];
	// Store the odds of coming a hand in the missing table cards
	private float[] potentialHand = new float[11];

	
	
	// Method to calculate the odds at flop knowing the cards of both players and table
	public float[] oddsPlayerVSPlayerFlop(ArrayList<Cards> player1, ArrayList<Cards> player2, ArrayList<Cards> table)
	{
		for(int i = 0; i < odds.length; i++)
			odds[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player1);
		outDeck.addAll(player2);
		outDeck.addAll(table);
		
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
				
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	
		
		// Get all cards combinations
		tableCombinations = getCombinations(inDeck, strInDeck);
		
		// initialize table and hand to receive multiples values
		table.add(null);
		table.add(null);
		
		// Calculate all the odds for both players
		for(int i = 0; i < tableCombinations.get(0).size(); i++)
		{
			//table gets 2 cards
			table.set(3, tableCombinations.get(0).get(i));
			table.set(4, tableCombinations.get(1).get(i));
						
			// Calculate ranking of player 1 hand
			result[0] = evaluate.evaluateHand(player1, table);
			
			// Player 1 gets is hand
			hand1.addAll(evaluate.getHand());
			evaluate.reset();
			
			
			// Calculate ranking of player 2 hand
			result[1] = evaluate.evaluateHand(player2, table);
			// Player 2 gets is hand
			hand2.addAll(evaluate.getHand());
			evaluate.reset();
			
			//Calculate winning hand
			odds[calculate.calculateWinner(hand1, hand2, result)]++;
			
			hand1.clear();
			hand2.clear();
		}
		
		// Convert odds results
		for(int i = 0; i < odds.length; i++)
		{
			odds[i] = (float) odds[i] / tableCombinations.get(0).size();
			odds[i] = roundOdds(odds[i]);
		}
		
		reset();
		return odds;
	}
	
	// Method to calculate the odds at turn knowing the cards of both players and table
	public float[] oddsPlayerVSPlayerTurn(ArrayList<Cards> player1, ArrayList<Cards> player2, ArrayList<Cards> table)
	{
		for(int i = 0; i < odds.length; i++)
			odds[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player1);
		outDeck.addAll(player2);
		outDeck.addAll(table);
		
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
				
		// Get the cards unused from the deck
		getDeckCards();
		
		// initialize table and hand to receive multiples values
		table.add(null);
		
		System.out.println(inDeck.size());
		
		// Calculate all the odds for both players
		for(int i = 0; i < inDeck.size(); i++)
		{
			//table gets 1 card
			table.set(4, inDeck.get(i));
						
			// Calculate ranking of player 1 hand
			result[0] = evaluate.evaluateHand(player1, table);
			
			// Player 1 gets is hand
			hand1.addAll(evaluate.getHand());
			evaluate.reset();
			
			
			// Calculate ranking of player 2 hand
			result[1] = evaluate.evaluateHand(player2, table);
			// Player 2 gets is hand
			hand2.addAll(evaluate.getHand());
			evaluate.reset();
			
			//Calculate winning hand
			odds[calculate.calculateWinner(hand1, hand2, result)]++;
			
			hand1.clear();
			hand2.clear();
		}
		
		// Convert odds results
		for(int i = 0; i < odds.length; i++)
		{
			odds[i] = (float) odds[i] / inDeck.size();
			odds[i] = roundOdds(odds[i]);
		}
		
		reset();
		return odds;
	}
	
	// Method to calculate the odds at flop knowing the cards of one player and table
	public float[] oddsFlop(ArrayList<Cards> player, ArrayList<Cards> table)
	{
		for(int i = 0; i < odds.length; i++)
			odds[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player);
		outDeck.addAll(table);
				
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
		
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	
		
		// Get all cards combinations
		opponentCombinations = getCombinations(inDeck, strInDeck);
		
		// Initialize opponent cards
		opponent.add(null);
		opponent.add(null);
		// Initialize more 2 outCards
		outDeck.add(null);
		outDeck.add(null);	
		// initialize 2 more table cards
		table.add(null);
		table.add(null);
		
		int n = 0;
		// Calculate odds with all combinations between the player and opponent and table cards
		for(int i = opponentCombinations.get(0).size()-1; i >= 1; i -= 20) 
		{
			// Opponent gets 2 cards
			opponent.set(0,opponentCombinations.get(0).get(i));
			opponent.set(1,opponentCombinations.get(1).get(i));
			
			// Add the card from the opponent to the cards that came out of the deck
			outDeck.set(5,opponent.get(0));
			outDeck.set(6,opponent.get(1));
			
			strOutDeck.clear();
			
			// Get all cards that came out of the deck as String
			convertToString(outDeck,strOutDeck);
			
			inDeck.clear();
			
			// Get the cards unused from the deck
			getDeckCards();
			
			// Get all cards that are still in the deck to String
			convertToString(inDeck,strInDeck);	
			
			// Get all cards combinations
			tableCombinations = getCombinations(inDeck, strInDeck);
			
			// Combinations between the 2 opponent cards all combinations to table
			for(int j = 0; j < tableCombinations.get(0).size(); j += 20)
			{
				// Table receives 2 cards
				table.set(3, tableCombinations.get(0).get(j));
				table.set(4, tableCombinations.get(1).get(j));
				
				// Calculate ranking of player hand
				result[0] = evaluate.evaluateHand(player, table);
				// Player 1 gets is hand
				hand1.addAll(evaluate.getHand());
				evaluate.reset();
				
				// Calculate ranking of player 2 hand
				result[1] = evaluate.evaluateHand(opponent, table);
				// Player 2 gets is hand
				hand2.addAll(evaluate.getHand());
				evaluate.reset();
					
				//Calculate winning hand
				odds[calculate.calculateWinner(hand1, hand2, result)]++;
				
				hand1.clear();
				hand2.clear();
				n++;
			}
		}
		
		// Convert odds results
		for(int i = 0; i < odds.length; i++)
		{
			odds[i] = (float) odds[i] / n;
			odds[i] = roundOdds(odds[i]);
		}
		
		reset();	
		return odds;
	}
	
	// Method to calculate the odds at turn knowing the cards of one player and table
	public float[] oddsTurn(ArrayList<Cards> player, ArrayList<Cards> table)
	{
		for(int i = 0; i < odds.length; i++)
			odds[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player);
		outDeck.addAll(table);
				
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
		
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	
		
		// Get all cards combinations
		opponentCombinations = getCombinations(inDeck, strInDeck);
		
		System.out.println(opponentCombinations.get(0).size());
		
		// Initialize opponent cards
		opponent.add(null);
		opponent.add(null);
		// Initialize more 2 outCards
		outDeck.add(null);
		// initialize 1 more table card
		table.add(null);
		
		
		int n = 0;
		// Calculate odds with all combinations between the player and opponent and table cards
		for(int i = opponentCombinations.get(0).size()-1; i >= 1; i -= 10) 
		{
			// Opponent gets 2 cards
			opponent.set(0,opponentCombinations.get(0).get(i));
			opponent.set(1,opponentCombinations.get(1).get(i));
			
			// Add the card from the opponent to the cards that came out of the deck
			outDeck.set(5,opponent.get(0));
			outDeck.set(6,opponent.get(1));
			
			strOutDeck.clear();
			
			// Get all cards that came out of the deck as String
			convertToString(outDeck,strOutDeck);
			
			inDeck.clear();
			
			// Get the cards unused from the deck
			getDeckCards();
			
			// Get all cards that are still in the deck to String
			convertToString(inDeck,strInDeck);	
			
			// Get all cards combinations
			tableCombinations = getCombinations(inDeck, strInDeck);
			
			// Combinations between the 2 opponent cards all combinations to table
			for(int j = 0; j < tableCombinations.get(0).size(); j += 10)
			{
				// Table receives 1 card
				table.set(4, tableCombinations.get(1).get(j));
				
				// Calculate ranking of player hand
				result[0] = evaluate.evaluateHand(player, table);
				// Player 1 gets is hand
				hand1.addAll(evaluate.getHand());
				evaluate.reset();
				
				// Calculate ranking of player 2 hand
				result[1] = evaluate.evaluateHand(opponent, table);
				// Player 2 gets is hand
				hand2.addAll(evaluate.getHand());
				evaluate.reset();
					
				//Calculate winning hand
				odds[calculate.calculateWinner(hand1, hand2, result)]++;
				
				hand1.clear();
				hand2.clear();
				n++;
			}
		}
		
		// Convert odds results
		for(int i = 0; i < odds.length; i++)
		{
			odds[i] = (float) odds[i] / n;
			odds[i] = roundOdds(odds[i]);
		}
		
		reset();
		return odds;
	}

	// Method to calculate the odds at turn knowing the cards of one player and table
	public float[] oddsRiver(ArrayList<Cards> player, ArrayList<Cards> table)
	{
		for(int i = 0; i < odds.length; i++)
			odds[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player);
		outDeck.addAll(table);
				
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
		
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	
		
		// Get all cards combinations
		opponentCombinations = getCombinations(inDeck, strInDeck);
				
		// Initialize opponent cards
		opponent.add(null);
		opponent.add(null);
		// Initialize more 2 outCards
		outDeck.add(null);
		
		int n = 0;
		// Calculate odds with all combinations between the player and opponent and table cards
		for(int i = 0; i < opponentCombinations.get(0).size(); i++) 
		{
			// Opponent gets 2 cards
			opponent.set(0,opponentCombinations.get(0).get(i));
			opponent.set(1,opponentCombinations.get(1).get(i));
			
			// Calculate ranking of player hand
			result[0] = evaluate.evaluateHand(player, table);
			// Player 1 gets is hand
			hand1.addAll(evaluate.getHand());
			evaluate.reset();
			
			// Calculate ranking of player 2 hand
			result[1] = evaluate.evaluateHand(opponent, table);
			// Player 2 gets is hand
			hand2.addAll(evaluate.getHand());
			evaluate.reset();
				
			//Calculate winning hand
			odds[calculate.calculateWinner(hand1, hand2, result)]++;
			
			hand1.clear();
			hand2.clear();
			n++;
			
		}
		
		// Convert odds results
		for(int i = 0; i < odds.length; i++)
		{
			odds[i] = (float) odds[i] / n;
			odds[i] = roundOdds(odds[i]);
		}
		
		reset();
		return odds;
	}

	// Method the potential to come a hand in turn or river
	public float[] potentialFlop(ArrayList<Cards> player, ArrayList<Cards> table)
	{
		for(int i = 0; i < potentialHand.length; i++)
			potentialHand[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player);
		outDeck.addAll(table);
				
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
		
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	
		
		// Get all cards combinations
		tableCombinations = getCombinations(inDeck, strInDeck);
		
		// initialize 2 more table cards
		table.add(null);
		table.add(null);
		
		int n = 0;
		// Calculate odds of coming a hand in the missing cards
		for(int i = 0; i < tableCombinations.get(0).size(); i++) 
		{
			// Table gets 2 cards
			table.set(3, tableCombinations.get(0).get(i));
			table.set(4, tableCombinations.get(1).get(i));
			
			// Odds of coming hand
			potentialHand[evaluate.evaluateHand(player, table)]++;
			evaluate.reset();

			n++;
		}
		
		// Convert odds results
		for(int i = 0; i < potentialHand.length; i++)
		{
			potentialHand[i] = (float) potentialHand[i] / n;
			potentialHand[i] = roundOdds(potentialHand[i]);
		}
		
		reset();
		return potentialHand;
	}
	
	// Method the potential to come a hand in river
	public float[] potentialTurn(ArrayList<Cards> player, ArrayList<Cards> table)
	{
		for(int i = 0; i < potentialHand.length; i++)
			potentialHand[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(player);
		outDeck.addAll(table);
				
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
		
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	

		// initialize 2 more table cards
		table.add(null);
		
		int n = 0;
		// Calculate odds of coming a hand in the missing cards
		for(int i = 0; i < inDeck.size(); i++) 
		{
			// Table gets 2 cards
			table.set(4, inDeck.get(i));
			
			// Odds of coming hand
			potentialHand[evaluate.evaluateHand(player, table)]++;
			evaluate.reset();

			n++;
		}
		
		// Convert odds results
		for(int i = 0; i < potentialHand.length; i++)
		{
			potentialHand[i] = (float) potentialHand[i] / n;
			potentialHand[i] = roundOdds(potentialHand[i]);
		}
		
		reset();
		return potentialHand;
	}

	// Method to calculate the odds of the opponent has some hand
	public float[] opponentHand(ArrayList<Cards> table)
	{
		for(int i = 0; i < potentialHand.length; i++)
			potentialHand[i] = 0;
		
		// Join all cards that already came out of the deck
		outDeck.addAll(table);
				
		// Get all cards that came out of the deck as String
		convertToString(outDeck,strOutDeck);
		
		// Get the cards unused from the deck
		getDeckCards();
		
		// Get all cards that are still in the deck to String
		convertToString(inDeck,strInDeck);	
		
		// Get all cards combinations
		opponentCombinations = getCombinations(inDeck, strInDeck);
		
		// Initialize 2 positions for opponent hand
		opponent.add(null);
		opponent.add(null);
		
		int n = 0;
		// Calculate odds of which hand the opponent could have
		for(int i = 0; i < opponentCombinations.get(0).size(); i++) 
		{
			opponent.set(0,opponentCombinations.get(0).get(i));
			opponent.set(1,opponentCombinations.get(1).get(i));
			
			// Odds opponent hand
			potentialHand[evaluate.evaluateHand(opponent, table)]++;
			evaluate.reset();
			n++;
		}
		
		// Convert odds results
		for(int i = 0; i < potentialHand.length; i++)
		{
			potentialHand[i] = (float) potentialHand[i] / n;
			potentialHand[i] = roundOdds(potentialHand[i]);
		}
		
		reset();
		
		return potentialHand;
	}

	// Reset everything
	private void reset() 
	{
		inDeck.clear();
		outDeck.clear();
		tableCombinations.clear();
		opponentCombinations.clear();
		strOutDeck.clear();
		strInDeck.clear();
		opponent.clear();
		hand1.clear();
		hand2.clear();	
	}

	// Method to get all cards from the deck except the cards that already came out
	private void getDeckCards()
	{
		
		Cards card;
		
		for(int i = 0; i < 13; i++)
		{
			for(int j = 0; j < 4; j++)
			{			
				card = new Cards(i,j);
				String strCard = Integer.toString(card.getRank()) + Integer.valueOf(card.getSuit());
								
				if(!strOutDeck.contains(strCard))
					inDeck.add(card);		
			}
		}
	}
	
	// Method to convert a card to String
	private void convertToString(ArrayList<Cards> cards, ArrayList<String> cardsStr)
	{
		for(Cards card : cards)
			cardsStr.add(Integer.toString(card.getRank()) + Integer.toString(card.getSuit()));
		
	}
	
	// Method to delete repetitions in cards
	private HashMap<Integer, ArrayList<Cards>> getCombinations(ArrayList<Cards> inDeck, ArrayList<String> strInDeck)
	{
		// Stores 2 temporary cards
		Cards[] temp = new Cards[2];
		// Put cards together to look for repetitions
		ArrayList<String> duplication = new ArrayList<>();
		// Store first cards
		ArrayList<Cards> cards1 = new ArrayList<>();
		// Stores second cards
		ArrayList<Cards> cards2 = new ArrayList<>();
		// Store cards combinations
		HashMap<Integer, ArrayList<Cards>> combinations = new HashMap<>();
		
		// Search all cards
		for(int i = 0; i < inDeck.size(); i++)
		{
			// Get first card
			temp[0] = inDeck.get(i);
			String opponent0 = strInDeck.get(i);
			
			// Check all cards
			for(int j = 0; j < inDeck.size(); j++)
			{	
				//Second opponent card has to be different from the first
				if(!strInDeck.get(j).equals(opponent0))
				{
					temp[1] = inDeck.get(j);
					String opponent1 = strInDeck.get(j);
					
					duplication.add(opponent1 + " " + opponent0);
					String tempOpponent = opponent0 + " " + opponent1;
							
					if(!duplication.contains(tempOpponent))
					{
						cards1.add(temp[0]);
						cards2.add(temp[1]);
					}		
				}
			}		
		}
				
		combinations.put(0, cards1);
		combinations.put(1, cards2);
		
		return combinations;
				
	}
	
	// Method to round odds to 2 numbers
	private float roundOdds(float num)
	{
		num = (float) num * 100;
		num = Math.round(num * 100);
		return num /= 100;
	}
	
}
