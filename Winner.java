import java.util.ArrayList;

/*
 * Description: This class calculates which player has the winner hand
 * Name: Filipe Andre de Matos Bicho
 * Last update: 28/10/2017
 */
public class Winner {

	/* Method to calculate which player wins
	* if return 1, player 1 wins, 2 player 2 winds, 0 draw
	*/
	public int calculateWinner(ArrayList<Cards> player1, ArrayList<Cards> player2,int[] result)
	{
		// Player 1 wins
		if(result[0] > result[1])
			return 1;
		// Player 2 wins
		else if(result[0] < result[1])
			return 2;
		else
		{
			//if players have the same hand ranking
			switch(result[0])
			{
				case 9:
					return straightFlush(player1, player2);
				case 8:
					return fourOfAKind(player1, player2);
				case 7:
					return fullHouse(player1, player2);
				case 6:
					return flush(player1, player2);
				case 5: 
					return straight(player1, player2);
				case 4: 
					return threeOfAKind(player1, player2);
				case 3: 
					return twoPair(player1, player2);
				case 2:
					return onePair(player1, player2);
				case 1:
					return highCard(player1, player2);
				
			}
			
			return 0;
		}
	}

	// Method to find winner in straight flush draw
	private int straightFlush(ArrayList<Cards> player1, ArrayList<Cards> player2) {

		if(player1.get(0).getRank() > player2.get(0).getRank())
			return 1;
		else if(player1.get(0).getRank() < player2.get(0).getRank())
			return 2;
		else
			return 0;
	}
	
	// Method to find winner in straight flush draw
	private int fourOfAKind(ArrayList<Cards> player1, ArrayList<Cards> player2) {
		
		// If both players have four of a kind of Aces
		if(Integer.valueOf(player1.get(0).getRank()).equals(0) &&
				Integer.valueOf(player2.get(0).getRank()).equals(0))
		{
			if(player1.get(4).getRank() > player2.get(4).getRank())
				return 1;
			else if(player1.get(4).getRank() < player2.get(4).getRank())
				return 2;
			else
				return 0;
		}
		// If player1 has a four of a kind of Aces
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has a four of a kind of Aces
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		else
		{
			if(player1.get(0).getRank() > player2.get(0).getRank())
				return 1;
			else if(player1.get(0).getRank() < player2.get(0).getRank())
				return 2;
			// If both have the same four of a kind
			else
			{
				// If both players have same kicker
				if(Integer.valueOf(player1.get(4).getRank()).equals(Integer.valueOf(player2.get(4).getRank())))
					return 0;
				// If player1 has kicker Ace
				else if(Integer.valueOf(player1.get(4).getRank()).equals(0))
					return 1;
				// If player2 has kicker Ace
				else if(Integer.valueOf(player2.get(4).getRank()).equals(0))
					return 2;
				else
				{
					if(player1.get(4).getRank() > player2.get(4).getRank())
						return 1;
					else if(player1.get(4).getRank() < player2.get(4).getRank())
						return 2;
					else
						return 0;
				}
			}
		}
		
	}
	
	// Method to find winner in a full house
	private int fullHouse(ArrayList<Cards> player1, ArrayList<Cards> player2) {
		
		// If both have the same three of a kind
		if(Integer.valueOf(player1.get(0).getRank()).equals(player2.get(0).getRank()))
		{
			// If both have the same par
			if(Integer.valueOf(player1.get(3).getRank()).equals(player2.get(3).getRank()))
				return 0;
			// If player1 has par of Aces
			else if(Integer.valueOf(player1.get(3).getRank()).equals(0))
				return 1;
			// If player2 has par of Aces
			else if(Integer.valueOf(player2.get(3).getRank()).equals(0))
				return 2;
			else
			{
				if(player1.get(3).getRank() > player2.get(3).getRank())
					return 1;
				else if(player1.get(3).getRank() < player2.get(3).getRank())
					return 2;
				else
					return 0;
			}
		}
		// If player1 has three of a kind of Aces
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has three of a kind of Aces
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		// If player 1 has a bigger three of a kind
		else if(player1.get(0).getRank() > player2.get(0).getRank())
			return 1;
		// If player 2 has a bigger three of a kind
		else if(player1.get(0).getRank() < player2.get(0).getRank())
			return 2;
		// Draw
		else
			return 0;
	}

