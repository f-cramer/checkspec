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
 * @author Anna Windt, Nathalie Steubing, Isabell L?mmermann
 *
 */
public class Player {
	
	 String name;
	 int id;
	 int[] points;
	 

	/**
	 * setzt name und id
	 * @param name Name des Players
	 * @param id ID des Players
	 */
	 public Player(String name, int id){
		this.name = name;
		this.id = id;
	}
	
	/**
	 * gibt Name des Players zur?ck	 
	 * @return Name des Players
	 */
    public String getName(){
		return this.name;
	}
	
    /**
     * gibt ID des Players zur?ck
     * @return ID des Players
     */
	public int getID(){
		return this.id;
	}
}
