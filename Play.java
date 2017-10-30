/* Description: The purpose of this class is to simulate a poker game
 * User VS User
 * User VS Computer 
 * Computer VS Computer
 * Name: Filipe Andre de Matos Bicho
 * Last Update: 30/10/2017
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class Play {

	private Dealer dealer = new Dealer();
	private Evaluate evaluate = new Evaluate();
	private Winner calcWinner = new Winner();
	private Deck deck;
	private Odds odds = new Odds();
	private Bets bet = new Bets();
	
	private HashMap<Integer, ArrayList<Cards>> cards = new HashMap<>();
	private ArrayList<Cards> player1 = new ArrayList<>();
	private ArrayList<Cards> player2 = new ArrayList<>();
	private ArrayList<Cards> table = new ArrayList<>();
	private ArrayList<Cards> hand1 = new ArrayList<>();
	private ArrayList<Cards> hand2= new ArrayList<>();
	private ArrayList<Cards> tempTable= new ArrayList<>();
	
	int[] result = new int[2];
	float[] playerOdds = new float[3];
	float[] potentialHand = new float[11];
	String[] resultStr = new String[2];
	
	// Method that simulates a User vs User game
	public void UserVsUser()
	{
		// Select randomly a dealer
		Random dealerRandom = new Random();
		int posDealer = dealerRandom.nextInt((1-0)+1)+0;
		
		/* 0 - player 1 poker chips
		 * 1 - player 2 poker chips
		 * 2 - pot poker chips
		 * 3 - if game continues or not
		 */
		float[] pokerChips = new float[4];
		
		// Both players start with 1500 chips
		pokerChips[0] = 1500;
		pokerChips[1] = 1500;
		
		// Game continue while game is true
		Boolean game = true;
		int i=0;
		
		// Game
		while(game)
		{
			player1.clear();
			player2.clear();
			hand1.clear();
			hand2.clear();
			table.clear();
			
			System.out.println("\n ******************************** Game nº " + (i+1) + "*********************************** \n");
			 
			// Shuffle the deck
			deck = new Deck();
			
			// Give the cards to the players
			dealer.giveCards(deck, player1, player2);
			
			// Store players cards
			cards.put(0, player1);
			cards.put(1, player2);

			showPlayersChips(pokerChips);
			
			// Pre Flop bets
			pokerChips = bet.betsPreFlop(pokerChips, posDealer, playerOdds, 1, cards);
			
			// Check if the game continues
			if(pokerChips[3] == 1)
			{
				// Change position if game stops
				posDealer = (posDealer == 0) ? 1 : 0;
				pokerChips[2] = 0; // pot reset
				continue;
			}
			
			// Get flop cards
			dealer.giveFlop(deck, table);
			System.out.println("\n Flop: " + table);
			
			// If one of the players don't have chips the game is finish
			if(pokerChips[0] == 0 || pokerChips[1] == 0)
			{
				tempTable.addAll(table);	
				playerOdds = odds.oddsPlayerVSPlayerFlop(player1, player2, tempTable);
				tempTable.clear();
				showPlayersOdds(pokerChips);
			}
			else
				showPlayersChips(pokerChips);
			
			// If both players still have poker chips 
			if(pokerChips[0] > 0 && pokerChips[1] > 1)
				pokerChips = bet.bets(pokerChips, posDealer, playerOdds, 1, 1, cards);
			
			// Check if the game continues
			if(pokerChips[3] == 1)
			{
				// Change position if game stops
				posDealer = (posDealer == 0) ? 1 : 0;
				pokerChips[2] = 0; // pot reset
				continue;
			}
			
			// Get turn
			dealer.giveOneCard(deck, table);
			System.out.println("\n Turn: " + table);
			
			// If one of the players don't have chips the game is finish
			if(pokerChips[0] == 0 || pokerChips[1] == 0)
			{
				tempTable.addAll(table);	
				playerOdds = odds.oddsPlayerVSPlayerTurn(player1, player2, tempTable);
				tempTable.clear();
				showPlayersOdds(pokerChips);
			}
			else
				showPlayersChips(pokerChips);
			
			// If both players still have poker chips 
			if(pokerChips[0] > 0 && pokerChips[1] > 1)
				pokerChips = bet.bets(pokerChips, posDealer, playerOdds, 1, 2, cards);
			
			// Check if the game continues
			if(pokerChips[3] == 1)
			{
				// Change position if game stops
				posDealer = (posDealer == 0) ? 1 : 0;
				pokerChips[2] = 0; // pot reset
				continue;
			}
			
			// Get River
			dealer.giveOneCard(deck, table);
			System.out.println("\n River: " + table);
			
			// If both players still have poker chips 
			if(pokerChips[0] > 0 && pokerChips[1] > 1)
				pokerChips = bet.bets(pokerChips, posDealer, playerOdds, 1, 3, cards);
			
			// Check if the game continues
			if(pokerChips[3] == 1)
			{
				// Change position if game stops
				posDealer = (posDealer == 0) ? 1 : 0;
				pokerChips[2] = 0; // pot reset
				continue;
			}
			
			showPlayersChips(pokerChips);
				
			result[0] = evaluate.evaluateHand(player1, table);
			hand1.addAll(evaluate.getHand());
			resultStr[0] = evaluate.getResult();
			evaluate.reset();
			
			result[1] = evaluate.evaluateHand(player2, table);
			hand2.addAll(evaluate.getHand());
			resultStr[1] = evaluate.getResult();
			evaluate.reset();
			
			if(Integer.valueOf(calcWinner.calculateWinner(hand1, hand2, result)).equals(1))
			{
				// Player 1 wins the pot
				System.out.println("Player 1 wins");
				pokerChips[0] += pokerChips[2];
			}
			else if (Integer.valueOf(calcWinner.calculateWinner(hand1, hand2, result)).equals(2))
			{
				// Player 2 wins the pot
				System.out.println("Player 2 wins");
				pokerChips[1] += pokerChips[2];
			}
			else
			{
				// If draw splits the pot
				pokerChips[0] += pokerChips[2] / 2;
				pokerChips[1] += pokerChips[2] / 2;
				System.out.println("Draw");
			}
			
			// Change dealer chip
			posDealer = (posDealer == 0) ? 1 : 0;
			pokerChips[2] = 0; //reset pot
			if(pokerChips[0] <= 0)
			{
				System.out.println(" !!!!!!!!!!!!!!!! Player 2 won the game !!!!!!!!!!");
				game = false;
			}
			else if(pokerChips[1] <= 0)
			{
				System.out.println(" !!!!!!!!!!!!!!!! Player 1 won the game !!!!!!!!!!");
				game = false;
			}			
			
		}
	}
	
	// Method to show both player poker chips
	private void showPlayersChips(float[] pokerChips)
	{
		System.out.println("---------------------------------------");
		System.out.println(" ( " + pokerChips[0] + " )" + "Player 1: " + player1);
		System.out.println(" ( " + pokerChips[1] + " )" + "Player 2: " + player2);
		System.out.println("Pot: " + pokerChips[2]);
		System.out.println("---------------------------------------");
	}
	
	// Method to show both player poker chips
	private void showPlayersOdds(float[] pokerChips)
	{
		System.out.println("---------------------------------------");
		System.out.println(" ( " + pokerChips[0] + " )" + "Player 1: " + player1 + " ( " + playerOdds[1] + " % )");
		System.out.println(" ( " + pokerChips[1] + " )" + "Player 2: " + player2 + " ( " + playerOdds[2] + " % )");
		System.out.println("Pot: " + pokerChips[2]);
		System.out.println("---------------------------------------");
	}
	

	
	
	
	
}