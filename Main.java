import java.util.ArrayList;

public class Main {

	public static void main(String[] args) 
	{	
		ArrayList<Cards> player1 = new ArrayList<>();
		ArrayList<Cards> player2 = new ArrayList<>();
		ArrayList<Cards> table= new ArrayList<>();
		
		Dealer dealer = new Dealer();
	
		
		for(int i = 0; i < 100; i++)
		{
			Evaluate evaluate = new Evaluate();
			Deck deck = new Deck();
			dealer.giveCards(deck, player1, player2);
			dealer.giveFlop(deck, table);
			System.out.println("-----------------------------------------------------------");
			System.out.println("Player: " + player1);
			System.out.println("Table: " + table);
			evaluate.evaluateHand(player1, table);
			System.out.println(evaluate.getHand() + " - " + evaluate.getResult());
			evaluate.reset();
			
			System.out.println("--------------------------------");
			dealer.giveOneCard(deck, table);
			System.out.println("Player: " + player1);
			System.out.println("Table: " + table);
			evaluate.evaluateHand(player1, table);
			System.out.println(evaluate.getHand() + " - " + evaluate.getResult());
			evaluate.reset();
			System.out.println("---------------------------------");
			dealer.giveOneCard(deck, table);
			System.out.println("Player: " + player1);
			System.out.println("Table: " + table);
			evaluate.evaluateHand(player1, table);
			System.out.println(evaluate.getHand() + " - " + evaluate.getResult());
			evaluate.reset();
			System.out.println("-----------------------------------------------------------");
			
			
			
			
			

			player1.clear();
			player2.clear();
			table.clear();
			System.out.println();
		}
	}

}
