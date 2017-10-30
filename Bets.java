/* Description: Class to define all bets in a game
 * Pay blinds
 * Bet
 * Check
 * Fold
 * Name: Filipe Andre de Matos Bicho
 * Last update: 30/10/2017
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Bets {

	// Choose the type of opponent
	//Simulador computador = new Simulador();
	
	// Store the probability of both players win the game
	private float[] playerOdds = new float[2];
	// Store previously pot money
	private float tempPot=0;
	// Store game mode
	private int gameMode;
	// Store the actual bet round
	private int round;
	// Dealer
	private int dealer;
	// Store players cards
	HashMap<Integer, ArrayList<Cards>> cards = new HashMap<>();

	
	/* 1- bet options
	 * 2- bet value
	 */
	private int[] betOption = new int[2];

	private int smallBlind = 10, 	// Small blind value
			bigBlind = 20,			// Big blind value 
			pot = 2;				// Position where pot chips are stored
	
	// If a player made an initial bet
	private boolean initialBet = true;
	// If a player made an initial check 
	private boolean initialCheck = false;			
	
	/* Players bets
	 * 0 - Player 1 bet
	 * 1 - Player 2 bet
	 */
	private float[] bet = new float[2];
	
	/* 0 - Player 1 poker chips
	 * 1 - Player 2 poker chips
	 * 2 - Pot poker chips
	 * 3 - Checks if games continues
	 */
	private float[] pokerChips = new float[4];
	
	// Confirmation that can go to the next round (both player with value 1)
	private int[] confirmation = new int[2];
	
	// Store the initial poker chip os each player
	private float[] initialPokerChips = new float[2];
	
	// User input
	Scanner input = new Scanner(System.in);
	
	// Reset all values
	public void reset()
	{
		bet[0] = 0;
		bet[1] = 0;
		confirmation[0] = 0;
		confirmation[1] = 0;
		pokerChips[pot] = 0;
		pokerChips[3] = 0;
	}
	
	// Method to make bets before the flop
	public float[] betsPreFlop(float[] pokerChips, int dealer, float[] odds, int gameMode, HashMap<Integer,ArrayList<Cards>> cards)
	{
		// Get Player cards
		this.cards.put(0, cards.get(0));
		this.cards.put(1, cards.get(1));
		
		
		// Initial round
		this.round = 0;
		
		// Get dealer
		this.dealer = dealer;
		
		// Get odds 
		playerOdds[0] = (float) odds[0];
		playerOdds[1] = (float) odds[1];
		
		// Initialize values
		reset();
		
		// Small blind bet
		initialBet = true;
		
		// Get Poker chips
		initialPokerChips[0] = pokerChips[0];
		initialPokerChips[1] = pokerChips[1];
		
		// Get game mode
		this.gameMode = gameMode;
		
		this.pokerChips = pokerChips;
		int blind;
		
		// Alternate dealer with small blind
		blind = (dealer == 0) ? 1 : 0;
		
		// If blind doesn't have enough money makes all in
		if(pokerChips[blind] <= bigBlind)
		{
			// If blind has less money than small blind makes all in
			if(pokerChips[blind] <= smallBlind)
			{
				// All in
				bet[blind] = pokerChips[blind];
				pokerChips[blind] -= bet[blind];
				
				// Dealer just pay blind all in
				pokerChips[dealer] -=bet[blind];
				
				// Set pot
				pokerChips[pot] = 2*bet[blind];
				
				// End of game
				confirmation[0] = 1;
				confirmation[1] = 1;
				
				return pokerChips;
			}
			else
			{
				// Dealer pays small blind
				bet[dealer] = smallBlind; 
				pokerChips[dealer] -= bet[dealer];
				
				// Set pot
				pokerChips[pot] = bet[dealer]+bet[blind];
				
				initialBet = false;
				// Blind has to do all in
				allIn(blind);
				return pokerChips;
			}
		}
		// If dealer has less than small blind makes all in
		if(pokerChips[dealer] <= smallBlind)
		{
			// All in
			bet[dealer] = pokerChips[dealer];
			pokerChips[dealer] -= bet[dealer];
			
			// Blind pays all in
			pokerChips[blind] -=bet[dealer];
			
			// Set pot
			pokerChips[pot] = 2*bet[dealer];
			
			// End of game
			confirmation[0] = 1;
			confirmation[1] = 1;
			
			return pokerChips;
			
		}
		// If both players have enough money
		if(pokerChips[blind] > bigBlind && pokerChips[dealer] > smallBlind) 
		{
			// Dealer pays small blind
			bet[dealer] = smallBlind; 
			bet[blind] = bigBlind;
			
			// Blind pays big blind
			pokerChips[dealer] -= bet[dealer];
			pokerChips[blind] -= bet[blind];
			
			// Set pot
			pokerChips[pot] = bet[dealer]+bet[blind];
			
			// Dealer choose his bet option
			menuFold_Call(dealer);
		}
		
		// Check if game continues
		if(confirmation[0] != 1 && confirmation[1] != 1 )
			System.out.println("Erro nas bets");
		
		return pokerChips;
	}
	
	// Method to make bets after the flop
	public float[] bets(float[] pokerChips, int dealer, float[] odds, int gameMode, int round, HashMap<Integer,ArrayList<Cards>> cards)
	{
		// Get Player cards
		this.cards.put(0, cards.get(0));
		this.cards.put(1, cards.get(1));
		
		// Get table cards
		this.cards.put(2, cards.get(2));
		
		// Get round
		this.round = round;
		
		// Get dealer
		this.dealer = dealer;
				
		// Stores pot poker chips 
		tempPot = pokerChips[pot];
		
		// Get player odds
		playerOdds[0] = odds[0];
		playerOdds[1] = odds[1];
		
		// Initialize values
		reset();
		
		// There is no initial bet
		initialBet = false;
		
		// If first player makes check the other player has to answer
		initialCheck = true;	
		
		// Store player chips
		initialPokerChips[0] = pokerChips[0];
		initialPokerChips[1] = pokerChips[1];
		
		// Get game mode
		this.gameMode = gameMode;
		
		// Get poker chips
		this.pokerChips = pokerChips;
		int player;
		
		// Alternate players
		player = (dealer == 0) ? 1 : 0;
		
		// Player bet options
		menuCheck(player);
		 
		// Update pot poker chips
		pokerChips[pot]+=tempPot;
		tempPot=0;
		
		// Check if game continues
		if(confirmation[0] != 1 && confirmation[1] != 1 )
			System.out.println("Error in Bets()");

		return pokerChips;
	}
	
	/* Fold - Player give up
	 * Call - Equals opponent bet
	 * bet blind - Bet a blind value
	 * bet valor - Bet a specific value
	 * All-in - bet all the poker chips
	 */
	public void menuFold_Call(int player)
	{
		if(gameMode == 1 || (gameMode == 2 && player == 0) || (gameMode == 3 && player == 0)
				|| (gameMode == 4 && player == 0) || (gameMode == 5 && player == 0))
		{
			System.out.println("player " + (player+1) + " make your bet: ");
			System.out.println("1 - Fold ");
			System.out.println("2 - Call ");
			System.out.println("3 - Bet blind");
			System.out.println("4 - Bet value");
			System.out.println("5 - All in");
		}
		
		betOption = gameMode(player,gameMode,5);

		switch(betOption[0])
		{
			case 1: 
				fold(player);
				break;
			case 2: 
				call(player);
				break;
			case 3: 
				betBlind(player);
				break;
			case 4: 
				betValor(player);
				break;
			case 5: 
				allIn(player);
				break;
			default:
				System.out.println("Wrong option - Try again!");
				menuFold_Call(player);
		}
		
		// If dealer makes call then show check menu 
		if (betOption[0] == 2 && initialBet == true)
		{
			player = (player == 0) ? 1 : 0;	
			initialBet = false;
			menuCheck(player);
		}	
	}

	/* Fold - Player give up
	 * All-in - bet all the poker chips
	 */
	public void menuFold_Allin(int player)
	{
		if(gameMode == 1 || (gameMode == 2 && player == 0) || (gameMode == 3 && player == 0)
				|| (gameMode == 4 && player == 0) || (gameMode == 5 && player == 0))
		{
			System.out.println("player " + (player+1) + " make your bet: ");
			System.out.println("1 - Fold ");
			System.out.println("2 - Call");
		}
		
		betOption = gameMode(player,gameMode,2);
		
		switch(betOption[0])
		{
			case 1: 
				fold(player);
				break;
			case 2: 
				call(player);
				break;
			default:
				System.out.println("Wrong option - Try again!");
				menuFold_Allin(player);		
		}	
	}
	
	/* Check - There is no bet
	 * bet blind - Bet a blind value
	 * bet valor - Bet a specific value
	 * All-in - bet all the poker chips
	 */
	public void menuCheck(int player)
	{	
		if(gameMode == 1 || (gameMode == 2 && player == 0) || (gameMode == 3 && player == 0)
				|| (gameMode == 4 && player == 0) || (gameMode == 5 && player == 0))
		{
			System.out.println("player " + (player+1) + " defina a sua bet: ");
			System.out.println("1 - Check ");
			System.out.println("2 - Bet blind");
			System.out.println("3 - Bet value");
			System.out.println("4 - All in");
		}
		
		betOption = gameMode(player,gameMode,4);
		
		switch(betOption[0])
		{
			// If check
			case 1:

				confirmation[player] = 1;
				System.out.println("O player " + (player+1) + " checked.");
				// If it was the first check the opponent has to talk
				if(initialCheck == true && initialBet == false)
				{
					initialCheck = false;
					player = (player == 0) ? 1 : 0;
					menuCheck(player);
				}		
				break;
			case 2: 
				betBlind(player);
				break;
			case 3: 
				betValor(player);
				break;	
			case 4: 
				allIn(player);
				break;
			default:
				System.out.println("Wrong option - Try again!");
				menuCheck(player);
		}	
	}

	// Method when a player makes fold
	public void fold(int player)
	{
		
		System.out.println("Player " + (player+1) + " makes fold");
		
		int opponent = (player == 0) ? 1 : 0;
		
		// Opponent wins the pot
		pokerChips[opponent] += pokerChips[pot] + tempPot;
		System.out.println("Player " + (opponent+1) + " wins " + (pokerChips[pot]+tempPot));
		
		// End of game
		confirmation[0] = 1;
		confirmation[1] = 1;
		pokerChips[3] = 1;
		pokerChips[pot] = 0;
	}

	// Method when the player equals opponent bet
	public void call(int player)
	{
		int opponent = (player == 0) ? 1 : 0;
		
		// Stores old bet
		float tempbet;
		
		// If call is bigger then player money makes all in
		if((bet[opponent] - bet[player]) >= pokerChips[player] )
		{
			tempbet = bet[player];
			bet[player] = pokerChips[player];
			
			System.out.println("Player " + (player+1) + " makes All in with (" + bet[player] + ")");
		
			pokerChips[player] -= bet[player];
			bet[player] += tempbet;

			// Opponent gets difference
			pokerChips[opponent] = (pokerChips[opponent] + bet[opponent]) - bet[player];
			pokerChips[pot]= bet[player]*2;	
		}
		else
		{
			tempbet = bet[player];
			// Bet is the difference of bets
			bet[player] = bet[opponent] - bet[player];
			System.out.println("Player " + (player+1) + " makes Call (" + bet[player] + ")");
			pokerChips[player] -= bet[player];
			bet[player] += tempbet;
			pokerChips[pot]= bet[player] + bet[opponent];	
					
		}
		
		initialCheck = false;
		
		// Bet cycle is finish
		confirmation[player] = 1;
		// Games continues
		pokerChips[3] = 0;
	}
	
	// Method to bet blind value
	public void betBlind(int player)
	{	
		int opponent = (player == 0) ? 1 : 0;
		int flag = 0;
		
		// If is the initial round
		if(initialBet == true && (bigBlind+smallBlind) < pokerChips[player] )
		{

			System.out.println("Player " + (player+1) + " makes a bet of " + (bigBlind+smallBlind));
			float tempbet = bet[player];
			
			// Bets big blind + small blind
			bet[player] = (bigBlind+smallBlind); 
			pokerChips[player] -= bet[player];
			bet[player] += tempbet; 
			pokerChips[pot] = bet[player]+bet[opponent];		
			flag=1;
		}
		// If is not the initial bet
		if(initialBet == false && 2*bigBlind <= pokerChips[player] && flag ==0 )
		{

			System.out.println("Player " + (player+1) + " makes a bet of " + (int)(bigBlind+bet[opponent]));
			
			// Player bet opponent bet + big blind
			bet[player] = bet[opponent] + bigBlind; 
			pokerChips[player] = initialPokerChips[player] - bet[player];
			pokerChips[pot] = bet[player]+bet[opponent];
			
			flag = 1;
		
		}
		// If blind is bigger then is poker chips
		else if((bigBlind*2) > pokerChips[player] && flag ==0)
		{
			allIn(player);
			return;
		}
		
		// Opponent has to talk
		confirmation[player] = 1;
		confirmation[opponent] = 0;
		
		// It is not initial bet anymore
		initialBet=false;
		
		player = (player == 0) ? 1 : 0;
		if(pokerChips[player] > 0) 
			menuFold_Call(player);	
		else
			allIn(player);
	}
	
	// Method to make a bet of a specific value
	public void betValor(int player)
	{
		int opponent = (player == 0) ? 1 : 0;
		
		// Ask user to input bet value
		if(pokerChips[player] < (2*bigBlind))
			betOption[1] = (int)pokerChips[player];	
		else
		{
			if((gameMode == 1) || (gameMode == 2 && player == 0) || (gameMode == 3 && player == 0) 
					|| (gameMode == 4 && player == 0) || (gameMode == 5 && player == 0))
			{
				System.out.println("Choose a value between " + (2*bigBlind) + " e " +  pokerChips[player]);
				betOption[1] = input.nextInt();
				while(betOption[1] < (2*bigBlind) || betOption[1] >= pokerChips[player])
				{
					System.out.println("(Invalid value!) "
							+ "Choose a value between " + (2*bigBlind) + "and " +  pokerChips[player]);
					betOption[1] = input.nextInt();
				}
				
			}
		}
		
		float tempbet = bet[player];
		
		bet[player] = betOption[1]; 

		System.out.println("Player " + (player+1) + " makes a bet of " 
				+ betOption[1]);
		
		pokerChips[player] -= bet[player];
		bet[player] += tempbet;
		pokerChips[pot] = bet[player]+ bet[opponent];
		
		// Opponent has to talk
		confirmation[player] = 1;
		confirmation[opponent] = 0;
		
		// It is not an initial bet anymore
		initialBet=false;
		
		player = (player == 0) ? 1 : 0;
		if(pokerChips[player] <=  betOption[1])
			menuFold_Allin(player);
		else
			menuFold_Call(player);
	}
	
	// Method to bet all the poker chips
	public void allIn(int player)
	{

		int opponent = (player == 0) ? 1 : 0;
		float tempbet = bet[player];
		
		// Bet all the chips
		bet[player] = pokerChips[player] + tempbet;
	
		System.out.println("Player " + (player+1) + " makes all in with " 
				+ bet[player]);

		pokerChips[player] -= (bet[player]-tempbet);
		pokerChips[pot] = (bet[player]+ bet[opponent]);
		
		// Opponent has to tal
		confirmation[player] = 1;
		confirmation[opponent] = 0;
		
		initialBet=false;
		player = (player == 0) ? 1 : 0;
		if(pokerChips[player] > 0)
			menuFold_Allin(player);
	}
	
	// Retorna a opcão consoante o modo de jogo
	public int[] gameMode(int player, int gameMode, int menu)
	{
		betOption[0] = input.nextInt();
		return betOption;
	}
	
}
