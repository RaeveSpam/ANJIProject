// Copyright 2014 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.anji.warlight.conquest.bot;

import java.util.ArrayList;
import java.util.LinkedList;

import org.jgap.Chromosome;
import org.jgap.Genotype;

import com.anji.integration.Activator;
import com.anji.warlight.conquest.game.GameMap;
import com.anji.warlight.conquest.game.RegionData;
import com.anji.warlight.conquest.game.move.AttackTransferMove;
import com.anji.warlight.conquest.game.move.Move;
import com.anji.warlight.conquest.game.move.PlaceArmiesMove;
import com.anji.warlight.conquest.view.GUI;


public class NEATWarlightBot implements Bot 
{	
	public float Score;
	
	private Activator neat;
	
	private BotState finalState;
	
	public NEATWarlightBot(Activator ac){
		neat = ac;
		Score = 0.0f;
	}
	
	public NEATWarlightBot(){
		super();
		Score = 0.0f;
	}
	
	/**
	 * A method used at the start of the game to decide which player start with what Regions. 6 Regions are required to be returned.
	 * This example randomly picks 6 regions from the pickable starting Regions given by the engine.
	 * @return : a list of m (m=6) Regions starting with the most preferred Region and ending with the least preferred Region to start with 
	 */
	@Override
	public ArrayList<RegionData> getPreferredStartingRegions(BotState state, Long timeOut)
	{
		GameMap plannedMap = state.getFullMap().getMapCopy();
		//Score = neat.getOutput(state.getFullMap.getMapCopy);
		int m = 6;
		ArrayList<RegionData> preferredStartingRegions = new ArrayList<RegionData>();
		for(int i=0; i<m; i++)
		{
			
			/*for(int j = 0; j < plannedMap.regions.size(); j++){
				//TODO EVERYTHING
				
			}
			*/
			double rand = Math.random();
			int r = (int) (rand*state.getPickableStartingRegions().size());
			int regionId = state.getPickableStartingRegions().get(r).getId();
			RegionData region = state.getFullMap().getRegion(regionId);

			if(!preferredStartingRegions.contains(region))
				preferredStartingRegions.add(region);
			else
				i--;
		}
		
		return preferredStartingRegions;
	}
	
	public BotState getFinalState(){
		return null;
	}
	