	// Method to find winner in a flush
	private int flush(ArrayList<Cards> player1, ArrayList<Cards> player2) {
		
		// Check if both players have a flush with an Ace
		if(Integer.valueOf(player1.get(0).getRank()).equals(0) &&
				Integer.valueOf(player2.get(0).getRank()).equals(0))
		{
			// Check which player have highest ranking starting in the last position
			for(int i = player1.size()-1; i > 0; i--)
			{
				if(player1.get(i).getRank() > player2.get(i).getRank())
					return 1;
				else if(player1.get(i).getRank() < player2.get(i).getRank())
					return 2;
			}
			return 0;
		}
		// If player1 has a flush with an Ace
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has a flush with an Ace
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		else
		{
			// Check which player have highest ranking starting in the last position
			for(int i = player1.size()-1; i >= 0; i--)
			{
				if(player1.get(i).getRank() > player2.get(i).getRank())
					return 1;
				else if(player1.get(i).getRank() < player2.get(i).getRank())
					return 2;
			}
			return 0;
		}
	}
	
	// Method to find winner in a straight
	private int straight(ArrayList<Cards> player1, ArrayList<Cards> player2) {

		// Check if both players have a Straight with an Ace
		if(Integer.valueOf(player1.get(0).getRank()).equals(0) &&
				Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 0;
		// If player1 has a straight with an Ace
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has a straight with an Ace
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		// If player1 has a higher straight
		else if(player1.get(0).getRank() > player2.get(0).getRank())
			return 1;
		// If player2 has a higher straight
		else if(player1.get(0).getRank() < player2.get(0).getRank())
			return 2;
		else
			return 0;
		
	}
	
	// Method to find winner in a three of a kind
	private int threeOfAKind(ArrayList<Cards> player1, ArrayList<Cards> player2) 
	{
		// If both have the same Three Of a Kind
		if(Integer.valueOf(player1.get(0).getRank()).equals(player2.get(0).getRank()))
		{
			// If both players have kicker with Aces
			if(Integer.valueOf(player1.get(3).getRank()).equals(0) &&
					Integer.valueOf(player2.get(3).getRank()).equals(0))
			{
				// Find biggest Kicker
				if(player1.get(4).getRank() > player2.get(4).getRank())
					return 1;
				else if(player1.get(4).getRank() < player2.get(4).getRank())
					return 2;
				else 
					return 0;
			}
			// If player1 has kicker Ace
			else if(Integer.valueOf(player1.get(3).getRank()).equals(0))
				return 1;
			// If player2 has kicker Ace
			else if(Integer.valueOf(player2.get(3).getRank()).equals(0))
				return 2;
			else
			{
				// Find biggest Kicker
				for(int i = 3; i < player1.size(); i++)
				{
					if(player1.get(i).getRank() > player2.get(i).getRank())
						return 1;
					else if(player1.get(i).getRank() < player2.get(i).getRank())
						return 2;
				}
				return 0;
			}
		}
		// If player1 has a three with an Ace
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has a three with an Ace
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		// If player 1 has a bigger three of a kind
		else if(player1.get(0).getRank() > player2.get(0).getRank())
			return 1;
		// If player 2 has a bigger three of a kind
		else 
			return 2;
		
	}
	
	// Method to find winner in two pair
	private int twoPair(ArrayList<Cards> player1, ArrayList<Cards> player2) 
	{
		// If both players have a pair of Aces
		if(Integer.valueOf(player1.get(0).getRank()).equals(0) &&
				Integer.valueOf(player2.get(0).getRank()).equals(0))
		{
			//If player 1 has biggest second par
			if(player1.get(2).getRank() > player2.get(2).getRank())
				return 1;
			//If player 2 has biggest second par
			else if(player1.get(2).getRank() < player2.get(2).getRank())
				return 2;
			else
			{
				//If player 1 has biggest kicker
				if(player1.get(4).getRank() > player2.get(4).getRank())
					return 1;
				//If player 2 has biggest kicker
				else if(player1.get(4).getRank() < player2.get(4).getRank())
					return 2;
				else
					return 0;
			}
		}
		// If player1 has par of Ace
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has par of Ace
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		//If player 1 has biggest first par
		else if(player1.get(2).getRank() > player2.get(2).getRank())
			return 1;
		//If player 2 has biggest first par
		else if(player1.get(2).getRank() < player2.get(2).getRank())
			return 2;
		else
		{
			//If player 1 has biggest second par
			if(player1.get(0).getRank() > player2.get(0).getRank())
				return 1;
			//If player 2 has biggest second par
			else if(player1.get(0).getRank() < player2.get(0).getRank())
				return 2;
			else
			{
				//If player 1 has biggest kicker
				if(player1.get(4).getRank() > player2.get(4).getRank())
					return 1;
				//If player 2 has biggest kicker
				else if(player1.get(4).getRank() < player2.get(4).getRank())
					return 2;
				else
					return 0;
			}
		}
	}

	// Method to find winner in a pair
	private int onePair(ArrayList<Cards> player1, ArrayList<Cards> player2) {
		
		// If both have the same pair
		if(Integer.valueOf(player1.get(0).getRank()).equals(player2.get(0).getRank()))
		{
			// If both players have kicker with Aces
			if(Integer.valueOf(player1.get(2).getRank()).equals(0) &&
					Integer.valueOf(player2.get(2).getRank()).equals(0))
			{
				// Find biggest Kicker
				for(int i = 3; i < player1.size(); i++)
				{
					if(player1.get(i).getRank() > player2.get(i).getRank())
						return 1;
					else if(player1.get(i).getRank() < player2.get(i).getRank())
						return 2;
				}
				return 0;
			}
			// If player1 has kicker Ace
			else if(Integer.valueOf(player1.get(2).getRank()).equals(0))
				return 1;
			// If player2 has kicker Ace
			else if(Integer.valueOf(player2.get(2).getRank()).equals(0))
				return 2;
			else
			{
				// Find biggest Kicker
				for(int i = 2; i < player1.size(); i++)
				{
					if(player1.get(i).getRank() > player2.get(i).getRank())
						return 1;
					else if(player1.get(i).getRank() < player2.get(i).getRank())
						return 2;
				}
				return 0;
			}
		}
		// If player1 has a par with an Ace
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has a par with an Ace
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		// If player 1 has a bigger pair
		else if(player1.get(0).getRank() > player2.get(0).getRank())
			return 1;
		// If player 2 has a bigger pair
		else 
			return 2;
	}

	// Method to find winner in high card
	private int highCard(ArrayList<Cards> player1, ArrayList<Cards> player2) {
		
		// If both players have Aces
		if(Integer.valueOf(player1.get(0).getRank()).equals(0) &&
				Integer.valueOf(player2.get(0).getRank()).equals(0))
		{
			// Find biggest Kicker
			for(int i = 1; i < player1.size(); i++)
			{
				if(player1.get(i).getRank() > player2.get(i).getRank())
					return 1;
				else if(player1.get(i).getRank() < player2.get(i).getRank())
					return 2;
			}
			return 0;
		}
		// If player1 has an Ace
		else if(Integer.valueOf(player1.get(0).getRank()).equals(0))
			return 1;
		// If player2 has an Ace
		else if(Integer.valueOf(player2.get(0).getRank()).equals(0))
			return 2;
		else
		{
			// Find biggest Kicker
			for(int i = 0; i < player1.size(); i++)
			{
				if(player1.get(i).getRank() > player2.get(i).getRank())
					return 1;
				else if(player1.get(i).getRank() < player2.get(i).getRank())
					return 2;
			}
			return 0;
		}
		
	}
}
