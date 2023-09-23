package cryptanalysis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tree.LexicographicTree;

public class DictionaryBasedAnalysis {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DICTIONARY = "mots/dictionnaire_FR_avec_accents.txt";
	
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock

	private String cryptogram;
	private LexicographicTree dict;
	private List<String> words;
	private String bestDecoding;
	private int bestNumberFind;
	
	/*
	 * CONSTRUCTOR
	 */
	public DictionaryBasedAnalysis(String cryptogram, LexicographicTree dict) {
		this.cryptogram = cryptogram;
		this.dict = dict;
		this.words = getWord(this.cryptogram);
		this.bestDecoding = "";
		this.bestNumberFind = 0;
	}
	
	/*
	 * PUBLIC METHODS
	 */

	/**
	 * Performs a dictionary-based analysis of the cryptogram and returns an approximated decoding alphabet.
	 * @param alphabet The decoding alphabet from which the analysis starts
	 * @return The decoding alphabet at the end of the analysis process
	 */
	public String guessApproximatedAlphabet(String alphabet) {
		if(alphabet == null || alphabet.length() != 26 || !checkAlphabet(alphabet))
			throw new IllegalArgumentException();
		
		this.bestDecoding = alphabet.toUpperCase();
		char[] decodingAlphabet = new char[26];
		List<String> listLength = null;
		Set<String> decodindWord = new HashSet<String>();
		for(String encoded : this.words) {
			if(!decodindWord.contains(encoded) && encoded.chars().distinct().count() < encoded.length() && !dict.containsWord(encoded)) {
				if(listLength == null || encoded.length() < listLength.get(0).length()) {
					listLength = new LinkedList<>(dict.getWordsOfLength(encoded.length()));
				}
				for(String word : listLength) {
					if(Arrays.equals(possibleCandidat(word), possibleCandidat(encoded))){
						decodingAlphabet = updateAlphabet(this.bestDecoding, encoded.toUpperCase(), word.toUpperCase()).toCharArray();
						if(!this.bestDecoding.equals(new String(decodingAlphabet))) 
							testKeyDecoding(decodindWord, decodingAlphabet);
						break;
					}
				}
			}
		}
		
		return this.bestDecoding;
	}

	/**
	 * Applies an alphabet-specified substitution to a text.
	 * @param text A text
	 * @param alphabet A substitution alphabet
	 * @return The substituted text
	 */
	public static String applySubstitution(String text, String alphabet) {
		if(text == null || alphabet == null || alphabet.length() != 26 || !checkAlphabet(alphabet)) {
			throw new IllegalArgumentException();
		}
		StringBuilder substitution = new StringBuilder();
		for(String word : text.split("\\s+")) {
			substitution.append(dechiffrer(alphabet.toCharArray(), word) + " ");
		}
		return substitution.toString().trim();
	}
	
	/**
	 * Compare invalid and candidate words and update current decoding alphabet accordingly.
	 * @param cryptogramWord A decoded word that is not valid (i.e. not found in the dictionary)
	 * @param candidateWord A dictionary word that could be the correct decoded word
	 * @param currentAlphabet The current decoding alphabet
	 * @return The updated decoding alphabet, or the original alphabet if no changes have been made
	 */
	public static String updateAlphabet(String currentAlphabet, String cryptogramWord, String candidateWord) {
		char[] alphabetTab = currentAlphabet.toCharArray();
		if(candidateWord.equals(applySubstitution(cryptogramWord, currentAlphabet))) {
			return currentAlphabet;
		}
		// A = 65 Z = 90
		Set<Character> alreadyMove = new HashSet<>();
		for(int i = 0; i < cryptogramWord.length(); i++) {
			char letterCrypto = cryptogramWord.charAt(i);
			char letterCandidat = candidateWord.charAt(i);
			int pos = (int) letterCrypto % 65; 
			char saveCrypto = alphabetTab[pos];
			if(!alreadyMove.contains(letterCandidat) && letterCandidat != '-' && letterCandidat != '\'') {
				alphabetTab[pos] = letterCandidat;
				alreadyMove.add(letterCandidat);
				if(new String(alphabetTab).chars().distinct().count() != alphabetTab.length) {
					for(int j = 0; j < alphabetTab.length; j++) {
						if(alphabetTab[j] == letterCandidat && j != pos) {
							alphabetTab[j] = saveCrypto;
							break;
						}
					}
				}
			}
		}
		return new String(alphabetTab);
	}
	
	/*
	 * PRIVATE METHODS
	 */
	
	/**
	 * vérifie si l'alphabet contient toutes les lettres 
	 * de l'ahpahbet
	 * @param alphabet
	 * 			alphabet à vérifier
	 * @return true si l'alaphabet est correct
	 */
	private static boolean checkAlphabet(String alphabet) {
		int[] tab = new int[26];
		for(int i = 0; i < alphabet.length(); i++) {
			int ascii = (int) alphabet.charAt(i) %65;
			if(ascii >= 0 && ascii <= 25) {
				tab[ascii] = 1;
 			}else {
				return false;
			}
		}
		return Arrays.stream(tab).distinct().count() == 1;
	}
	
