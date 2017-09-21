package bowling;

/*-
 * #%L
 * CheckSpec Test Files
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * 
 * @author Anna Windt, Nathalie Steubing, Isabell L�mmermann
 *
 */
public class TannenbaumKegeln extends Game{
	

	/**
	 * setzt die n�tigen Attribute 
	 * @param max maximale Anzahl an Mitspieler
	 */
	public TannenbaumKegeln(int max) {
		super (max);
		name = "TannenbaumKegeln";
		pins = 9;
		rounds = 100;
		remainingPins =9;
		
	}

	@Override 
	public Player addPlayer(String name) {
		 if (hasStarted() || activePlayers==maxPlayer){
			 return null; 
		 }
		   else{
			   if (activePlayers ==0){
			    	  Player newPlayer= new Player (name, 0);
					   
					   players[0] = newPlayer;
					   players[0].points = new int[]{1,2,7,6,5,4,3,2,1};
					   currentPlayer = newPlayer;
					   activePlayers = 1+ activePlayers;
					   return newPlayer;
			      }
			   else {
			   
			   
		             Player newPlayer= new Player (name, activePlayers);
		   
		             players[activePlayers] = newPlayer;
		             players[activePlayers].points = new int[]{1,2,7,6,5,4,3,2,1};
		   
		             activePlayers = 1+ activePlayers;
		             
		             
		             return newPlayer;
			   }
		 }
	}
	
	@Override
	public boolean throwBall(int count){
	    
    	if (!hasStarted()){
    		System.err.println("Game has not started yet");
    		return false;
    	}
    	else if (hasFinished()){
    		System.err.println("Game has already finished");
    		return false;
    	}
    	else if (count < 0){
    		System.err.println("No negative amount of pins allowed");
    		return false;
    	}
    	else if (count > remainingPins){
    		System.err.println("Not enough pins left");	
    		return false;
    	}
    	else {
    		if (currentThrow == 2){
    			remainingPins = remainingPins - count;
    			setScore(currentPlayer);
    			setActivePlayer();
    			setRound();
    			
    		}
    		
    		else {
    			remainingPins = pins;
    			remainingPins = remainingPins - count;
                if (remainingPins== 0){
                	setScore(currentPlayer);
                	currentThrow = 2;
                	setActivePlayer();
                	setRound();
                }
                else 
    			currentThrow =2;
    		}
    		
    		return true;
    		
    	}
    	
    }

	@Override
	public int[] getScore(Player player) {
			return player.points;	
	}
	
	/**
	 * berehnet den Score eines Spielers
	 * @param player �bergebener Spieler
	 */
	public void setScore (Player player){
		int[] list = {0,0,0,0,0,0,0,0,0};
		int score = pins - remainingPins;
		if ( score == 0|| player.points[score-1] == 0)
			player.points = player.points;
		else {
			player.points[score-1] = player.points[score-1]-1;
			if (player.points.equals(list)){
				finishedPlayer = true;
			}	
		}
	}
    
	/*
	 * @return all points left in the points array of a certain player
	 */
	public int getPoints(Player player){
		int a = 0;
		for (int i=0; i< player.points.length; i++){
			a = a + player.points[i];
		}
		return a;
	}
	
	@Override
	public Player getWinner() {
		Player winner = null;
		if (finishedPlayer){
			int[] list = {0,0,0,0,0,0,0,0,0};
			for (int i=0; i< players.length; i++){
				if (players[i].points.equals(list))
					winner = players[i];
			}
		}
		else {
			for (int i=0; i< players.length; i++){
				if (getPoints(players[i])<= getPoints(players[i+1])){
					winner = players[i];
				}
				else 
					winner = players[i+1];
			}
		}
		
	  return winner;
	}

	@Override
	public boolean hasFinished() {
		return finishedPlayer || finishedRounds;
	}

}
	
	
