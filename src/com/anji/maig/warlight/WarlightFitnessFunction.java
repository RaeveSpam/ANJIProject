package com.anji.maig.warlight;

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

	private ActivatorTranscriber activatorFactory = new ActivatorTranscriber();
	
	private NEATWarlightAI bestBot;
	
	//ownedRegion (42)
	
	@Override
	public void init(Properties props) throws Exception {
		activatorFactory.init(props);
		bestBot = null;
		// TODO Auto-generated method stub

	}

	@Override
	public void evaluate(List subjects) {
		
		NEATWarlightAI[] bots = new NEATWarlightAI[subjects.size()];
		
		for(int i = 0; i < subjects.size(); i++){
			try{
			NEATWarlightAI bot = new NEATWarlightAI(activatorFactory.newActivator((Chromosome)subjects.get(i)));
			bot.runGame();
			bots[i] = bot;
			((Chromosome)subjects.get(i)).setFitnessValue(bot.Score.intValue());
			
			} catch(Exception e) {
				System.out.println(subjects.get(i).toString() + " | " + e.getMessage());
			}
		}	
		
		Arrays.sort(bots);
		bestBot = bots[bots.length-1];
		//TODO?????
	}

	@Override
	public int getMaxFitnessValue() {
		return bestBot.Score.intValue();
	}
}