	/**
	 * test la clé trouvé sur l'ensemble des mots
	 * @param wordFindList
	 * 		liste des mots déjà déchiffrer
	 * @param decodingAlphabet
	 * 		clé pour déchiffrer
	 */
	private void testKeyDecoding(Set<String> decodindWord, char[] decodingAlphabet) {
		int number = 0;
		for(String word : this.words) {
			String dechiffre = dechiffrer(decodingAlphabet, word);
			if(dict.containsWord(dechiffre.toLowerCase())) {
				number++;
				decodindWord.add(word);
			}
		}
		if(this.bestNumberFind < number) {
			this.bestNumberFind = number;
			this.bestDecoding =  new String(decodingAlphabet);
		}else {
			decodingAlphabet = this.bestDecoding.toCharArray();
		}
	}
	
	/**
	 * transforme un mot en tableau de int pour indiquer les 
	 * endroits dans le mot où ce répete les mêmes lettres
	 * @param word
	 * 		mot à transformer
	 * @return tableau de int
	 */
	private static int[] possibleCandidat(String word) {
		int[] tab = new int[word.length()];
		for(int i = 0; i<word.length();i++) {
			if(tab[i] == 0) {
				tab[i] = i+1;
			}
			for(int j = i+1; j<word.length(); j++) {
				if(tab[j] == 0 && word.charAt(j) == word.charAt(i)) {
					tab[j] = i+1;
				}
			}
		}
		return tab;
	}
	
	
	/**
	 * déchiffre les mots avec la clé pour déchiffrer
	 * @param keyDecoding
	 * 		clé pour déchiffrer
	 * @param word
	 * 		mot chiffrer
	 * @return le mot déchiffré
	 */
	private static String dechiffrer(char[] keyDecoding, String word) {
		char[] dechiffrer = word.toCharArray();
		for(int i = 0; i< dechiffrer.length;i++) {
			for(int j = 0; j< keyDecoding.length;j++) {
				if(dechiffrer[i] == LETTERS.charAt(j)) {
					dechiffrer[i] = keyDecoding[j];
					break;
				}
			}
		}
		return new String(dechiffrer);
	}
	
	/**
	 * récupere tous les mots du text, remplace les caractères spéciale
	 * en espace
	 * @param text
	 * 		texte des mots
	 * @return list des mots
	 */
	private static List<String> getWord(String text){
		List<String> list = new ArrayList<>();
		for(String word : text.split("\\s+")) {
			if(word.length() >= 3 && !list.contains(word)) {
				list.add(word);
			}
		}
		Comparator<String> comparateur = Comparator.comparingInt(String::length);
        Collections.sort(list, comparateur.reversed());
		return list;
	}

	/**
	 * Compares two substitution alphabets.
	 * @param a First substitution alphabet
	 * @param b Second substitution alphabet
	 * @return A string where differing positions are indicated with an 'x'
	 */
	private static String compareAlphabets(String a, String b) {
		String result = "";
		for (int i = 0; i < a.length(); i++) {
			result += (a.charAt(i) == b.charAt(i)) ? " " : "x";
		}
		return result;
	}
	
	/**
	 * Load the text file pointed to by pathname into a String.
	 * @param pathname A path to text file.
	 * @param encoding Character set used by the text file.
	 * @return A String containing the text in the file.
	 * @throws IOException
	 */
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

    /*
	 * MAIN PROGRAM
	 */
	
	public static void main(String[] args) {
		/*
		 * Load dictionary
		 */
		long startTime = System.currentTimeMillis();
		System.out.println("Loading dictionary... ");
		LexicographicTree dict = new LexicographicTree(DICTIONARY);
		long loadDictTime = System.currentTimeMillis();
		System.out.println("Duration : " + (loadDictTime - startTime)/1000.0);
		System.out.println("Number of words : " + dict.size());
		System.out.println();
		
		/*
		 * Load cryptogram
		 */
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
//		System.out.println("*** CRYPTOGRAM ***\n" + cryptogram.substring(0, 100));
//		System.out.println();

		/*
		 *  Decode cryptogram
		 */
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		String startAlphabet = LETTERS;
//		String startAlphabet = "ZISHNFOBMAVQLPEUGWXTDYRJKC"; // Random alphabet
		String finalAlphabet = dba.guessApproximatedAlphabet(startAlphabet);
		
		// Display final results
		long solveTime = System.currentTimeMillis();
		System.out.println("-----------------------------------------------------------------");
		System.out.println();
		System.out.println("Standard     alphabet : " + startAlphabet);
		System.out.println("Decoding     alphabet : " + DECODING_ALPHABET);
		System.out.println("Approximated alphabet : " + finalAlphabet);
		System.out.println("Remaining differences : " + compareAlphabets(DECODING_ALPHABET, finalAlphabet));
		System.out.println();
		System.out.println("Analysis duration : " + (solveTime - loadDictTime)/1000.0);
		System.out.println();
		
		// Display decoded text
		System.out.println("-----------------------------------------------------------------");
		System.out.println();
		System.out.println("*** DECODED TEXT ***\n" + applySubstitution(cryptogram, finalAlphabet).substring(0, Math.min(200, cryptogram.length())));
		System.out.println();
	}
}
