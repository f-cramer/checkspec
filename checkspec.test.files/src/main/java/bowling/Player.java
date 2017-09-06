package bowling;

/**
 * 
 * @author Anna Windt, Nathalie Steubing, Isabell L�mmermann
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
	 * gibt Name des Players zur�ck	 
	 * @return Name des Players
	 */
    public String getName(){
		return this.name;
	}
	
    /**
     * gibt ID des Players zur�ck
     * @return ID des Players
     */
	public int getID(){
		return this.id;
	}
}