	/**
	 * This method is called for at first part of each round. This example puts two armies on random regions
	 * until he has no more armies left to place.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	@Override
	public ArrayList<PlaceArmiesMove> getPlaceArmiesMoves(BotState state, Long timeOut) 
	{
		finalState = state;
		//Score = neat.getOutput(state.getFullMap.getMapCopy);
		ArrayList<PlaceArmiesMove> placeArmiesMoves = new ArrayList<PlaceArmiesMove>();
		String myName = state.getMyPlayerName();
		int armies = 1;
		int armiesLeft = state.getStartingArmies();
		LinkedList<RegionData> visibleRegions = state.getMap().getRegions();
		
		GameMap plannedMap = state.getFullMap().getMapCopy();
		double plannedScore = 0.0; 
		plannedScore = neat.next(getDoubleArrayMap(plannedMap, state.getMap(), myName))[0];
		//neat.next();
		while(armiesLeft > 0)
		{
			double bestScore = 0.0;
			PlaceArmiesMove bestMove = null;
			GameMap bestMap = null;
			
			for(RegionData toRegion : visibleRegions){
				if(toRegion.ownedByPlayer(myName)){
					//create potential move
					PlaceArmiesMove potMove = new PlaceArmiesMove(myName, toRegion, Math.min(armiesLeft, armies));
					//evaluate move
					GameMap potMap = createPotentialMap(plannedMap, potMove, myName);
					double potScore = 0.0; //neat.getoutput(potmap);
					potScore = neat.next(getDoubleArrayMap(potMap, state.getMap(), myName))[0]; //Only one output neuron
					if(potScore > bestScore){
						bestMove = potMove;
						bestScore = potScore;
						bestMap = potMap;
					}
				}
			}
			if(!bestMove.equals(null)){
				plannedMap = bestMap;
				plannedScore = bestScore;
				placeArmiesMoves.add(bestMove);
				armiesLeft -= armies;
			}
		}
		return placeArmiesMoves;
	}

	/**
	 * This method is called for at the second part of each round. This example attacks if a region has
	 * more than 6 armies on it, and transfers if it has less than 6 and a neighboring owned region.
	 * @return The list of PlaceArmiesMoves for one round
	 */
	@Override
	public ArrayList<AttackTransferMove> getAttackTransferMoves(BotState state, Long timeOut) 
	{
		finalState = state;
		//Score = neat.getOutput(state.getFullMap.getMapCopy);
		ArrayList<AttackTransferMove> attackTransferMoves = new ArrayList<AttackTransferMove>();
		String myName = state.getMyPlayerName();
		int armies = 5;
		GameMap plannedMap = state.getFullMap().getMapCopy();
		double plannedMapScore = 0.0; // = NEAT.getOutput(plannelMap);
		
		for(RegionData fromRegion : state.getMap().getRegions())
		{
			if(fromRegion.ownedByPlayer(myName)) //do an attack
			{
				ArrayList<RegionData> possibleToRegions = new ArrayList<RegionData>();
				possibleToRegions.addAll(fromRegion.getNeighbors());	
				
				// ----- Incredible Stuff -----
				AttackTransferMove bestMove = null;
				double bestMoveScore = plannedMapScore;
				GameMap bestPotMap = plannedMap;
				
				// generate move from realMap
				for(RegionData toRegion : possibleToRegions){

					// generate move from realMap
					armies = fromRegion.getArmies()-1;
					AttackTransferMove potMove = new AttackTransferMove(myName, fromRegion, toRegion, armies);
					GameMap potMap = createPotentialMap(state, potMove);
					
					// evaluate move
					double potScore = 0.0; //NEAT.getOutPut(potMap)
					if(potScore > bestMoveScore)
					{
						bestMove = potMove;
						bestMoveScore = potScore;
					}
				}	
				if(!bestMove.equals(null)){
					//add best move
					attackTransferMoves.add(bestMove);
					plannedMap = bestPotMap;
					plannedMapScore = bestMoveScore;
				}				
			}
		}
		
		return attackTransferMoves;
	}

	/**
	 * Get a float[] representation of the given map based on the player's visible map.
	 * @param fullMap
	 * @param visibleMap
	 * @param playerName
	 * @return
	 */
	public double[] getDoubleArrayMap(GameMap fullMap, GameMap visibleMap, String playerName){
		LinkedList<RegionData> full = fullMap.getRegions();
		LinkedList<RegionData> visible = visibleMap.getRegions();
		double[] result = new double[full.size()];
		//Value is equal to army strength. Positive when owned, negative when enemy or neutral. Same as method below.
		for(RegionData r : full){
			if(visible.contains(r)){
				if(r.ownedByPlayer(playerName)){
					result[r.getId()] = r.getArmies();			//Owned region
				} else {
					result[r.getId()] = -1.0 * r.getArmies(); 	//Enemy or neutral region
				}
			} else {
				result[r.getId()] = -2.0;						//Unknown territory, assumed neutral
			}
		}
		return result;	
	}
	
	public GameMap createPotentialMap(GameMap map, Move move, String playerName){
		GameMap result = map.getMapCopy();
		if(move.getClass() == PlaceArmiesMove.class){			//Place armies move
			PlaceArmiesMove place = (PlaceArmiesMove)move;
			RegionData r = result.getRegion(place.getRegion().getId());
			r.setArmies(place.getArmies()+r.getArmies());		//add armies to region
		} else if(move.getClass() == AttackTransferMove.class){
			AttackTransferMove att = (AttackTransferMove)move;
			RegionData from = result.getRegion(att.getFromRegion().getId());
			RegionData to = result.getRegion(att.getToRegion().getId());
			if(att.getToRegion().ownedByPlayer(playerName)){
				//transfer move
				from.setArmies(from.getArmies()-att.getArmies());		//remove armies from FromRegion
				to.setArmies(to.getArmies()+att.getArmies());			//add armies to ToRegion
			} else {
				//Attack move
				int[] combatResult = getAverageOutcome(att.getToRegion().getArmies(), att.getArmies());
				if(combatResult[1]==0){	
					//Defenders lost
					to.setArmies(combatResult[0]);						//add surviving attackers to defending region
					from.setArmies(from.getArmies()-att.getArmies());	//remove attack armies from origin
					to.setPlayerName(playerName);
				} else if(combatResult[0]>0){
					//Defenders won
					to.setArmies(combatResult[1]);						//Set surviving defenders
					from.setArmies(from.getArmies()-att.getArmies()+combatResult[0]);	//Return potential survivors
				}
			}
		}
		return result;
	}
	
