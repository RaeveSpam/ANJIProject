package com.anji.maig.warlight;

import java.util.List;


import org.jgap.BulkFitnessFunction;

import com.anji.warlight.conquest.bot.Bot;
import com.anji.warlight.conquest.bot.BotState;
import com.anji.warlight.conquest.bot.NEATWarlightBot;
import com.anji.util.Configurable;
import com.anji.util.Properties;

public class WarlightFitnessFunction implements BulkFitnessFunction, Configurable {

	//ownedRegion/42
	
	@Override
	public void init(Properties props) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void evaluate(List subjects) {
		// TODO Auto-generated method stub
		
		BotState[] states = new BotState[subjects.size()];
		
		// for int i ... i < chromosome 
			// run game with bot (chromosome[i]), states[i] = RunGame(...);
		
		// sort states
		// get best state (states[0])
	}

	@Override
	public int getMaxFitnessValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	public BotState RunGame(Bot bot, int iterations){
		
		return null;
	}

}
