package com.anji.maig.warlight;

import java.io.IOException;

import com.anji.integration.Activator;
import com.anji.util.Properties;
import com.anji.warlight.conquest.bot.*;
import com.anji.warlight.conquest.game.RegionData;

public class NEATWarlightAI implements Comparable<NEATWarlightAI> {

	private NEATWarlightBot bot;
	private Activator a;
	Properties properties;
	private final static String TRANSCRIBER_CLASS_KEY = "warlightai.transcriber";

	public Double Score;
	
	NEATWarlightAI(Activator activator){
		a = activator;
		try {
			properties = new Properties("warlightai.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bot = new NEATWarlightBot(activator);
		Score = 0.0;
	}
	
	public void init(){
		
		
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
