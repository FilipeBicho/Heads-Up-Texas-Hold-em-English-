import java.util.ArrayList;
import java.util.HashMap;

/*
 * Description: Development of several algorithms that simulates a poker player
 * Name: Filipe Andre de Matos Bicho
 * Last update: 27/10/2017
 */

public class Simulator {

	private Evaluate evaluate = new Evaluate();
	private Odds oddsObject = new Odds();
	
	/* Mathematically Fair Strategy - MFS 
	 * W - odds of winning
	 * L - odds of losing
	 * A - Sum of the bet made by the opponent
	 * J - Sum of the bet made by the player
	 */
	public int[] MFS(int player, float[] pokerChips, float temppot, float[] playerodds, float[] bet, int menu)
	{
		int opponent = (player == 0) ? 1 : 0;
		
		float call = (bet[opponent] - bet[player]);	
		float pot = pokerChips[2]+temppot;					
		float W = playerodds[player];			
		float L = 100 - W;								
		float V;										
		int T = 20;										
		
		int[] betOptions = new int[2];
		
		// If there is no bets
		if(call == 0 && menu != 2)
		{
			// Check
			if(W <= 55)
				betOptions[0] = 1;
			// Bet big blind
			if(W > 55 && W <= 65)
			{
				// If player has less then 20 pokerChips
				if(pokerChips[player]<= 20)
					betOptions[0] = 4;
				else
					betOptions[0] = 2;
			}
			// Bet pot
			if(W > 65 && W <= 75)
			{
				betOptions[0] = 3;
				
				if(pokerChips[player] <= 40)
					betOptions[0]=4;
				if(pot < pokerChips[player])
					betOptions[1] =  Math.round(pot);
				else
					betOptions[0] =  4 ;	
			}
			// Bet pot + 10*Big blind
			if(W > 75 && W <= 85)
			{
				betOptions[0] = 3;
			
				if(pokerChips[player] <= 40)
					betOptions[0]=2;
				if((pot+400) < pokerChips[player])
					betOptions[1] = Math.round(pot+400);
				else
					betOptions[0] = 4;	
			}
			// All in
			if(W > 85 )
				betOptions[0] = 4;
		} 
		// Menu Fold_Call / Fold_Allin
		else
		{
			V = (W*bet[opponent]-L*call)/100;
			
			// Fold_Allin
			if(menu == 2)
			{
				// Fold 
				if(V <= 5*T )
					betOptions[0] = 1;
				// Call
				else 
					betOptions[0] = 2;
			}
			// menuCheck 
			else
			{
				// Fold 
				if(V <= 0 )
					betOptions[0] = 1;
				// Call 
				if(V > 0 && V <= T)
					betOptions[0] = 2;
				// Bet Big blind
				if(V > T && V <= 2*T)
					if(pokerChips[player]<= 20)
						betOptions[0]=5;
					else
						betOptions[0] = 3;
				// Bet pot
				if(V > 2*T && V <= 5*T)
				{
					betOptions[0] = 4;
					
					if(pokerChips[player] <= 40)
						betOptions[0]=5;
					if(pot < pokerChips[player])
						betOptions[1] = Math.round(pot) ;	
					else
						betOptions[0] =  5;	
				}
				// Bet pot + 10*Big Blind
				if(V > 5*T && V <= 10*T)
				{
					betOptions[0] = 4;
					
					if(pokerChips[player] <= 40)
						betOptions[0]=5;
					if((pot+400) < pokerChips[player])
						betOptions[1] = Math.round(pot+400) ;	
					else
						betOptions[0] = 5;	
				}
				// All in
				if(V > 10*T)
					betOptions[0] = 5;
			}
		}

		return betOptions;
	}
	
	// Main method for the passive mode
	public int[] computer_passive(int player, int dealer, float[] pokerChips, float[] bet, float tempPote, int round, 
			int menu,HashMap<Integer, ArrayList<Cards>> cards , float[] odds) 
	{
		int[] betOption = new int[2];
			
		// Before the flop
		if(round == 0)
			betOption = preFlop_passive(player, dealer, pokerChips, tempPote, bet, cards, menu);
		else
		{	
			// Flop e turn 
			if(round == 1 || round == 2)
				betOption = flop_passive(player, dealer, pokerChips, tempPote, bet, odds, cards, menu, round);
			if(round == 3)
				betOption = river_passive(player, dealer, pokerChips, tempPote, bet, odds, menu);
		}
			

		return betOption;
	}
	
	// Main method for the aggressive mode
	public int[] computer_aggressive(int player, int dealer, float[] pokerChips, float[] bet, float tempPote, int round, 
			int menu,HashMap<Integer, ArrayList<Cards>> cards , float[] odds) 
	{
		int[] betOption = new int[2];
			
		// Before the flop
		if(round == 0)
			betOption = preFlop_aggressive(player, dealer, pokerChips, tempPote, bet, cards, menu);
		else
		{	
			// Flop e turn 
			if(round == 1 || round == 2)
				betOption = flop_aggressive(player, dealer, pokerChips, tempPote, bet, odds, cards, menu, round);
			if(round == 3)
				betOption = river_aggressive(player, dealer, pokerChips, tempPote, bet, odds, menu);
		}
			

		return betOption;
	}
	
