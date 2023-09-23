package tree;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;

public class LexicographicTreeTest {
	private static final String[] WORDS = new String[] {"BUT", "ET", "été", "aide", "AS", "Au", "AuX",
			"BU", "bus"};
	private static final String[] EXPECTED_WORDS = new String[] {"AIDE", "AS", "AU", "AUX",
			"BU", "BUS", "BUT", "ET", "ETE"};
	private static final LexicographicTree DICT = new LexicographicTree();

	@BeforeAll
	static void initTestDictionary() {
		for (int i=0; i<WORDS.length; i++) {
			DICT.insertWord(WORDS[i]);
		}
	}
	
	@Test
	void constructor_EmptyDictionary() {
		LexicographicTree dict = new LexicographicTree();
		assertNotNull(dict);
		assertEquals(0, dict.size());
	}

	@Test
	void insertWord_General() {
		LexicographicTree dict = new LexicographicTree();
		for (int i=0; i<WORDS.length; i++) {
			dict.insertWord(WORDS[i]);
			assertEquals(i+1, dict.size(), "Mot " + WORDS[i] + " non inséré");
			dict.insertWord(WORDS[i]);
			assertEquals(i+1, dict.size(), "Mot " + WORDS[i] + " en double");
		}
	}
	
	@Test
	void containsWord_General() {
		for (String word : EXPECTED_WORDS) {
			assertTrue(DICT.containsWord(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] {"AID", "AI", "AIDES", "MOT", "E"}) {
			assertFalse(DICT.containsWord(word), "Mot " + word + " inexistant trouvé");
		}
	}
	
	@Test
	void containsPrefix_General() {
		for (String word : new String[] {"A", "AI", "AID", "AS", "AU", "AUX", "AIDE", "B", "BU", "BUS", "BUT", "E", "ET", "ETE"}) {
			assertTrue(DICT.containsPrefix(word), "Mot " + word + " non trouvé");
		}
		for (String word : new String[] {"AB", "AIDES", "IDE", "C", "UX"}) {
			assertFalse(DICT.containsPrefix(word), "Mot " + word + " inexistant trouvé");
		}
	}
	
	@Test
	void getWords_General() {
		assertEquals(EXPECTED_WORDS.length, DICT.getWords("").size());
		assertArrayEquals(EXPECTED_WORDS, DICT.getWords("").toArray());
		
		assertEquals(0, DICT.getWords("X").size());
		
		assertEquals(3, DICT.getWords("BU").size());
		assertArrayEquals(new String[] {"BU", "BUS", "BUT"}, DICT.getWords("BU").toArray());
	}

	@Test
	void getWordsOfLength_General() {
		assertEquals(4, DICT.getWordsOfLength(3).size());
		assertArrayEquals(new String[] {"AUX", "BUS", "BUT", "ETE"}, DICT.getWordsOfLength(3).toArray());
	}
	
	@Test
	void filenameConstructorIsOk() {
		LexicographicTree dict = new LexicographicTree("mots/dictionnaire_FR_avec_accents.txt");
		assertTrue(dict.size() == 327956);
	}
	
	@Test
	void getWordsOfLengthWithSpecialNumber() {
		assertEquals(0, DICT.getWordsOfLength(99999999).size());
		assertEquals(0, DICT.getWordsOfLength(-1).size());
		assertEquals(0, DICT.getWordsOfLength(-99999999).size());
		assertEquals(0, DICT.getWordsOfLength(1).size());
	}
	
	@Test
	void getWordsWithSpecialPrefix() {
		assertEquals(0, DICT.getWords("zlejfzojvn").size());
		assertEquals(0, DICT.getWords("*-/*/").size());
		assertEquals(0, DICT.getWords("656451").size());
		assertEquals(0, DICT.getWords("éèëê").size());
	}
	
	@Test
	void insertValueNullOrEmpty() {
		LexicographicTree DICT = new LexicographicTree();
		DICT.insertWord(null);
		assertEquals(0, DICT.size());
		DICT.insertWord("   	");
		assertEquals(0, DICT.size());
		DICT.insertWord("");
		assertEquals(1, DICT.size());
	}
	
	@Test
	void insertOnlyOneLetter() {
		LexicographicTree DICT = new LexicographicTree();
		DICT.insertWord("a");
		DICT.insertWord("y");
		DICT.insertWord("b");
		DICT.insertWord("-");
		DICT.insertWord("'");
		assertEquals(5, DICT.size());
	}
	
	@Test
	void searchWordLengthError() {
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(-3).toArray());
		assertArrayEquals(new String[] {}, DICT.getWordsOfLength(0).toArray());
	}
	
