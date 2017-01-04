package com.anji.maig.warlight;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.jgap.Chromosome;

public class NEATRunner {

	static boolean run_with_visuals = true;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		NEATWarlightAI bot = new NEATWarlightAI();

		//bot.runGame(run_with_visuals);
		runMultiple(100);
	}
	
	
	
	static void runMultiple(int match_count){
		
		NEATWarlightAI bot = new NEATWarlightAI();
		try{
			PrintWriter writer = new PrintWriter("stats.csv", "UTF-8");
			writer.println("score; winner; bot1 regions; bot2 regions; bot1 armies; bot2armies; #bot1wins; #bot2wins; #ties");
			
			int totalBot1Regions = 0;
			int totalBot1Wins = 0;
			int totalBot2Wins = 0;
			int totalTies = 0;
			int totalBot2Regions = 0;
			int totalBot1Armies = 0;
			int totalBot2Armies = 0;
			
			for(int i = 0; i < match_count; i++){
				try{
					bot.runGame(false);
					int win = 2;
					if (bot.result.player1Regions > bot.result.player2Regions){
						win = 1;
					}
					else if (bot.result.player1Regions == bot.result.player2Regions){
						win = 0;
					}
					writer.println(bot.score +"; "+win + "; "+ bot.result.player1Regions + "; "+bot.result.player2Regions + "; "+bot.result.player1Armies+ "; "+bot.result.player2Armies);
				
					totalBot1Regions += bot.result.player1Regions;
					totalBot2Regions += bot.result.player2Regions;
					totalBot1Armies += bot.result.player1Armies;
					totalBot2Armies += bot.result.player2Armies;
					
					if (win == 0)
						totalTies++;
					else if (win == 1)
						totalBot1Wins++;
					else if (win == 2)
						totalBot2Wins++;
						
				} catch(Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		
			writer.println();
			writer.print("; ; "+ totalBot1Regions + "; "+ totalBot2Regions+ "; " + totalBot1Armies + "; "+ totalBot2Armies+ "; " + totalBot1Wins + "; " + totalBot2Wins + "; "+ totalTies);
			writer.close();
		} catch (IOException e){
			System.out.println("IOException | " + e.getMessage());
		}
		
	}

}
