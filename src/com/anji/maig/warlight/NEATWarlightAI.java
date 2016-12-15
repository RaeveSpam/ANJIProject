package com.anji.maig.warlight;

import java.io.File;
import java.io.IOException;

import com.anji.integration.Activator;
import com.anji.util.Properties;
import com.anji.warlight.conquest.bot.*;
import com.anji.warlight.conquest.engine.RunGame;
import com.anji.warlight.conquest.engine.RunGame.Config;
import com.anji.warlight.conquest.engine.RunGame.GameResult;
import com.anji.warlight.conquest.game.RegionData;

public class NEATWarlightAI implements Comparable<NEATWarlightAI> {

	private NEATWarlightBot bot;
	private Activator a;
	Properties properties;
	private final static String TRANSCRIBER_CLASS_KEY = "warlightai.transcriber";

	public Double Score;
	
	NEATWarlightAI(Activator activator){
		a = activator;
		/*try {
			properties = new Properties("warlightai.properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		bot = new NEATWarlightBot(activator);
		Score = 0.0;
	}
	
	public void init(){
		
		
	}
	
	public void runGame(){
		// Chromosome XML
		System.out.println("Run simulation");
		Config config = new Config();
		
		config.engine.maxGameRounds = 100; //Integer.parseInt(args[0]);
		config.engine.botCommandTimeoutMillis = 5000; //Long.parseLong(args[1]);
		
		config.bot1Init = "internal:com.anji.warlight.conquest.bot.NEATWarlightBot"; //args[2];
		config.bot2Init = "internal:com.anji.warlight.conquest.bot.BotStarter";
		
		config.visualize = false;// Boolean.parseBoolean(args[4]);
		
//			if (args.length == 6) {
			config.replayLog = new File("replay.log");
//		}
			
		RunGame run = new RunGame(config);
		GameResult result = run.go(bot, new BotStarter()); //Bot1 & Boit2
		Score = (double)result.player1Regions;
		
		//BotState state = bot.run();
		//Score = (double)ScoreState(state);
		System.out.println("    Score: " + Score);
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
