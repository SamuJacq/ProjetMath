package cryptanalysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;

import tree.LexicographicTree;


public class DictionaryBasedAnalysisTest {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String CRYPTOGRAM_FILE = "txt/Plus fort que Sherlock Holmes (cryptogram).txt";
	private static final String ENCODING_ALPHABET = "YESUMZRWFNVHOBJTGPCDLAIXQK"; // Sherlock
	private static final String DECODING_ALPHABET = "VNSTBIQLWOZUEJMRYGCPDKHXAF"; // Sherlock
	private static LexicographicTree dictionary = null;

	@BeforeAll
	private static void initTestDictionary() {
		dictionary = new LexicographicTree("mots/dictionnaire_FR_avec_accents.txt");
	}
	
	@Test
	void applySubstitutionTest() {
		String message = "DEMANDE RENFORTS IMMEDIATEMENT";
		String encoded = "UMOYBUM PMBZJPDC FOOMUFYDMOMBD";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}

	@Test
	void updateAlphabetTest() {
		String cryptogramWord1 = "SJBCSFMBSFMLCMOMBD";
		String candidateWord1  = "CONSCIENCIEUSEMENT";
		String expected1 = "ANSTJIGHFOKUEBMPQRCDLVWXYZ";
		assertEquals(expected1, DictionaryBasedAnalysis.updateAlphabet(LETTERS, cryptogramWord1, candidateWord1));

		String cryptogramWord2 = "SJBZFUMBDFMHHMOMBD";
		String candidateWord2 = "CONFIDENTIELLEMENT";
		String expected2 = "ANSTJIGLZOKUEBMPQRCHDVWXYF";
		assertEquals(expected2, DictionaryBasedAnalysis.updateAlphabet(expected1, cryptogramWord2, candidateWord2));
	}

	@Test
	void guessApproximatedAlphabetTest() {
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		assertNotNull(dba);
		String alphabet = dba.guessApproximatedAlphabet(LETTERS);
		int score = 0;
		for (int i = 0; i < DECODING_ALPHABET.length(); i++) {
			if (DECODING_ALPHABET.charAt(i) == alphabet.charAt(i)) score++;
		}
		assertTrue(score >= 9, "Moins de 9 correspondances trouvées [" + score + "]");
	}
	
	private static String readFile(String pathname, Charset encoding) {
		String data = "";
		try {
			data = Files.readString(Paths.get(pathname), encoding);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	@Test
	void guessApproximatedAlphabetShortAlphabet() {
		// Given
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		String shortAlphabet = "AZERTYUIOP";
		
		// Then
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet(shortAlphabet));
	}
	
	@Test
	void guessApproximatedAlphabetSpeciaCharsAlphabet() {
		// Given
		String cryptogram = readFile(CRYPTOGRAM_FILE, StandardCharsets.UTF_8);
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		String shortAlphabet = "ABCDEFGHIJKLMNOPQRST^^158Z";
		
		// Then
		assertThrows(IllegalArgumentException.class, () -> dba.guessApproximatedAlphabet(shortAlphabet));
	}
	
	@Test
	void guessApproximatedAlphabet() {
		// Given
		String cryptogram = "JE SUIS DU TEXTE";
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dictionary);
		// Then
		assertEquals(LETTERS, dba.guessApproximatedAlphabet(LETTERS));
	}
	
	@Test
	void guessApproximatedOneWord() {
		// Given
		String cryptogram = "SJBCSFMBSFMLCMOMBD";
		LexicographicTree dict = new LexicographicTree();
		dict.insertWord("CONSCIENCIEUSEMENT");
		DictionaryBasedAnalysis dba = new DictionaryBasedAnalysis(cryptogram, dict);
		// Then
		assertEquals("ANSTJIGHFOKUEBMPQRCDLVWXYZ", dba.guessApproximatedAlphabet(LETTERS));
	}
	
	
	// Substitution
	
	@Test
	void applySubstitutionTestOnArtiste() {
		String message = "ARTISTE";
		String encoded = "YPDFCDM";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, ENCODING_ALPHABET));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, DECODING_ALPHABET));
	}
	
	@Test
	void applySubstitutionAllClaire() {
		String message = "ARTISTE";
		String encoded = "ARTISTE";
		assertEquals(encoded, DictionaryBasedAnalysis.applySubstitution(message, LETTERS));
		assertEquals(message, DictionaryBasedAnalysis.applySubstitution(encoded, LETTERS));
	}
	
	@Test
	void applySubstitutionTestShortAlphabet() {
		String message = "ARTISTE";
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution(message, "AZERTYUIOP"));
	}
	
	@Test
	void applySubstitutionTestBadAlphabet() {
		String message = "ARTISTE";
		assertThrows(IllegalArgumentException.class, () -> DictionaryBasedAnalysis.applySubstitution(message, "ABCDEFGHIJKLMNOPQRST^^158Z"));
	} 
	
	// Update
	
	@Test
	void updateAlphabetNonCrypte() {
		String cryptogramWord1 = "CONSCIENCIEUSEMENT";
		String candidateWord1  = "CONSCIENCIEUSEMENT";
		String expected1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		assertEquals(expected1, DictionaryBasedAnalysis.updateAlphabet(LETTERS, cryptogramWord1, candidateWord1));

	}
	
}