	// Method to simulate a computer that just plays with good odds - Before Flop
	private int[] preFlop_passive(int player, int dealer, float[] pokerChips, float tempPot, float[] bet, HashMap<Integer, ArrayList<Cards>> cards, int menu)
	{
		
		int opponent = (player == 0) ? 1 : 0;

		int[] betOptions = new int[2];
		// Big Blind value
		int bb = 20;
		// Stores converted poker chips to big blinds
		int playerStack;
		int opponentStack; 
		
		if(pokerChips[player] > bb)
			playerStack = (int)pokerChips[player] / bb;
		else
			playerStack = 0;
		if(pokerChips[opponent] > bb)
			opponentStack = (int)pokerChips[opponent] / bb;
		else
			opponentStack = 0;
		
		// Get the group that the cards belong to
		int cardGroup = group(cards.get(player));
		

		int call;
		if(playerStack > 0 && opponentStack > 0)
			call = (int)(bet[opponent] - bet[player]);
		else
			call = 0;
	
		// Opponent didn't raised
		if(call <= bb) 
		{
			// All in
			if(playerStack < 8 || (opponentStack+call) < 8)
				betOptions[0] = menu;
			// Player or opponent have less then 12 big blinds
			if((playerStack >= 8 && playerStack < 12) || (opponentStack >= 8 && opponentStack < 12))
			{
				if(player == dealer)
				{
					if(cardGroup <= 8)
						betOptions[0] = menu;
					// Fold
					else
						betOptions[0] = 1;
				}
				else
				{
					// All in
					if(cardGroup <= 7)
						betOptions[0] = menu;
					// Fold
					else
						betOptions[0] = 1;
				}			
			}
			// Player or opponent have more then 12 big blind
			if(playerStack >= 12 || opponentStack >=12)
			{
				// player is dealer
				if(player == dealer)
				{	
	
					if(cardGroup <= 8)
					{
						if(cardGroup <= 5)
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 6*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 6*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 3*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 3*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
						
					}	
					// Fold or Check
					else
						betOptions[0] = 1;
				}
				// Player is blind
				else
				{
					if(cardGroup <= 7)
					{
						if(cardGroup <= 4)
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 8*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 8*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 4*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 4*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
					}	
					// Check
					else
						betOptions[0] = 1;
				}	
			}
		}
		
		// If opponent made a bet
		if(call > bb)
		{
			//// Player or opponent have less then 2 big blind makas All in
			if(playerStack < 2 || (opponentStack+call) < 2)
			{
				// menu fold_call 
				if(menu == 5)
					betOptions[0] = 2;
				// menu fold_allin 
				if(menu == 2)
					betOptions[0] = menu;
			}
			// Player or opponent have between 2 and 4 big blind
			if( (playerStack >= 2 && playerStack < 4) || ((opponentStack+call) <= 2 && (opponentStack+call) < 4))
			{
				// All in
				if(cardGroup <= 7)
					betOptions[0]=menu;
				//Fold
				else
					betOptions[0] = 1;
			}
			// Player or opponent have between 4 and 12 big blind
			if( (playerStack >= 4 && playerStack < 12) || ((opponentStack+call) <= 4 && (opponentStack+call) < 12))
			{
				// All in
				if(cardGroup <= 6)
					betOptions[0]=menu;
				//Fold
				else
					betOptions[0] = 1;
			}
			// Player or opponent have more then 12 big blind
			if(playerStack >= 12 || (opponentStack+call) >=12)
			{
				if(call <= 3*bb)
				{
					// Raise
					if(cardGroup <= 7)
					{
						if(cardGroup <= 5)
						{
							if(pokerChips[player] > 4*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (4*call);
							}
							else
								betOptions[0] = menu;
							
							if(menu == 2)
								betOptions[0] = menu;
						}
						// Call
						else
							betOptions[0] = 2;
					}
					// Fold
					else
						betOptions[0] = 1;	
				}
				// If opponent made a bet bigger then 3 big blinds
				if(call > 3*bb && call <= 6*bb)
				{
					if(cardGroup <= 6)
					{
						// Raise
						if(cardGroup <= 4)
						{
							if(cardGroup <= 2)
							{
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								else
									betOptions[0] = menu;
								if(menu == 2)
									betOptions[0] = menu;
							}
							else
							{
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								else
									betOptions[0] = menu;
								if(menu == 2)
									betOptions[0] = menu;
							}
							
						}
						// Call
						else
							betOptions[0] = 2;
					}
					// Fold
					else
						betOptions[0] = 1;
				}
				// If opponent made a bet between 6 and 12 big blinds
				if(call > 6*bb && call <= 12*bb)
				{
					if(cardGroup <= 5)
					{
						if(cardGroup <= 3)
						{
							if(cardGroup <= 2)
							{
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								else
									betOptions[0] = menu;
								if(menu == 2)
									betOptions[0] = menu;
							}
							else
							{
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								else
									betOptions[0] = menu;				
								if(menu == 2)
									betOptions[0] = menu;
							}					
						}
						// Call
						else
							betOptions[0] = 2;
					}
					// Fold
					else
						betOptions[0] = 1;
				}
				// If the opponent made a bet bigger then 12 big blinds
				if(call > 12*bb)
				{
					if(cardGroup <= 4)
					{
						// Raise
						if(cardGroup <= 3)
						{
							if(cardGroup <= 2)
							{
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								else
									betOptions[0] = menu;				
								if(menu == 2)
									betOptions[0] = menu;
							}
							else
							{
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								else
									betOptions[0] = menu;				
								if(menu == 2)
									betOptions[0] = menu;
							}
							
						}
						// Call
						else
							betOptions[0] = 2;
					}
					// Fold
					else
						betOptions[0] = 1;
				}		
			}		
		}	
		return betOptions;
	}

