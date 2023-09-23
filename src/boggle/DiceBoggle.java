package boggle;

/**
 * classe qui représente un dé dans le graph
 * connait sa lettre
 * @author Samuel Jacquemin
 *
 */
public class DiceBoggle {

	private String letter;
	
	/**
	 * constructeu de DiceBoggle
	 * @param letter
	 * 		lettre du dé
	 */
	public DiceBoggle(String letter) {
		this.letter = letter;
	}
	
	/**
	 * retourne la lettre du dé
	 * @return lettre du dé
	 */
	public String getLetter() {
		return this.letter;
	}
	
}
