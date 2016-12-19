package com.anji.maig.warlight;

import java.io.File;
import java.io.IOException;

import org.jgap.Chromosome;
import org.jgap.InvalidConfigurationException;

import com.anji.integration.Activator;
import com.anji.integration.ActivatorTranscriber;
import com.anji.integration.TranscriberException;
import com.anji.neat.NeatConfiguration;
import com.anji.persistence.FilePersistence;
import com.anji.persistence.Persistence;
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
	private final static String CHROMOSOME_ID_KEY = "warlightai.chromosome.id";
	

	public Double score;
	
	NEATWarlightAI(Activator activator){
		a = activator;
		bot = new NEATWarlightBot(activator);
		score = 0.0;
	}
	
	NEATWarlightAI(){
		try {
			properties = new Properties("warlightai.properties");
			NeatConfiguration configuration = new NeatConfiguration(properties);

			Persistence persistence = new FilePersistence();
			persistence.init(properties);
			String chromosome_id = properties.getProperty(CHROMOSOME_ID_KEY);
			Chromosome chromosome = persistence.loadChromosome(chromosome_id, configuration);

			ActivatorTranscriber activator_transcriber = (ActivatorTranscriber)properties.newObjectProperty(TRANSCRIBER_CLASS_KEY);
			a = activator_transcriber.newActivator(chromosome);
			
			bot = new NEATWarlightBot(a);
			score = 0.0;
		} 
		catch (InvalidConfigurationException | IOException | TranscriberException e) {
			e.printStackTrace();
		}
	}
	
	public void init(){
		//no-op
	}

	public void runGame(boolean visible){
		// Chromosome XML
		System.out.println("Run simulation");
		Config config = new Config();
		
		config.engine.maxGameRounds = 100; //Integer.parseInt(args[0]);
		config.engine.botCommandTimeoutMillis = 2000; //Long.parseLong(args[1]);
		
		config.bot1Init = "internal:com.anji.warlight.conquest.bot.NEATWarlightBot"; //args[2];
		config.bot2Init = "internal:com.anji.warlight.conquest.bot.BotStarter";
		
		config.visualize = visible;// Boolean.parseBoolean(args[4]S);
		
//			if (args.length == 6) {
		//config.replayLog = new File("replay.log");
//		}
		
		RunGame run = new RunGame(config);
		
		GameResult result = run.go(bot, new BotStarter()); //Bot1 & Boit2
	
		if(result == null){
			score = 0.0;
		} else {
			System.out.println(result.getWinnerId());
			score = (double)result.player1Regions + (double)bot.getContinentScore();
			if(result.getWinnerId() == "Bot1"){
				score = 66.0;
			}
		}
		//BotState state = bot.run();
		//Score = (double)ScoreState(state);
		System.out.println("    Score: " + score);
	
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
		return score.compareTo(o.score);
	}	
	
}