	// Method to simulate a computer that just plays with good odds - In Flop
	private int[] flop_passive(int player, int dealer, float[] pokerChips, float tempPot, float[] bet, float[] odds, 
				HashMap<Integer,ArrayList<Cards>> cards, int menu, int round)
	{
		int opponent = (player == 0) ? 1 : 0;
		// Store bet option 
		int[] betOptions = new int[2];
		int bb = 20;
		// Convert poker chips to big blinds
		int playerStack = (int)pokerChips[player] / bb;
		int opponentStack = (int)pokerChips[opponent] / bb;
		// Pot value
		float pot = pokerChips[2] + tempPot;
		// Opponent raise value
		float call = (bet[opponent] - bet[player]);
		// Store pot odds value
		float potOdds;
		// Store player hand result
		int result;
		// Stores the odds of the opponent has a certain hand
		float[] opponentHandOdds = new float[11];
		// Stores the odds to come a certain hand
		float[] potentialHandOdds = new float[11];
		// Gets the sum of the odds has a worst hand
		float worstGameOdds = 0;
		// Gets the sum of the odds to player improve his hand
		float improveHandOdds = 0;		
		// Stores temporally table cards
		ArrayList<Cards> table = new ArrayList<>();
		// Stores temporally player cards
		ArrayList<Cards> playerCards = new ArrayList<>();
		
		// Get player hand result
		result = evaluate.evaluateHand(cards.get(player), cards.get(2));
		table.addAll(cards.get(2));
		playerCards.addAll(cards.get(player));
		
		// Get opponent potential hand
		opponentHandOdds = oddsObject.opponentHand(table);
		table.clear();
		table.addAll(cards.get(2));

		if(round == 1)
		{
			// Get the odds of improve the hand
			potentialHandOdds = oddsObject.potentialFlop(playerCards, table);
			playerCards.clear();
			table.clear();
			table.addAll(cards.get(2));
			playerCards.addAll(cards.get(player));

			// Calculate the odds of having a worst game then the opponent
			for(int i = result; i< opponentHandOdds.length; i++)
				worstGameOdds += opponentHandOdds[i];
			
			// Calculate the odds of improving the game
			for(int i = result+1; i< potentialHandOdds.length; i++)
				improveHandOdds += potentialHandOdds[i];
		}
		else if(round == 2)
		{
			
			// Get the odds of improve the hand
			potentialHandOdds = oddsObject.potentialTurn(playerCards, table);
			playerCards.clear();
			table.clear();
			table.addAll(cards.get(2));
			playerCards.addAll(cards.get(player));

			// Calculate the odds of having a worst game then the opponent
			for(int i = result; i< opponentHandOdds.length; i++)
				worstGameOdds += opponentHandOdds[i];
			
			// Calculate the odds of improving the game
			for(int i = result+1; i< potentialHandOdds.length; i++)
				improveHandOdds += potentialHandOdds[i];
		}
		
		// If opponent didn't bet
		if(call == 0 )
		{
			// If odds of winning is less then 50 makes check
			if(odds[player] < 50)
				betOptions[0] = 1;
			// If odds are bigger then 50 stays in the game
			else
			{
				// All in
				if(playerStack < 8 || opponentStack < 8)
					betOptions[0] = menu; 
				if((playerStack >= 8 && playerStack < 12) || (opponentStack >= 8 && opponentStack < 12))
				{
					if(improveHandOdds > 70)
					{
							betOptions[0] = 4;
							betOptions[1] = 5*bb;
					}
					// All in
					else if(worstGameOdds < 40)
						betOptions[0] = menu;
					// Check
					else
						betOptions[0] = 1;			
				}
				// Bet
				if(playerStack >= 12 || opponentStack >=12)
				{
					if(worstGameOdds < 40)
					{
						if(worstGameOdds < 20)
						{
							betOptions[0] = 3;
							betOptions[1] = bb*12;
						}
						else
						{
							betOptions[0] = 3;
							betOptions[1] = bb*8;
						}
					}
					else
					{
						if(improveHandOdds > 80)
						{
							betOptions[0] = 3;
							betOptions[1] = bb*8;
						}
						// Check
						else
							betOptions[0] = 1;
					}
				}
			}
		}	
	
		// If the opponent bet
		else
		{
			// Calculate pot Odds
			potOdds = call / (pot+call);
			potOdds *= 100;
			// Fold
			if(potOdds > odds[player] || odds[player] < 50)
				betOptions[0] = 1;
			// Continues in game
			else
			{
				// Call or All in
				if(playerStack < 4 || (opponentStack + call) < 4)
				{
					// menu fold_call 
					if(menu == 5)
					{
						if(worstGameOdds < 50 || improveHandOdds > 50)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				else if( (playerStack >= 4 && playerStack < 8) || ((opponentStack+call) <= 4 && (opponentStack+call) < 8))
				{ 			
					// menu fold_call 
					if(menu == 5)
					{
						if(odds[player] > 60 || worstGameOdds < 40 || improveHandOdds > 60)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				else if( (playerStack >= 8 && playerStack < 15) || ((opponentStack+call) <= 8 && (opponentStack+call) < 15))
				{
					if(odds[player] > 75 || worstGameOdds < 35 || improveHandOdds > 75)
						betOptions[0] = menu;
					else if(odds[player] > 65 || worstGameOdds < 40 || improveHandOdds > 70)
					{
						if(pokerChips[player] > 2*call)
						{
							betOptions[0] = 4;
							betOptions[1] = (int) (2*call);
						}
						else
							betOptions[0] = menu;
						
						// menu fold_allin 
						if(menu == 2)
							betOptions[0] = menu;
					}
					else
						betOptions[0] = 2;
				}
				else if(playerStack >= 15 || (opponentStack+call) >=15)
				{
					if(call <= 5*bb)
					{
						// If player is dealer
						if(dealer == player)
						{
							if(odds[player] > 75 || worstGameOdds < 35 )
							{
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								else
									betOptions[0] = menu;
								
								// menu fold_allin 
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(odds[player] > 60)
							{
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								else
									betOptions[0] = menu;
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(improveHandOdds > 60)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								else
									betOptions[0] = menu;
								
								// menu fold_allin 
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
						// If player is Blind
						if(dealer != player)
						{
							if(odds[player] > 80 || worstGameOdds < 30 )
							{
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
									
									if(menu == 2)
										betOptions[0] = menu;
								}
								// All in
								else
									betOptions[0] = menu;
							}
							else if(odds[player] > 65)
							{
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
									
									// fold_allin
									if(menu == 2)
										betOptions[0] = menu;
								}
								// All in
								else
									betOptions[0] = menu;
							}
							else if(improveHandOdds > 65)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
						
					}
					if(call > 5*bb)
					{
						
						// If player is dealer
						if(dealer == player)
						{
							if(odds[player] > 75 && worstGameOdds < 35)
							{
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}

							else if(odds[player] > 65)
							{
								
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// fold_all in
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(improveHandOdds > 80)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								// fold_all in
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
						
						// If player is Blind
						if(dealer != player)
						{
							if(odds[player] > 80 && worstGameOdds < 30)
							{
								
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_all in
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(odds[player] > 75)
							{
								
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(improveHandOdds > 85)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
					}		
				}	
			}
		}
	
		return betOptions;
	}
	
	// Method to simulate a computer that just plays with good odds - In River
	private int[] river_passive(int player, int dealer, float[] pokerChips,float tempPot, float[] bet,float[] odds, int menu)
	{
		int opponent = (player == 0) ? 1 : 0;
		int[] betOptions = new int[2];
		int bb = 20;
		// Convert poker chips to big blinds
		int playerStack = (int)pokerChips[player] / bb;
		int opponentStack = (int)pokerChips[opponent] / bb;
		// Pot value
		float pot = pokerChips[2] + tempPot;
		// Opponent raise value
		float call = (bet[opponent] - bet[player]);
		// Store pot odds value
		float potOdds;
			
		// If there is no bets
		if(call == 0 )
		{
			// If odds of winning is less then 50 makes check
			if(odds[player] < 50)
				betOptions[0] = 1;
			else
			{
				if(playerStack < 8 || opponentStack < 8)
					betOptions[0] = menu;
				if((playerStack >= 8 && playerStack < 12) || (opponentStack >= 8 && opponentStack < 12))
				{
					if(odds[player] > 70)
						betOptions[0] = menu;
					// Check
					else
						betOptions[0] = 1;
				}
				if(playerStack >= 12 || opponentStack >=12)
				{
					if(odds[player] > 70)
					{	
						if(odds[player] > 80)
						{
							betOptions[0] = 3;
							betOptions[1] = bb*12;
						}
						else
						{
							betOptions[0] = 3;
							betOptions[1] = bb*8;
						}
					}
					else
						betOptions[0] = 1;	
				}		
			}	
		}
		// If the opponent bet
		else
		{
			// Calculate pot Odds
			potOdds = call / (pot+call);
			potOdds *= 100;
			// Fold
			if(potOdds > odds[player] || odds[player] < 50)
				betOptions[0] = 1;
			// Continues in game
			else
			{
				// Call or All in
				if(playerStack < 4 || (opponentStack+call) < 4)
				{
					// menu fold_call 
					if(menu == 5)
					{
						if(odds[player] > 60)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				if( (playerStack >= 4 && playerStack < 8) || ((opponentStack+call) <= 4 && (opponentStack+call) < 8))
				{ 
					
					// menu fold_call 
					if(menu == 5)
					{
						if(odds[player] > 70)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				if( (playerStack >= 8 && playerStack < 15) || ((opponentStack+call) <= 8 && (opponentStack+call) < 15))
				{
					if(odds[player] > 75)
						betOptions[0] = menu;
					else
						betOptions[0] = 2;
				
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				
				if(playerStack >= 15 || (opponentStack+call) >=15)
				{
					if(call <= 5*bb)
					{
						if(odds[player] > 85)
						{

							if(pokerChips[player] > 4*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (4*call);
							}
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
							betOptions[0] = 2;
					
						// menu fold_allin 
						if(menu == 2)
							betOptions[0] = menu;
					}
					if(call > 5*bb)
					{
						if(odds[player] > 95)
							betOptions[0] = menu;
						else if(odds[player] > 90)
						{
							
							if(pokerChips[player] > 4*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (4*call);
							}
							// All in
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
							
						}
						else if(odds[player] > 80)
						{
							if(pokerChips[player] > 3*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (3*call);
							}
							// All in
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
						}
						else if(odds[player] > 70)
						{
							if(pokerChips[player] > call)
								betOptions[0] = 2;
							// All in
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
						}
						// Fold
						else
							betOptions[0] = 1;
					}		
				}
			}	
		}
		
		return betOptions;
	}
		
	// Method to simulate a computer that plays aggressive - Before Flop
	private int[] preFlop_aggressive(int player, int dealer, float[] pokerChips, float tempPot, float[] bet, HashMap<Integer, ArrayList<Cards>> cards, int menu)
	{
		int opponent = (player == 0) ? 1 : 0;
		int[] betOptions = new int[2];
		int bb = 20;
		int playerStack;
		int opponentStack; 
		if(pokerChips[player] > bb)
			playerStack = (int)pokerChips[player] / bb;
		else
			playerStack = 0;
		if(pokerChips[opponent] > bb)
			opponentStack = (int)pokerChips[opponent] / bb;
		else
			opponentStack = 0;
		int cardGroup = group(cards.get(player));
		// Opponent raise value
		int call;
		if(playerStack > 0 && opponentStack > 0)
			call = (int)(bet[opponent] - bet[player]);
		else
			call = 0;

		// Opponent didn't raise
		if(call <= bb) 
		{
			if(playerStack < 8 || (opponentStack+call) < 8)
				betOptions[0] = menu;
			if((playerStack >= 8 && playerStack < 12) || ((opponentStack+call) >= 8 && (opponentStack+call) < 12))
			{	
				if(cardGroup <= 8)
					betOptions[0] = menu;
				else
					betOptions[0] = 1;
				
			}
			if(playerStack >= 12 || (opponentStack+call) >=12)
			{
				// player is dealer
				if(player == dealer)
				{	
					if(cardGroup <= 8)
					{
						if(cardGroup <= 6)
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 10*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 10*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 5*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 5*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
						
					}	
					// Fold / check
					else
						betOptions[0] = 1;
				}
				// player is blind
				else
				{
					if(cardGroup <= 8)
					{
						if(cardGroup <= 5)
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 12*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 12*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
						{
							// menu fold_call 
							if(menu == 5)
							{
								betOptions[0] = 4;
								betOptions[1] = 8*bb;
							}
							// menu check 
							if(menu == 4)
							{
								betOptions[0] = 3;
								betOptions[1] = 8*bb;
							}
							//Menu fold_call
							if(menu == 2)
								betOptions[0] = menu;
						}
					}	
					// Check
					else
						betOptions[0] = 1;
				}	
			}
		}
		
		// Opponent raised
		if(call > bb)
		{
			if(playerStack < 2 || (opponentStack+call) < 2)
			{
				// menu fold_call 
				if(menu == 5)
					betOptions[0] = 2;
				// menu fold_allin 
				if(menu == 2)
					betOptions[0] = menu;
			}
			if( (playerStack >= 2 && playerStack < 4) || ((opponentStack+call) <= 2 && (opponentStack+call) < 4))
			{
				if(cardGroup <= 8)
					betOptions[0]=menu;
				else
					betOptions[0] = 1;
			}
			if( (playerStack >= 4 && playerStack < 12) || ((opponentStack+call) <= 4 && (opponentStack+call) < 12))
			{
				if(cardGroup <= 7)
					betOptions[0]=menu;
				else
					betOptions[0] = 1;
			}
			if(playerStack >= 12 || (opponentStack+call) >=12)
			{
				if(call <= 5*bb)
				{
					if(cardGroup <= 8)
					{
						if(cardGroup <= 6)
						{
							
							if(pokerChips[player] > 5*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (5*call);
							}
							else
								betOptions[0] = menu;
							
							// menu fold_allin
							if(menu == 2)
								betOptions[0] = menu;
						}
						// Call
						else
							betOptions[0] = 2;
					}
					// Fold
					else
						betOptions[0] = 1;	
				}
				if(call > 5*bb && call <= 10*bb)
				{
					if(cardGroup <= 7)
					{
						if(cardGroup <= 5)
						{
							if(cardGroup <= 3)
							{
								if(pokerChips[player] > 6*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (6*call);
								}
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							else
							{
								if(pokerChips[player] > 3*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (3*call);
								}
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}			
						}
						// call
						else
							betOptions[0] = 2;
					}
					// fold
					else
						betOptions[0] = 1;
				}
				if(call > 10*bb && call <= 15*bb)
				{
					if(cardGroup <= 6)
					{
						if(cardGroup <= 5)
						{
							if(cardGroup <= 3)
							{
								
								if(pokerChips[player] > 4*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (4*call);
								}
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							else
							{
								
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}				
						}
						// call
						else
							betOptions[0] = 2;
					}
					// fold
					else
						betOptions[0] = 1;
				}
				if(call > 15*bb)
				{
					if(cardGroup <= 6)
					{
						if(cardGroup <= 5)
						{
							if(cardGroup <= 4)
							{
								if(pokerChips[player] > 5*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (5*call);
								}
								else
									betOptions[0] = menu;
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							else
							{
								if(pokerChips[player] > 3*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (3*call);
								}
								else
									betOptions[0] = menu;
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							
						}
						// call
						else
							betOptions[0] = 2;
					}
					// fold
					else
						betOptions[0] = 1;
				}		
			}		
		}
		
		return betOptions;
	}
	
	// // Method to simulate a computer that plays aggressive  - In Flop
	private int[] flop_aggressive(int player, int dealer, float[] pokerChips,float temppot, float[] bet,float[] odds, 
			HashMap<Integer, ArrayList<Cards>> cards, int menu, int round)
	{
		int opponent = (player == 0) ? 1 : 0;
		int[] betOptions = new int[2];
		int bb = 20;
		int playerStack = (int)pokerChips[player] / bb;
		int opponentStack = (int)pokerChips[opponent] / bb;
		// Pot value
		float pot = pokerChips[2] + temppot;
		// Opponent raise value
		float call = (bet[opponent] - bet[player]);
		// Store pot odds value
		float potOdds;
		int result;
		// Stores the odds of the opponent has a certain hand
		float[] opponentHandOdds = new float[11];
		// Stores the odds to come a certain hand
		float[] potentialHandOdds = new float[11];
		// Gets the sum of the odds has a worse hand
		float worstGameOdds = 0;
		// Stores temporally player cards
		float improveHandOdds = 0;		
		// Stores temporally table cards
		ArrayList<Cards> table = new ArrayList<>();
		// Stores temporally player cards
		ArrayList<Cards> playerCards = new ArrayList<>();
		
		System.out.println(cards.get(2));
		
		// Get player hand result
		result = evaluate.evaluateHand(cards.get(player), cards.get(2));
		table.addAll(cards.get(2));
		playerCards.addAll(cards.get(player));
		
		// Get opponent potential hand
		opponentHandOdds = oddsObject.opponentHand(table);
		table.clear();
		table.addAll(cards.get(2));

		if(round == 1)
		{
			// Get the odds of improve the hand
			potentialHandOdds = oddsObject.potentialFlop(playerCards, table);
			playerCards.clear();
			table.clear();
			table.addAll(cards.get(2));
			playerCards.addAll(cards.get(player));

			// Calculate the odds of having a worst game then the opponent
			for(int i = result; i< opponentHandOdds.length; i++)
				worstGameOdds += opponentHandOdds[i];
			
			// Calculate the odds of improving the game
			for(int i = result+1; i< potentialHandOdds.length; i++)
				improveHandOdds += potentialHandOdds[i];
		}
		else if(round == 2)
		{
			
			// Get the odds of improve the hand
			potentialHandOdds = oddsObject.potentialTurn(playerCards, table);
			playerCards.clear();
			table.clear();
			table.addAll(cards.get(2));
			playerCards.addAll(cards.get(player));

			// Calculate the odds of having a worst game then the opponent
			for(int i = result; i< opponentHandOdds.length; i++)
				worstGameOdds += opponentHandOdds[i];
			
			// Calculate the odds of improving the game
			for(int i = result+1; i< potentialHandOdds.length; i++)
				improveHandOdds += potentialHandOdds[i];
		}
		
		// If there is no bets
		if(call == 0 )
		{
			// If odds of winning is less then 50 makes check
			if(odds[player] < 50)
				betOptions[0] = 1;
			// If odds are bigger then 50 stays in the game
			else
			{
				if(playerStack < 8 || opponentStack < 8)
					betOptions[0] = menu;
				if((playerStack >= 8 && playerStack < 12) || (opponentStack >= 8 && opponentStack < 12))
				{
					if(improveHandOdds > 60)
					{
							betOptions[0] = 4;
							betOptions[1] = 7*bb;
					}
					else if(worstGameOdds < 50)
						betOptions[0] = menu;
					// Check
					else
						betOptions[0] = 1;			
				}
				if(playerStack >= 12 || opponentStack >=12)
				{
					if(worstGameOdds < 60)
					{
						if(worstGameOdds < 40)
						{
							betOptions[0] = 3;
							betOptions[1] = bb*12;
						}
						else
						{
							betOptions[0] = 3;
							betOptions[1] = bb*8;
						}
					}
					else
					{
						if(improveHandOdds > 70)
						{
							betOptions[0] = 3;
							betOptions[1] = bb*10;
						}
						// Check
						else
							betOptions[0] = 1;
					}
				}
			}
		}	

		// If the opponent bet
		else
		{
			// Calculate pot Odds
			potOdds = call / (pot+call);
			potOdds *= 100;
			// Fold
			if(potOdds > odds[player] || odds[player] < 50)
				betOptions[0] = 1;
			// Continues in game
			else
			{
				if(playerStack < 4 || (opponentStack + call) < 4)
				{
					// menu fold_call 
					if(menu == 5)
					{
						if(worstGameOdds < 60 || improveHandOdds > 40)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				else if( (playerStack >= 4 && playerStack < 8) || ((opponentStack+call) <= 4 && (opponentStack+call) < 8))
				{ 			
					// menu fold_call 
					if(menu == 5)
					{
						if(odds[player] > 50 || worstGameOdds < 50 || improveHandOdds > 50)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				else if( (playerStack >= 8 && playerStack < 15) || ((opponentStack+call) <= 8 && (opponentStack+call) < 15))
				{
					if(odds[player] > 70 || worstGameOdds < 40 || improveHandOdds > 70)
						betOptions[0] = menu;
					else if(odds[player] > 55 || worstGameOdds < 50 || improveHandOdds > 60)
					{
						if(pokerChips[player] > 3*call)
						{
							betOptions[0] = 4;
							betOptions[1] = (int) (3*call);
						}
						else
							betOptions[0] = menu;
					}
					else
						betOptions[0] = 2;
				
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				else if(playerStack >= 15 || (opponentStack+call) >=15)
				{
					if(call <= 7*bb)
					{
						if(dealer == player)
						{
							if(odds[player] > 65 || worstGameOdds < 45 )
							{
								if(pokerChips[player] > 5*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (5*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin 
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(odds[player] > 50)
							{
								
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin 
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(improveHandOdds > 50)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin 
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
						if(dealer != player)
						{
							if(odds[player] > 70 || worstGameOdds < 40 )
							{
								if(pokerChips[player] > 5*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (5*call);
									
									// menu fold_allin
									if(menu == 2)
										betOptions[0] = menu;
								}
								// All in
								else
									betOptions[0] = menu;
							}
							else if(odds[player] > 55)
							{
								if(pokerChips[player] > 3*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (3*call);
									
									// menu fold_allin
									if(menu == 2)
										betOptions[0] = menu;
								}
								// All in
								else
									betOptions[0] = menu;
							}
							else if(improveHandOdds > 55)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
						
					}
					if(call > 7*bb)
					{					
						if(dealer == player)
						{

							if(odds[player] > 65 && worstGameOdds < 45)
							{
								if(pokerChips[player] > 5*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (5*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							else if(odds[player] > 65)
							{
							
								if(pokerChips[player] > 2*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (2*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}

							else if(improveHandOdds > 60)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
						
						if(dealer != player)
						{

							if(odds[player] > 70 && worstGameOdds < 40)
							{
								if(pokerChips[player] > 5*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (5*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}

							else if(odds[player] > 65)
							{
								if(pokerChips[player] > 3*call)
								{
									betOptions[0] = 4;
									betOptions[1] = (int) (3*call);
								}
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}

							else if(improveHandOdds > 70)
							{
								if(pokerChips[player] > call)
									betOptions[0] = 2;
								// All in
								else
									betOptions[0] = menu;
								
								// menu fold_allin
								if(menu == 2)
									betOptions[0] = menu;
							}
							// Fold
							else
								betOptions[0] = 1;
						}
					}		
				}	
			}
		}
		return betOptions;
	}

	// Method to simulate a computer that just plays aggressive - In River
	private int[] river_aggressive(int player, int dealer, float[] pokerChips,float temppot, float[] bet,float[] odds, int menu)
	{
		int opponent = (player == 0) ? 1 : 0;
		int[] betOptions = new int[2];
		int bb = 20;
		int playerStack = (int)pokerChips[player] / bb;
		int opponentStack = (int)pokerChips[opponent] / bb;
		// Pot value
		float pot = pokerChips[2] + temppot;
		// Opponent raise value
		float call = (bet[opponent] - bet[player]);
		// Store pot odds value
		float potOdds;
			
		//If there is no bets
		if(call == 0 )
		{
			// If odds of winning is less then 50 makes check
			if(odds[player] < 50)
				betOptions[0] = 1;
			else
			{
				if(playerStack < 8 || opponentStack < 8)
					betOptions[0] = menu;
				if((playerStack >= 8 && playerStack < 12) || (opponentStack >= 8 && opponentStack < 12))
				{
					if(odds[player] > 60)
						betOptions[0] = menu;
					// Check
					else
						betOptions[0] = 1;
				}
				if(playerStack >= 12 || opponentStack >=12)
				{
					if(odds[player] > 60)
					{
						if(odds[player] > 70)
						{
							betOptions[0] = 3;
							betOptions[1] = bb*12;
						}
						else
						{
							betOptions[0] = 3;
							betOptions[1] = bb*8;
						}
					}
					else
						betOptions[0] = 1;	
				}		
			}	
		}
		// If the opponent bet
		else
		{
			// Calculate pot Odds
			potOdds = call / (pot+call);
			potOdds *= 100;
			// Fold
			if(potOdds > odds[player] || odds[player] < 50)
				betOptions[0] = 1;
			// Continues in game
			else
			{
				if(playerStack < 4 || (opponentStack+call) < 4)
					betOptions[0] = menu;
				if( (playerStack >= 4 && playerStack < 8) || ((opponentStack+call) <= 4 && (opponentStack+call) < 8))
				{ 	
					if(menu == 5)
					{
						if(odds[player] > 60)
							betOptions[0] = menu;
						else
							betOptions[0] = 2;
					}
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				if( (playerStack >= 8 && playerStack < 15) || ((opponentStack+call) <= 8 && (opponentStack+call) < 15))
				{
					if(odds[player] > 60)
						betOptions[0] = menu;
					else
						betOptions[0] = 2;
				
					// menu fold_allin 
					if(menu == 2)
						betOptions[0] = menu;
				}
				
				if(playerStack >= 15 || (opponentStack+call) >=15)
				{
					if(call <= 8*bb)
					{
						if(odds[player] > 75)
						{
							if(pokerChips[player] > 6*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (6*call);
							}
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
							betOptions[0] = 2;
					
						// menu fold_allin 
						if(menu == 2)
							betOptions[0] = menu;
					}
					if(call > 5*bb)
					{
						if(odds[player] > 85)
							betOptions[0] = menu;
						else if(odds[player] > 80)
						{
							if(pokerChips[player] > 6*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (6*call);
							}
							// All in
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
							
						}
						else if(odds[player] > 70)
						{
							
							if(pokerChips[player] > 4*call)
							{
								betOptions[0] = 4;
								betOptions[1] = (int) (4*call);
							}
							// All in
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
						}
						else if(odds[player] > 50)
						{
							if(pokerChips[player] > call)
								betOptions[0] = 2;
							// All in
							else
								betOptions[0] = menu;
							
							// menu fold_allin 
							if(menu == 2)
								betOptions[0] = menu;
						}
						else
							betOptions[0] = 1;
					}		
				}
			}	
		}
		
		return betOptions;
	}
	
	// Method to define which group the cards belong
	private int group(ArrayList<Cards> card)
	{
		int group = 9;
	
		
		int[] rankCard = new int[2];
		int[] suitCard = new int[2];
		
		Boolean _AA = false;
		Boolean _KK = false;
		Boolean _QQ = false;
		Boolean _AKs = false;
		Boolean _AKo = false;
		Boolean _JJ = false;
		Boolean _AQs = false;
		Boolean _AQo = false;
		Boolean _TT = false;
		Boolean _99 = false;
		Boolean _AJs = false;
		Boolean _KQs = false;
		Boolean _88 = false;
		Boolean _77 = false;
		Boolean _AJo = false;
		Boolean _AT = false;
		Boolean _KQo = false;
		Boolean _KJs = false;
		Boolean _66 = false;
		Boolean _55 = false;
		Boolean _A9s_A2s = false;
		Boolean _KJo = false;
		Boolean _KTs = false;
		Boolean _QJs = false;
		Boolean _QTs = false;
		Boolean _JTs = false;
		Boolean _44 = false;
		Boolean _33 = false;
		Boolean _22 = false;
		Boolean _A9o_A2o = false;
		Boolean _KTo = false;
		Boolean _QJo = false;
		Boolean _QTo = false;
		Boolean _JTo = false;
		Boolean _T9s = false;
		Boolean _98s = false;
		Boolean _87s = false;
		Boolean _76s = false;
		Boolean _65s = false;
		Boolean _54s = false;
		Boolean _K9 = false;
		Boolean _K8 = false;
		Boolean _Q9s = false;
		Boolean _Q8s = false;
		Boolean _J9s = false;
		Boolean _T8s = false;
		Boolean _T9o = false;
		Boolean _97s = false;
		Boolean _98o = false;
		Boolean _86s = false;
		Boolean _87o = false;
		Boolean _75s = false;
		Boolean _76o = false;
		Boolean _64s = false;
		
		rankCard[0] = card.get(0).getRank();
		rankCard[1] = card.get(1).getRank();
		
		suitCard[0] = card.get(0).getSuit();
		suitCard[1] = card.get(1).getSuit();
		
		// AA
		if(rankCard[0] == 0 && rankCard[1] == 0)
			_AA = true;
		// KK
		if(rankCard[0] == 12 && rankCard[1] == 12)
			_KK = true;
		
		// group 1
		if(_AA || _KK)
			group = 1;
		
		/* ------------------------------------------------ */
		
		// QQ
		if(rankCard[0] == 11 && rankCard[1] == 11)
			_QQ = true;
		// AK suited
		if((rankCard[0] == 0 && rankCard[1] == 12 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 12 && rankCard[1] == 0 && suitCard[0] ==  suitCard[1]))
			_AKs = true;
		// AK out suited
		if((rankCard[0] == 0 && rankCard[1] == 12 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 12 && rankCard[1] == 0 && suitCard[0] !=  suitCard[1]))
			_AKo = true;
		// JJ
		if(rankCard[0] == 10 && rankCard[1] == 10)
			_JJ = true;
		
		// group 2
		if(_QQ || _AKs || _AKo || _JJ)
			group = 2;
		
		/* ------------------------------------------------ */
		
		// AQ suited
		if((rankCard[0] == 0 && rankCard[1] == 11 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 11 && rankCard[1] == 0 && suitCard[0] ==  suitCard[1]))
			_AQs = true;
		// AQ out suited
		if((rankCard[0] == 0 && rankCard[1] == 11 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 11 && rankCard[1] == 0 && suitCard[0] !=  suitCard[1]))
			_AQo = true;
		// TT
		if(rankCard[0] == 9 && rankCard[1] == 9)
			_TT = true;
		// 99
		if(rankCard[0] == 8 && rankCard[1] == 8)
			_99 = true;
			
		// group 3
		if(_AQs || _AQo || _TT || _99)
			group = 3;
		
		/* ------------------------------------------------ */
		
		// AJ suited
		if((rankCard[0] == 0 && rankCard[1] == 10 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 10 && rankCard[1] == 0 && suitCard[0] ==  suitCard[1]))
			_AJs = true;
		// KQ suited
		if((rankCard[0] == 12 && rankCard[1] == 11 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 11 && rankCard[1] == 12 && suitCard[0] ==  suitCard[1]))
			_KQs = true;
		// 88
		if(rankCard[0] == 7 && rankCard[1] == 7)
			_88 = true;
		// 77
		if(rankCard[0] == 6 && rankCard[1] == 6)
			_77 = true;
			
		// group 4
		if(_AJs || _KQs || _88 || _77)
			group = 4;
		
		/* ------------------------------------------------ */
		
		// AJ out suited
		if((rankCard[0] == 0 && rankCard[1] == 10 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 10 && rankCard[1] == 0 && suitCard[0] !=  suitCard[1]))
			_AJo = true;
		// AT
		if((rankCard[0] == 0 && rankCard[1] == 9 ) || (rankCard[0] == 9 && rankCard[1] == 0))
			_AT = true;
		// KQ out suited
		if((rankCard[0] == 12 && rankCard[1] == 11 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 11 && rankCard[1] == 12 && suitCard[0] !=  suitCard[1]))
			_KQo = true;
		// KJ suited
		if((rankCard[0] == 12 && rankCard[1] == 10 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 10 && rankCard[1] == 12 && suitCard[0] ==  suitCard[1]))
			_KJs = true;
		// 66
		if(rankCard[0] == 5 && rankCard[1] == 5)
			_66 = true;
		// 55
		if(rankCard[0] == 4 && rankCard[1] == 4)
			_55 = true;
		
		// group 5
		if(_AJo || _AT || _KQo || _KJs || _66 || _55)
			group = 5;
		
		/* ------------------------------------------------ */
		
		// A9 suited till A2 suited
		if((rankCard[0] == 0 && (rankCard[1] > 1 && rankCard[1] < 8) && suitCard[0] == suitCard[1]) ||
				rankCard[1] == 0 && (rankCard[0] > 1 && rankCard[0] < 8) && suitCard[0] == suitCard[1])
			_A9s_A2s = true;
		// KJ out suited
		if((rankCard[0] == 12 && rankCard[1] == 10 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 10 && rankCard[1] == 12 && suitCard[0] !=  suitCard[1]))
			_KJo = true;
		// KT suited
		if((rankCard[0] == 12 && rankCard[1] == 9 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 9 && rankCard[1] == 12 && suitCard[0] ==  suitCard[1]))
			_KTs = true;
		// QJ suited
		if((rankCard[0] == 11 && rankCard[1] == 10 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 10 && rankCard[1] == 11 && suitCard[0] ==  suitCard[1]))
			_QJs = true;
		// QT suited
		if((rankCard[0] == 11 && rankCard[1] == 9 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 9 && rankCard[1] == 11 && suitCard[0] ==  suitCard[1]))
			_QTs = true;
		// JT suited
		if((rankCard[0] == 10 && rankCard[1] == 9 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 9 && rankCard[1] == 10 && suitCard[0] ==  suitCard[1]))
			_JTs = true;
		// 44
		if(rankCard[0] == 3 && rankCard[1] == 3)
			_44 = true;
		// 33
		if(rankCard[0] == 2 && rankCard[1] == 2)
			_33 = true;
		// 22
		if(rankCard[0] == 1 && rankCard[1] == 1)
			_22 = true;
		
		// group 6
		if(_A9s_A2s || _KJo || _KTs || _QJs || _QTs || _JTs || _44 || _33 || _22)
			group = 6;
	
		/* ------------------------------------------------ */
		
		// A9 out suited till A2 out suited
		if((rankCard[0] == 0 && (rankCard[1] > 1 && rankCard[1] < 8) && suitCard[0] != suitCard[1]) ||
				rankCard[1] == 0 && (rankCard[0] > 1 && rankCard[0] < 8) && suitCard[0] != suitCard[1])
			_A9o_A2o = true;
		// KT out suited
		if((rankCard[0] == 12 && rankCard[1] == 9 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 9 && rankCard[1] == 12 && suitCard[0] !=  suitCard[1]))
			_KTo = true;
		// QJ out suited
		if((rankCard[0] == 11 && rankCard[1] == 10 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 10 && rankCard[1] == 11 && suitCard[0] !=  suitCard[1]))
			_QJo = true;
		// QT out suited
		if((rankCard[0] == 11 && rankCard[1] == 9 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 9 && rankCard[1] == 11 && suitCard[0] !=  suitCard[1]))
			_QTo = true;
		// JT out suited
		if((rankCard[0] == 10 && rankCard[1] == 9 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 9 && rankCard[1] == 10 && suitCard[0] !=  suitCard[1]))
			_JTo = true;
		// T9 suited
		if((rankCard[0] == 9 && rankCard[1] == 8 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 8 && rankCard[1] == 9 && suitCard[0] ==  suitCard[1]))
			_T9s = true;
		// 98 suited
		if((rankCard[0] == 8 && rankCard[1] == 7 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 7 && rankCard[1] == 8 && suitCard[0] ==  suitCard[1]))
			_98s = true;
		// 87 suited
		if((rankCard[0] == 7 && rankCard[1] == 6 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 6 && rankCard[1] == 7 && suitCard[0] ==  suitCard[1]))
			_87s = true;
		// 76 suited
		if((rankCard[0] == 6 && rankCard[1] == 5 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 5 && rankCard[1] == 6 && suitCard[0] ==  suitCard[1]))
			_76s = true;
		// 65 suited
		if((rankCard[0] == 5 && rankCard[1] == 4 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 4 && rankCard[1] == 5 && suitCard[0] ==  suitCard[1]))
			_65s = true;
		// 54 suited
		if((rankCard[0] == 4 && rankCard[1] == 3 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 3 && rankCard[1] == 4 && suitCard[0] ==  suitCard[1]))
			_54s = true;
		
		// group 7
		if(_A9o_A2o || _KTo || _QJo || _QTo || _JTo || _T9s || _98s || _87s ||_76s ||_65s ||_54s )
			group = 7;
				
		/* ------------------------------------------------ */
		// K9
		if((rankCard[0] == 12 && rankCard[1] == 8 ) || (rankCard[0] == 8 && rankCard[1] == 12))
			_K9 = true;
		// K8
		if((rankCard[0] == 12 && rankCard[1] == 7 ) || (rankCard[0] == 7 && rankCard[1] == 12))
			_K8 = true;
		// Q9 suited
		if((rankCard[0] == 11 && rankCard[1] == 8 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 8 && rankCard[1] == 11 && suitCard[0] ==  suitCard[1]))
			_Q9s = true;
		// Q8 suited
		if((rankCard[0] == 11 && rankCard[1] == 7 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 7 && rankCard[1] == 11 && suitCard[0] ==  suitCard[1]))
			_Q8s = true;
		// J9 suited
		if((rankCard[0] == 10 && rankCard[1] == 8 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 8 && rankCard[1] == 10 && suitCard[0] ==  suitCard[1]))
			_J9s = true;
		// T8 suited
		if((rankCard[0] == 9 && rankCard[1] == 7 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 7 && rankCard[1] == 9 && suitCard[0] ==  suitCard[1]))
			_T8s = true;
		// T9 out suited
		if((rankCard[0] == 9 && rankCard[1] == 8 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 8 && rankCard[1] == 9 && suitCard[0] !=  suitCard[1]))
			_T9o = true;
		// 97 suited
		if((rankCard[0] == 8 && rankCard[1] == 6 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 6 && rankCard[1] == 8 && suitCard[0] ==  suitCard[1]))
			_97s = true;
		// 98 out suited
		if((rankCard[0] == 8 && rankCard[1] == 7 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 7 && rankCard[1] == 8 && suitCard[0] !=  suitCard[1]))
			_98o = true;
		// 86 suited
		if((rankCard[0] == 7 && rankCard[1] == 5 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 5 && rankCard[1] == 7 && suitCard[0] ==  suitCard[1]))
			_86s = true;
		// 87 out suited
		if((rankCard[0] == 7 && rankCard[1] == 6 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 6 && rankCard[1] == 7 && suitCard[0] !=  suitCard[1]))
			_87o = true;
		// 75 suited
		if((rankCard[0] == 6 && rankCard[1] == 4 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 4 && rankCard[1] == 6 && suitCard[0] ==  suitCard[1]))
			_75s = true;
		// 76 out suited
		if((rankCard[0] == 6 && rankCard[1] == 5 && suitCard[0] !=  suitCard[1] ) || 
				(rankCard[0] == 5 && rankCard[1] == 6 && suitCard[0] !=  suitCard[1]))
			_76o = true;
		// 64 suited
		if((rankCard[0] == 5 && rankCard[1] == 3 && suitCard[0] ==  suitCard[1] ) || 
				(rankCard[0] == 3 && rankCard[1] == 5 && suitCard[0] ==  suitCard[1]))
			_65s = true;
		
		// group 8
		if( _K9 || _K8 || _Q9s || _Q8s || _J9s || _T8s || _T9o || _97s || _98o ||
				_86s || _87o || _75s || _76o || _64s)
			group = 8;
		
		return group;		
	}
	
}
