package com.anji.maig.warlight;

import com.anji.integration.Activator;
import com.anji.warlight.conquest.bot.*;
import com.anji.warlight.conquest.game.RegionData;

public class NEATWarlightAI implements Comparable<NEATWarlightAI> {

	private NEATWarlightBot bot;
	private Activator a;
	
	public Double Score;
	
	NEATWarlightAI(Activator activator){
		a = activator;
		bot = new NEATWarlightBot(activator);
		Score = 0.0;
	}
	
	public void runGame(){
		BotState state = bot.run();
		Score = (double)ScoreState(state);
	}

	private int ScoreState(BotState state){
		int result = 0;
		for(RegionData r : state.getFullMap().getRegions()){
			if(r.ownedByPlayer(state.getMyPlayerName())){
				result++;
			}
		}
		return result;
	}
	
	public Activator getActivator(){	//Maybe just the activator name??
		return a;
	}
	
	@Override
	public int compareTo(NEATWarlightAI o) {
		return Score.compareTo(o.Score);
	}
	
	
	
	
}
