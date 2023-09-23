package tree;

/**
 * Classe qui est un noeud dans l'arbre qui connait sa lettre, 
 * s'il est la fin d'un mot, ses noeuds suivants et 
 * @author Samuel Jacquemin
 *
 */
public class Noeud {
	
	private char letter;
	private boolean endWord = false;
	private Noeud[] noeudSuivant;
	
	/**
	 * constructeur de Noeud sans spécifier de lettre 
	 */
	public Noeud() {
        this.noeudSuivant = null;
    }
	
	/**
	 * constructeur de Noeud en spécifier une lettre 
	 * @param letter
	 * 		lettre que le noeud spécifie
	 */
	public Noeud(char letter) {
		this.letter = letter;
		this.noeudSuivant = null;
	}
    
	/**
	 * retourne true si le noeud est la fin d'un mot
	 * @return true si le noeud est la fin d'un mot
	 */
    public boolean isEndWord() {
		return this.endWord;
	}
	
    /**
     * indique que le noeud est la fin d'un mot
     */
	public void setEndWord() {
		this.endWord = true;
	}
	
	/**
	 * retourne la lettre du noeud
	 * @return la lettre du noeud
	 */
	public char getLetter() {
		return letter;
	}
	
	/**
	 * retourne la lettre du noeud
	 * @return la lettre du noeud
	 */
	public void setLetter(char letter) {
		this.letter = letter;
	}
	
	/**
	 * retourne le tableau des noeud suivants
	 * @return le tableau des noeud suivants
	 */
	public Noeud[] getNoeudSuivant() {
		return noeudSuivant;
	}
	
	/**
	 * cherche un noeud dans le tableau des noeuds enfants avec 
	 * la lettre qu'on cherche
	 * @param letter
	 * 			lettre chercher
	 * @return noeud enfant représentant la lettre
	 */
	public Noeud getNoeudCourant(char letter) {
		
		if(this.noeudSuivant != null) {
			for(int i = 0; i < this.noeudSuivant.length; i++) {
				if(this.noeudSuivant[i].getLetter() == letter) {
					return this.noeudSuivant[i];
				}
			}
		}
		return null;
	}
	
	/**
     * ajoute un noeud dans le tableau du noeud actuel
     * @param suivant
     * 		noeud qu'on doit ajouter
     */
    public void addNoeudSuivant(Noeud suivant){
    	if(this.noeudSuivant == null) {
    		this.noeudSuivant = new Noeud[0];
    	}
    	
		int N = this.noeudSuivant.length;
		Noeud[] newSuivant = new Noeud[N+1];
		System.arraycopy(this.noeudSuivant,0,newSuivant, 0, N);
		newSuivant[N] = suivant;
		this.noeudSuivant = newSuivant;
	}
	
}
