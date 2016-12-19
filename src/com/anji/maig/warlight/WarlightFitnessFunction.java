package com.anji.maig.warlight;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;


import org.jgap.BulkFitnessFunction;
import org.jgap.Chromosome;

import com.anji.warlight.conquest.bot.Bot;
import com.anji.warlight.conquest.bot.BotState;
import com.anji.warlight.conquest.bot.NEATWarlightBot;
import com.anji.integration.ActivatorTranscriber;
import com.anji.util.Configurable;
import com.anji.util.Properties;

public class WarlightFitnessFunction implements BulkFitnessFunction, Configurable {

	private final static String TRANSCRIBER_CLASS_KEY = "warlightai.transcriber";
	private ActivatorTranscriber activatorFactory = new ActivatorTranscriber();
	private NEATWarlightAI bestBot;
	
	//ownedRegion (42)
	
	@Override
	public void init(Properties props) throws Exception {
		//activatorFactory.init(props);
		activatorFactory = (ActivatorTranscriber) props.newObjectProperty( TRANSCRIBER_CLASS_KEY );

		bestBot = null;
		// TODO Auto-generated method stub

	}

	@Override
	public void evaluate(List subjects) {
		
		NEATWarlightAI[] bots = new NEATWarlightAI[subjects.size()];
		double bestScore = 0;
		try{
			PrintWriter writer = new PrintWriter("generation.txt", "UTF-8");
			for(int i = 0; i < subjects.size(); i++){
				try{
				NEATWarlightAI bot = new NEATWarlightAI(activatorFactory.newActivator((Chromosome)subjects.get(i)));
				bot.runGame(false);
				
				bots[i] = bot;
				writer.println(i + ": Score " + bot.score);			
				((Chromosome)subjects.get(i)).setFitnessValue(bot.score.intValue());
				
				} catch(Exception e) {
					System.out.println("!!!!!!!!!!!!!! " + subjects.get(i).toString() + " | " + e.getMessage());
					
					System.exit(0);
				}
			}
			Arrays.sort(bots);
			bestBot = bots[bots.length-1];
			writer.println();	
			writer.println("Best Score " + bestBot.score);
			writer.close();
		} catch (IOException e){
			System.out.println("IOException | " + e.getMessage());
		}
		
		//TODO?????
	}

	@Override
	public int getMaxFitnessValue() {
		return 42+24; // amount of region + max continentBonus
		//return 42; // amount of regions 
	/*	if(bestBot == null){
			return 0;
		} else {
			return bestBot.Score.intValue();
		} */
	}
}