	// Constructor
		@Test
		void constructorBadFile(){
			// Given
			LexicographicTree dicoBad;
			
			// When
			dicoBad = new LexicographicTree("nope");
			
			// Then
			assertEquals(0, dicoBad.size());
		}
		
		@Test
		void constructorEmptyFile() {
			// Given
			LexicographicTree dico;
			
			// When
			dico = new LexicographicTree("mots/empty.txt");
			
			// Then
			assertEquals(0, dico.size());
			
		}
		
		// InsertWord
		@Test
		void insertWordNormal() {
			// Given
			LexicographicTree dict = new LexicographicTree();
			List<String> words = new ArrayList<>();
			String word = "HELLO";
			
			// When
			dict.insertWord(word);
			words.add(word);
					
			// Then
			assertEquals(1, dict.size());
			assertEquals(words, dict.getWords(""));
		}
		
		@Test
		void insertWordNormalWithNumbers() {
			// Given
			LexicographicTree dict = new LexicographicTree();
			List<String> words = new ArrayList<>();
			String word = "HELLO";
			String badWord = "hel15lo";
			
			// When
			dict.insertWord(badWord);
			words.add(word);
			
			// Then
			assertEquals(1, dict.size());
			assertEquals(words, dict.getWords(""));
		}
		
		@Test
		void insertWordNormalWithSpecialCharacters() {
			// Given
			LexicographicTree dict = new LexicographicTree();
			List<String> words = new ArrayList<>();
			String word = "HELLO";
			String badWord = "he^^ll$o";
			
			// When
			dict.insertWord(badWord);
			words.add(word);
			
			// Then
			assertEquals(1, dict.size());
			assertEquals(words, dict.getWords(""));
		}
		
		// GetWords
		@Test
		void getWordsOfNulLength() {
			assertEquals(0, DICT.getWordsOfLength(0).size());	
		}
		
		@Test
		void getWordsOfNegativeLength() {
			assertEquals(0, DICT.getWordsOfLength(-5).size());	
		}
		
		@Test
		void getWordsOfTooHighLength() {
			assertEquals(0, DICT.getWordsOfLength(35).size());
		}
		
		
		
		@Test
		void getWordsInAlphabeticalOrdrerByPrefix() {
			// Given
			LexicographicTree dict = new LexicographicTree();
			List<String> words = new ArrayList<>();
			String word2 = "HELLO";
			String word3 = "NOPE";
			String word4 = "OUKILEST";
			String word1 = "AZERBAIJAN";
			
			// When
			words.add(word1);
			words.add(word2);
			words.add(word3);
			words.add(word4);
			
			dict.insertWord(word4);
			dict.insertWord(word3);
			dict.insertWord(word1);
			dict.insertWord(word2);
			
			// Then
			assertEquals(words, dict.getWords(""));
		}
		
		@Test
		void getWordsInAlphabeticalOrdrerByPrefixNull() {
			// Given
			LexicographicTree dict = new LexicographicTree();
			List<String> words = new ArrayList<>();
			String word2 = "HELLO";
			String word3 = "NOPE";
			String word4 = "OUKILEST";
			String word1 = "AZERBAIJAN";
			
			// When
			words.add(word1);
			words.add(word2);
			words.add(word3);
			words.add(word4);
			
			dict.insertWord(word4);
			dict.insertWord(word3);
			dict.insertWord(word1);
			dict.insertWord(word2);
			
			// Then
			assertEquals(words, dict.getWords(null));
		}
		
		@Test
		void getWordsInAlphabeticalOrdrerByLength() {
			// Given
			LexicographicTree dict = new LexicographicTree();
			List<String> words = new ArrayList<>();
			String word2 = "HELLO";
			String word3 = "NOPE";
			String word4 = "OUKILEST";
			String word1 = "AZERBAIJAN";
			
			// When
			words.add(word2);
			
			dict.insertWord(word4);
			dict.insertWord(word3);
			dict.insertWord(word1);
			dict.insertWord(word2);
			
			// Then
			assertEquals(words, dict.getWordsOfLength(5));
		}

}
