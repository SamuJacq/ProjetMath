package tree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class LexicographicTree {
	private Noeud arbre;
	private int numberWord = 0;
	/*
	 * CONSTRUCTORS
	 */
	
	/**
	 * Constructor : creates an empty lexicographic tree.
	 */
	public LexicographicTree() {
		this.arbre = new Noeud();
	}
	
	/**
	 * Constructor : creates a lexicographic tree populated with words 
	 * @param filename A text file containing the words to be inserted in the tree 
	 */
	public LexicographicTree(String filename) {
		this();
		try(BufferedReader br = new BufferedReader(new FileReader(new File(filename)))){
			String line = br.readLine();
			while(line != null){
				insertWord(line);
				line = br.readLine();
			}
		}catch (IOException e) {
			
		}
		
	}
	
	/*
	 * PUBLIC METHODS
	 */
	
	/**
	 * Returns the number of words present in the lexicographic tree.
	 * @return The number of words present in the lexicographic tree
	 */
	public int size() {
		return numberWord;
	}

	/**
	 * Inserts a word in the lexicographic tree if not already present.
	 * @param word A word
	 */
	public void insertWord(String word) {
		if(word == null) {
			return;
		}
		if(!arbre.isEndWord() && word == "") {
			this.arbre.setLetter('\0');
			numberWord++;
			this.arbre.setEndWord();
			return;
		}
		word = word.toLowerCase().trim();
		
		Noeud noeudActuel = this.arbre;
	    for(int i = 0; i < word.length(); i++) {
	    	char letter = remplaceDiacritque(word.charAt(i));
	    	letter = Character.toUpperCase(letter);
	    	if(letter == '\'' || letter == '-' || Character.isLetter(letter)) {
	    		Noeud noeudSuivant = new Noeud(letter);
		    	if(noeudActuel.getNoeudSuivant() == null) { 
		    		noeudActuel.addNoeudSuivant(noeudSuivant);
		   			noeudActuel = noeudSuivant;
		   		}else {
		   			Noeud next = noeudActuel.getNoeudCourant(letter);
		   			if(next == null) {
		   				noeudActuel.addNoeudSuivant(noeudSuivant);
		   				noeudActuel = noeudSuivant;
		   			}else {
		   				noeudActuel = next;
		   			}
		   		}
	   		}
	   	}
	    if(!noeudActuel.isEndWord() && word != "") {
	   		numberWord++;
	   		noeudActuel.setEndWord();
	   	}
		
	}
	
	/**
	 * Determines if a word is present in the lexicographic tree.
	 * @param word A word
	 * @return True if the word is present, false otherwise
	 */
	public boolean containsWord(String word) {
		if(word == null)
    		return false;
		if(word == "" && this.arbre.isEndWord())
			return true;
		
    	Noeud noeudActuel = checkPrefix(word, this.arbre);
    	return noeudActuel != null && noeudActuel.isEndWord();
	}
	
	/**
	 * Determines if a prefix is present in the lexicographic tree.
	 * @param prefix A prefix
	 * @return True if a prefix is present, false otherwise
	 */
	public boolean containsPrefix(String prefix) {
		if(prefix == null)
    		return false;
		if(prefix == "" && this.arbre.isEndWord())
			return true;
		
		return checkPrefix(prefix, this.arbre) != null;
	}
	
	/**
	 * Returns an alphabetic list of all words starting with the supplied prefix.
	 * If 'prefix' is an empty string, all words are returned.
	 * @param prefix Expected prefix
	 * @return The list of words starting with the supplied prefix
	 */
	public List<String> getWords(String prefix) {
		List<String> wordSave = new ArrayList<>();
		
		prefix = prefix == null ? "" : normaliserWord(prefix);
    	getWord(wordSave, prefix, "", this.arbre);
    	Collections.sort(wordSave);
    	
    	return wordSave;
	}

	/**
	 * Returns an alphabetic list of all words of a given length.
	 * If 'length' is lower than or equal to zero, an empty list is returned.
	 * @param length Expected word length
	 * @return The list of words with the given length
	 */
	public List<String> getWordsOfLength(int length) {
		List<String> wordSave = new ArrayList<>();
    	
	    getWordLength(wordSave, length, "", this.arbre);
	    Collections.sort(wordSave);
	    
    	return wordSave;
	}

	/*
	 * PRIVATE METHODS
	 */
	
	/**
     * méthode récursive qui cherche les modes dans l'arbre
     * @param listWords
     * 		liste des mots trouvé
     * @param prefix
     * 		prefix des mots qu'on cherche
     * @param wordBuild
     * 		mot en construction des noeuds
     * @param actuel
     * 		dernier noeud atteint 
     */
    private void getWord(List<String> listWords, String prefix, String wordBuild, Noeud actuel){
    	if(actuel.getNoeudSuivant() == null) {
    		return;
    	}
    	for(Noeud suivant : actuel.getNoeudSuivant()) {
    		if(prefix.length() <= wordBuild.length() || suivant.getLetter() == prefix.charAt(wordBuild.length())) {
    			wordBuild += suivant.getLetter();
        		if(suivant.isEndWord()) {
        			listWords.add(wordBuild);
        		}
        		getWord(listWords, prefix, wordBuild, suivant);
        		wordBuild = wordBuild.substring(0, wordBuild.length() - 1);
    		}
    	}
    }
    
    /**
     * récupere les mots en fonction dans la longueur donnée
     * @param listWords
     * 		liste des mots trouvé
     * @param length
     * 		longueur des mots que l'on souhaite
     * @param wordBuild
     * 		mot en construction
     * @param actuel
     */
    private void getWordLength(List<String> listWords, int length, String wordBuild, Noeud actuel){
    	if(actuel.getNoeudSuivant() == null) {
    		return;
    	}
    	for(Noeud suivant : actuel.getNoeudSuivant()) {
    		wordBuild += suivant.getLetter();
		    if(suivant.isEndWord() && wordBuild.length() == length) 
		    	listWords.add(wordBuild);
		        	
		    if(wordBuild.length() < length) 
		       	getWordLength(listWords, length, wordBuild, suivant);
		        	
		    wordBuild = wordBuild.substring(0, wordBuild.length() - 1);
    	}
    }
	
    /**
     * vérifie si le préfix existe dans l'arbre
     * @param prefix
     * 			préfix qu'on cherche
     * @param courant
     * 			noeud courant dans la recherche
     * @return préfix trouvé
     */
	private Noeud checkPrefix(String prefix, Noeud courant) {
		prefix = normaliserWord(prefix);
    	for(int i = 0; i < prefix.length() && courant != null; i++) 
    		courant = courant.getNoeudCourant(prefix.charAt(i)); 	
    	
    	return courant;
	}
	
	/**
	 * retire tous les caractères spécial du mot fournit
	 * @param word
	 * 			mot fournit
	 * @return mot sans caractère spécial
	 */
	private String normaliserWord(String word) {
		String normaliseWord = "";
		for(int i = 0; i < word.length(); i++) {
			normaliseWord += remplaceDiacritque(word.charAt(i));
		}
		return normaliseWord.toUpperCase();
	}
	
	/**
	 * remplacer le caractère spécial par sa lettre normal
	 * @param letter
	 * 			lettre à vérifier
	 * @return lettre sans caractère spécial
	 */
	private char remplaceDiacritque(char letter) {
		if(letter == 'à' || letter == 'â' || letter == 'ä') {
			return 'a';
		}
		if(letter == 'ç') {
			return 'c';
		}
		if(letter == 'é' || letter == 'è' || letter == 'ê' || letter == 'ë') {
			return 'e';
		}
		if(letter == 'î' || letter == 'ï') {
			return 'i';
		}
		if(letter == 'ö' || letter == 'ô') {
			return 'o';
		}
		if(letter == 'ù' || letter == 'û' || letter == 'ü') {
			return 'u';
		}
		if(letter == 'ÿ') {
			return 'y';
		}
		return letter;
		
	}
	
	/*
	 * TEST FUNCTIONS
	 */
		
	private static String numberToWord(long number) {
		String word = "";
		int radix = 2;
		do {
			word = (((int)(number % radix) == 0) ? "a" : "m") + word;
			number = number / radix;
		} while(number != 0);
		return word;
	}
	
	private static void testDictionaryPerformance(String filename) {
		long startTime;
		int repeatCount = 20;
		
		// Create tree from list of words
		startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary...");
		LexicographicTree dico = null;
		for (int i = 0; i < repeatCount; i++) {
			dico = new LexicographicTree(filename);
		}
		System.out.println("Load time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println("Number of words : " + dico.size());
		System.out.println();
		
		// Search existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching existing words in dictionary...");
		File file = new File(filename);
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine();
				    boolean found = dico.containsWord(word);
				    if (!found) {
				    	//System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search non-existing words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching non-existing words in dictionary...");
		for (int i = 0; i < repeatCount; i++) {
			Scanner input;
			try {
				input = new Scanner(file);
				while (input.hasNextLine()) {
				    String word = input.nextLine() + "xx";
				    boolean found = dico.containsWord(word);
				    if (found) {
				    	System.out.println(word + " / " + word.length() + " -> " + found);
				    }
				}
				input.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Retrieve all words in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Retrieving all words in dictionary...");
		for (int i = 0; i < 100; i++) {
			int count = dico.getWords("").size();
			if (dico.size() != count) {
				System.out.printf("Count mismatch : dict size = %d / getWords() count = %d\n", dico.size(), count);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();

		// Search words of increasing length in dictionary
		startTime = System.currentTimeMillis();
		System.out.println("Searching for words of increasing length...");
		for (int i = 0; i < 5; i++) {
			int total = 0;
			for (int n = 0; n <= 28; n++) {
				int count = dico.getWordsOfLength(n).size();
				total += count;
			}
			if (dico.size() != total) {
				System.out.printf("Total mismatch : dict size = %d / getWordsOfLength() total = %d\n", dico.size(), total);
			}
		}
		System.out.println("Search time : " + (System.currentTimeMillis() - startTime) / 1000.0);
		System.out.println();
	}

	private static void testDictionarySize() {
		final int MB = 1024 * 1024;
		System.out.print(Runtime.getRuntime().totalMemory()/MB + " / ");
		System.out.println(Runtime.getRuntime().maxMemory()/MB);

		LexicographicTree dico = new LexicographicTree();
		long count = 0;
		while (true) {
			dico.insertWord(numberToWord(count));
			count++;
			if (count % MB == 0) {
				System.out.println(count / MB + "M -> " + Runtime.getRuntime().freeMemory()/MB);
			}
		}
	}
	
	/*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		// CTT : test de performance insertion/recherche
		testDictionaryPerformance("mots/dictionnaire_FR_avec_accents.txt");
		
		// CST : test de taille maximale si VM -Xms2048m -Xmx2048m
		testDictionarySize();
	}
}
