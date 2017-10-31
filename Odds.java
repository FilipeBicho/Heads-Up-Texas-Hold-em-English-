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
	
	// Odds of each poker and before flop 
	public double preFlopOdds(ArrayList<Cards> card)
	{
		/* A A */
		if(card.get(0).getRank()==0 && card.get(1).getRank()==0)
			return 85.3;
		
		/* A K suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==12 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==12 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 67;
		
		/* A K out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==12 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==12 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 65.4;
		
		/* A Q suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==11 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==11 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 66.1;
		
		/* A Q out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==11 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==11 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 66.1;
		
		/* A J suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==10 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==10 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 65.4;
		
		/* A J out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==10 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==10 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 63.6;
		
		
		/* A 10 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 64.7;
		
		/* A 10 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 62.9;
		
		/* A 9 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 63;
		
		/* A 9 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 60.9;
		
		/* A 8 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 62.1;
		
		/* A 8 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 60.1;
		
		/* A 7 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 61.1;
		
		/* A 7 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 59.1;
		
		/* A 6 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 60;
		
		/* A 6 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 57.8;
		
		/* A 5 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 59.9;
		
		/* A 5 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 57.7;
		
		/* A 4 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 58.9;
		
		/* A 4 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 56.4;
		
		/* A 3 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 58;
		
		/* A 3 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 55.6;
		
		/* A 2 suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 57;
		
		/* A 2 out suit */
		if((card.get(0).getRank()==0 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==0 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 54.6;
		
		/*--------------------------------------------------------------------------------------------*/
		
		/* K K */
		if(card.get(0).getRank()==12 && card.get(1).getRank()==12)
			return 82.4;
		
		/* K Q suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==11 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==11 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 63.4;
		
		/* K Q out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==11 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==11 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 61.4;
		
		/* K J suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==10 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==10 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 62.6;
		
		/* K J out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==10 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==10 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 60.6;
		
		/* K 10 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 61.9;
		
		/* K 10 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 59.9;
		
		/* K 9 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 60;
		
		/* K 9 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 58;
		
		/* K 8 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 58.5;
		
		/* K 8 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 56.3;
		
		/* K 7 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 57.8;
		
		/* K 7 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 55.4;
		
		/* K 6 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 56.8;
		
		/* K 6 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 54.3;
		
		/* K 5 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 55.8;
		
		/* K 5 out suit */
		if((card.get(0).getRank()==12&& card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 53.3;
		
		/* K 4 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 54.7;
		
		/* K 4 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 52.1;
		
		/* K 3 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 53.8;
		
		/* K 3 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 51.2;
		
		/* K 2 suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 52.9;
		
		/* K 2 out suit */
		if((card.get(0).getRank()==12 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==12 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 50.2;
		
		/*-----------------------------------------------------------------------------------------*/
		
		/* Q Q */
		if(card.get(0).getRank()==11 && card.get(1).getRank()==11)
			return 79.9;
		
		/* Q J suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==10 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==10 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 60.3;
		
		/* Q J out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==10 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==10 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 58.2;
		
		/* Q 10 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 59.5;
		
		/* Q 10 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 57.4;
		
		/* Q 9 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 57.9;
		
		/* Q 9 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 55.5;
		
		/* Q 8 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 56.2;
		
		/* Q 8 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 53.8;
		
		/* Q 7 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 54.5;
		
		/* Q 7 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 51.9;
		
		/* Q 6 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 53.8;
		
		/* Q 6 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 51.1;
		
		/* Q 5 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 52.9;
		
		/* Q 5 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 50.2;
		
		/* Q 4 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 51.7;
		
		/* Q 4 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 49;
		
		/* Q 3 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 50.7;
		
		/* Q 3 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 47.9;
		
		/* Q 2 suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 49.9;
		
		/* Q 2 out suit */
		if((card.get(0).getRank()==11 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==11 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 47;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* J J */
		if(card.get(0).getRank()==10 && card.get(1).getRank()==10)
			return 77.5;
		
		/* J 10 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==9 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 57.5;
		
		/* J 10 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==9 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 55.4;
		
		/* J 9 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 55.8;
		
		/* J 9 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 53.4;
		
		/* J 8 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 54.2;
		
		/* J 8 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 51.7;
		
		/* J 7 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 52.4;
		
		/* J 7 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 49.9;
		
		/* J 6 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 50.8;
		
		/* J 6 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 47.9;
		
		/* J 5 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 50;
		
		/* J 5 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 47.1;
		
		/* J 4 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 49;
		
		/* J 4 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 46.1;
		
		/* J 3 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 47.9;
		
		/* J 3 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 45;
		
		/* J 2 suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 47.1;
		
		/* J 2 out suit */
		if((card.get(0).getRank()==10 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==10 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 44;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 10 10 */
		if(card.get(0).getRank()==9 && card.get(1).getRank()==9)
			return 75.1;
		
		/* 10 9 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==8 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 54.3;
		
		/* 10 9 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==8 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 51.7;
		
		/* 10 8 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 52.6;
		
		/* 10 8 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 50;
		
		/* 10 7 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 51;
		
		/* 10 7 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 48.2;
		
		/* 10 6 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 49.2;
		
		/* 10 6 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 46.3;
		
		/* 10 5 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 47.2;
		
		/* 10 5 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 44.2;
		
		/* 10 4 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 46.4;
		
		/* 10 4 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 43.4;
		
		/* 10 3 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 45.5;
		
		/* 10 3 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 42.4;
		
		/* 10 2 suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 44.7;
		
		/* 10 2 out suit */
		if((card.get(0).getRank()==9 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==9 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 41.5;
		
		/*-----------------------------------------------------------------------------------*/
		
		/* 9 9 */
		if(card.get(0).getRank()==8 && card.get(1).getRank()==8)
			return 72.1;
		
		/* 9 8 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==7 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 51.1;
		
		/* 9 8 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==7 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 48.4;
		
		/* 9 7 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 49.5;
		
		/* 9 7 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 46.7;
		
		/* 9 6 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 47.7;
		
		/* 9 6 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 44.9;
		
		/* 9 5 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 45.9;
		
		/* 9 5 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 42.9;
		
		/* 9 4 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 43.8;
		
		/* 9 4 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 40.7;
		
		/* 9 3 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 43.2;
		
		/* 9 3 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 39.9;
		
		/* 9 2 suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 42.3;
		
		/* 9 2 out suit */
		if((card.get(0).getRank()==8 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==8 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 38.9;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 8 8 */
		if(card.get(0).getRank()==7 && card.get(1).getRank()==7)
			return 69.1;
		
		/* 8 7 suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==6 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 48.2;
		
		/* 8 7 out suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==6 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 45.5;
		
		/* 8 6 suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 46.5;
		
		/* 8 6 out suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 43.6;
		
		/* 8 5 suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 44.8;
		
		/* 8 5 out suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 41.7;
		
		/* 8 4 suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 42.7;
		
		/* 8 4 out suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 39.6;
		
		/* 8 3 suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 40.8;
		
		/* 8 3 out suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 37.5;
		
		/* 8 2 suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 40.3;
		
		/* 8 2 out suit */
		if((card.get(0).getRank()==7 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==7 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 36.8;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 7 7 */
		if(card.get(0).getRank()==6 && card.get(1).getRank()==6)
			return 66.2;
		
		/* 7 6 suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==5 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 45.7;
		
		/* 7 6 out suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==5 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 42.7;
		
		/* 7 5 suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 43.8;
		
		/* 7 5 out suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 40.8;
		
		/* 7 4 suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 41.8;
		
		/* 7 4 out suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 38.6;
		
		/* 7 3 suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 40;
		
		/* 7 3 out suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 36.6;
		
		/* 7 2 suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 38.1;
		
		/* 7 2 out suit */
		if((card.get(0).getRank()==6 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==6 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 34.6;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 6 6 */
		if(card.get(0).getRank()==5 && card.get(1).getRank()==5)
			return 63.3;
		
		/* 6 5 suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==4 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 43.2;
		
		/* 6 5 out suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==4 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 40.1;
		
		/* 6 4 suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 41.4;
		
		/* 6 4 out suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 38;
		
		/* 6 3 suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 39.4;
		
		/* 6 3 out suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 35.9;
		
		/* 6 2 suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 37.5;
		
		/* 6 2 out suit */
		if((card.get(0).getRank()==5 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==5 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 34;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 5 5 */
		if(card.get(0).getRank()==4 && card.get(1).getRank()==4)
			return 60.3;
		
		/* 5 4 suit */
		if((card.get(0).getRank()==4 && card.get(1).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==4 && card.get(0).getRank()==3 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 41.1;
		
		/* 5 4 out suit */
		if((card.get(0).getRank()==4 && card.get(1).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==4 && card.get(0).getRank()==3 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 37.9;
		
		/* 5 3 suit */
		if((card.get(0).getRank()==4 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==4 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 39.3;
		
		/* 5 3 out suit */
		if((card.get(0).getRank()==4 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==4 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 35.8;
		
		/* 5 2 suit */
		if((card.get(0).getRank()==4 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==4 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 37.5;
		
		/* 5 2 out suit */
		if((card.get(0).getRank()==4 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==4 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 33.9;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 4 4 */
		if(card.get(0).getRank()==3 && card.get(1).getRank()==3)
			return 57;
		
		/* 4 3 suit */
		if((card.get(0).getRank()==3 && card.get(1).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==3 && card.get(0).getRank()==2 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 38;
		
		/* 4 3 out suit */
		if((card.get(0).getRank()==3 && card.get(1).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==3 && card.get(0).getRank()==2 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 34.4;
		
		/* 4 2 suit */
		if((card.get(0).getRank()==3 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==3 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 36.3;
		
		/* 4 2 out suit */
		if((card.get(0).getRank()==3 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==3 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 32.5;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 3 3 */
		if(card.get(0).getRank()==2 && card.get(1).getRank()==2)
			return 53.7;
		
		/* 3 2 suit */
		if((card.get(0).getRank()==2 && card.get(1).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()) ||
				(card.get(1).getRank()==2 && card.get(0).getRank()==1 && card.get(0).getSuit() == card.get(1).getSuit()))
			return 35.1;
		
		/* 3 2 out suit */
		if((card.get(0).getRank()==2 && card.get(1).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()) ||
				(card.get(1).getRank()==2 && card.get(0).getRank()==1 && card.get(0).getSuit() != card.get(1).getSuit()))
			return 31.2;
		
		/*-------------------------------------------------------------------------------------*/
		
		/* 2 2 */
		if(card.get(0).getRank()==1 && card.get(1).getRank()==1)
			return 50.3;
		
		return 0;
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