	public GameMap createPotentialMap(BotState state, Move move){
		GameMap map = state.getFullMap().getMapCopy();
		if(move.getClass() == PlaceArmiesMove.class){			//Place armies move
			PlaceArmiesMove place = (PlaceArmiesMove)move;
			RegionData r = map.getRegion(place.getRegion().getId());
			r.setArmies(place.getArmies()+r.getArmies());		//add armies to region
		} else if(move.getClass() == AttackTransferMove.class){
			AttackTransferMove att = (AttackTransferMove)move;
			RegionData from = map.getRegion(att.getFromRegion().getId());
			RegionData to = map.getRegion(att.getToRegion().getId());
			if(att.getToRegion().ownedByPlayer(state.getMyPlayerName())){
				//transfer move
				from.setArmies(from.getArmies()-att.getArmies());		//remove armies from FromRegion
				to.setArmies(to.getArmies()+att.getArmies());			//add armies to ToRegion
			} else {
				//Attack move
				int[] combatResult = getAverageOutcome(att.getToRegion().getArmies(), att.getArmies());
				if(combatResult[1]==0){	
					//Defenders lost
					to.setArmies(combatResult[0]);						//add surviving attackers to defending region
					from.setArmies(from.getArmies()-att.getArmies());	//remove attack armies from origin
					to.setPlayerName(state.getMyPlayerName());
				} else if(combatResult[0]>0){
					//Defenders won
					to.setArmies(combatResult[1]);						//Set surviving defenders
					from.setArmies(from.getArmies()-att.getArmies()+combatResult[0]);	//Return potential survivors
				}
			}
		}
		return map;
	}
	
	public float[] createWeightedPotentialState(BotState state, Move move){
		GameMap map = state.getFullMap().getMapCopy();
		
		
		if(move.getClass() == PlaceArmiesMove.class){			//Place armies move
			PlaceArmiesMove place = (PlaceArmiesMove)move;
			RegionData r = map.getRegion(place.getRegion().getId());
			r.setArmies(place.getArmies()+r.getArmies());		//add armies to region
		} else if(move.getClass() == AttackTransferMove.class){
			AttackTransferMove att = (AttackTransferMove)move;
			RegionData from = map.getRegion(att.getFromRegion().getId());
			RegionData to = map.getRegion(att.getToRegion().getId());
			if(att.getToRegion().ownedByPlayer(state.getMyPlayerName())){
				//transfer move
				from.setArmies(from.getArmies()-att.getArmies());		//remove armies from FromRegion
				to.setArmies(to.getArmies()+att.getArmies());			//add armies to ToRegion
			} else {
				//Attack move
				int[] combatResult = getAverageOutcome(att.getToRegion().getArmies(), att.getArmies());
				if(combatResult[1]==0){	
					//Defenders lost
					to.setArmies(-combatResult[0]);						//add surviving attackers to defending region
					from.setArmies(from.getArmies()-att.getArmies());	//remove attack armies from origin
				} else if(combatResult[0]>0){
					//Defenders won
					to.setArmies(combatResult[1]);						//Set surviving defenders
					from.setArmies(from.getArmies()-att.getArmies()+combatResult[0]);	//Return potential survivors
				}
			}
		}	

		// DO SOME MAGIC 
		// Apply value for each region dependent on strengths, threats
		LinkedList<RegionData> full = map.getRegions();
		LinkedList<RegionData> visible = map.getRegions();
		
		float[] result = new float[full.size()];
		
		//Value is equal to army strength. Positive when owned, negative when enemy or neutral. Same as method below.
		for(RegionData r : map.getRegions()){
			if(visible.contains(r)){
				//CALCULATE STRENGTH
				//Sum nearbours.armies+this.armies
				
				if(r.ownedByPlayer(state.getMyPlayerName())){
					result[r.getId()] = r.getArmies();			//Owned region
					
					
				} else {
					result[r.getId()] = -1.0f * r.getArmies(); 	//Enemy or neutral region
				}
			} else {
				result[r.getId()] = -2.0f;						//Unknown territory, assumed neutral
			}
		}
		return result;
	}
	
