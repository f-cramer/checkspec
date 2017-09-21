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
public class Bowling extends Game {

	int counter;
	
	/**
	 * setzt die n�tigen Attribute
	 * @param max maximale Anzahl an Mitspielern
	 */
	public Bowling(int max) {
		super(max);
		name = "Bowling";
		pins = 10;
		rounds = 10;
		remainingPins = 10;
		counter =0;
	}

	@Override
	public Player addPlayer(String name) {
	
		 if(hasStarted()==true || activePlayers==maxPlayer){
			 return null; 
		 }
		   else{
			   if (activePlayers ==0){
			    	  Player newPlayer= new Player (name, 0);
					   
					   players[0] = newPlayer;
					   players[0].points = new int[22];
					   currentPlayer = newPlayer;
					   activePlayers = 1+ activePlayers;
					   return newPlayer;
			      }
			   else {
			   
			   
		             Player newPlayer= new Player (name, activePlayers);
		   
		             players[activePlayers] = newPlayer;
		             players[activePlayers].points = new int[22];
		   
		             activePlayers = 1+ activePlayers;
		             
		             
		             return newPlayer;
			   }
		 }
	   }

	@Override
	public int[] getScore(Player player) {
		return player.points;
	}
	
	public void setScore(Player player){
	  	
	}

	/*
	 * @return all points out of the points array of a certain player
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
		for (int i=0; i< players.length; i++){
		   if (getPoints(players[i]) >= getPoints(players[i+1])){
			  winner = players[i];
				}
		   else 
			  winner = players[i+1];
			}
		  return winner;
		}
		
	 

	@Override
	public boolean hasFinished() {
		return finishedRounds;
	}

	@Override
	public boolean throwBall(int count) {
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
    	else if (count > getPinsLeft()){
    		System.err.println("Not enough pins left");	
    		return false;
    	}
    	else {
    		if (currentRound< rounds){
    			normal(count);
    			
    		}
    		if (currentRound == rounds){
    			last(count);
    		}
    	}
		return true;
	}
	
	/**
	 * Hilfsmethode f�r die Runden 1 bis 9
	 * @param count Anzahl der getroffenen Pins
	 */
	public void normal (int count){
		if (currentThrow==1){
			remainingPins = pins;
			remainingPins = remainingPins - count;
			if (remainingPins==0){
				setScore(currentPlayer);
				currentThrow= 2;
				setActivePlayer();
				setRound();
			}
			
			else{
				currentThrow=2;
				setScore(currentPlayer);
				
			}
		}
		else {
			remainingPins =remainingPins-count;
			setScore(currentPlayer);
			setActivePlayer();
			setRound();
		}
	}
	
	/**
	 * Hilfsmethode f�r die 10. Runde
	 * @param count Anzahl der getroffenen Pins
	 */
	public void last (int count){
		if(currentThrow ==1){
			remainingPins=pins;
			remainingPins= remainingPins-count;
			if(remainingPins == 0){
				setScore(currentPlayer);
				counter=counter+1;
			}
			else{
			   setScore(currentPlayer);
			   currentThrow=2;
			}
		}
		
		else{
			remainingPins = remainingPins-count;
			if(remainingPins==0){
				setScore(currentPlayer);
				
			}
			
		}
	}
}