	public float[] createPotentialState(BotState state, Move move){		
		LinkedList<RegionData> full = state.getFullMap().getRegions();
		LinkedList<RegionData> visible = state.getMap().getRegions();
		
		float[] result = new float[full.size()];
		
		for(RegionData r : state.getFullMap().getRegions()){
			if(visible.contains(r)){
				if(r.ownedByPlayer(state.getMyPlayerName())){
					result[r.getId()] = r.getArmies();			//Owned region
				} else {
					result[r.getId()] = -1.0f * r.getArmies(); 	//Enemy or neutral region
				}
			} else {
				result[r.getId()] = -2.0f;						//Unknown territory, assumed neutral
			}
		}
		
		if(move.getClass() == PlaceArmiesMove.class){			//Place armies move
			PlaceArmiesMove place = (PlaceArmiesMove)move;
			result[place.getRegion().getId()] += place.getArmies();	//add armies to region
		} else if(move.getClass() == AttackTransferMove.class){
			AttackTransferMove att = (AttackTransferMove)move;
			if(att.getToRegion().ownedByPlayer(state.getMyPlayerName())){
				//transfer move
				result[att.getFromRegion().getId()] -= att.getArmies();	//remove armies from FromRegion
				result[att.getToRegion().getId()] += att.getArmies();		//add armies to ToRegion
			} else {
				//Attack move
				int[] combatResult = getAverageOutcome(att.getToRegion().getArmies(), att.getArmies());
				if(combatResult[1]==0){	
					//Defenders lost
					result[att.getToRegion().getId()] = combatResult[0];		//add surviving attackers to defending region
					result[att.getFromRegion().getId()] -= att.getArmies();		//remove attack armies from origin
				} else if(combatResult[0]>0){
					//Defenders won
					result[att.getToRegion().getId()] = -1.0f*combatResult[1]; 	//Set surviving defenders
					result[att.getFromRegion().getId()] -= att.getArmies(); 	//remove armies that went to attack
					result[att.getFromRegion().getId()] += combatResult[0];		//add retreating survivors
				}
			}
		}	
		return result;
	}
	
	@Override
	public void setGUI(GUI gui) {
	}
	
	/**
	 * @param defenders
	 * @param attackers
	 * @return size 2 array. [0] surviving attacker, [1] surviving defenders.
	 */
	private int[] getAverageOutcome(int defenders, int attackers){
		boolean original=true;
		int[] result = new int[2];
		int dd;
		int da;
		if(original){			
			//Original attack
			dd = Math.round(attackers*0.6f);
			da = Math.round(defenders*0.7f);
		} else {
			//Continual attack
			float turnsToKill = (defenders/0.6f);
			float otherTTK = (attackers/0.7f);
			if(otherTTK < turnsToKill){
				turnsToKill = otherTTK;						//how many turns will it take
			}
			dd = Math.round(turnsToKill*0.6f);
			da = Math.round(turnsToKill*0.7f);
		}
		if(da >= attackers){
			da = attackers;
			if(dd >= defenders){
				dd = defenders-1;
			}
		} else if(dd >= defenders){
			dd = defenders;
		}
		result[0] = attackers-da;
		result[1] = defenders-dd;
		return result;
	}
	
	public BotState run(){
		BotParser parser = new BotParser(new NEATWarlightBot());
		//parser.setLogFile(new File("./BotStarter.log"));
		parser.run();
		return finalState;
	}
	
	public static void main(String[] args)
	{

		BotParser parser = new BotParser(new NEATWarlightBot());
		//parser.setLogFile(new File("./BotStarter.log"));
		parser.run();
	}

}
