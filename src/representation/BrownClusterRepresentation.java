package representation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import process.TokenProcessor;

public class BrownClusterRepresentation {
	public static Map<String, String> brownClusterWordToRepresentation;
	public static Map<String, String> brownClusterWordToRepresentationSmoothed;

	public static void main(String args[]) throws IOException {
		String filename = "brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt";
		createBrownClusterMap(filename);
	}

	public static void createBrownClusterMap(String filename) throws IOException {
		brownClusterWordToRepresentation = new HashMap<String, String>();
		brownClusterWordToRepresentationSmoothed = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				String splitted[] = line.split("(\\s+)|(\\t+)");
				if (splitted.length < 2) {
					throw new RuntimeException(
							"Error reading brown cluster representation on line : "
									+ line);
				}
				String rep = splitted[0];
				String word = splitted[1];
				brownClusterWordToRepresentation.put(word, rep);
				brownClusterWordToRepresentationSmoothed.put(TokenProcessor.getSmoothedWord(word), rep);
			}
		}
	}
	
	/*
	 * Pass the original word, will try in this order
	 * first: try finding using original word
	 * second: (when first fails), try using smoothed string 
	 */
	public static String getRepresentation(String word) {
		String defaultRep = "-1";
		String defaultNum = "-2"; //for numbers _NUM_
		if(brownClusterWordToRepresentation == null) {
			throw new RuntimeException("representation map not initialized");
		}
		String rep;
		rep = defaultRep;
		if(brownClusterWordToRepresentation.containsKey(word)) {
			rep = brownClusterWordToRepresentation.get(word);
		} else {
			word = TokenProcessor.getSmoothedWord(word);
			if(brownClusterWordToRepresentationSmoothed.containsKey(word)) {
				rep = brownClusterWordToRepresentationSmoothed.get(word);
			} else if(word.equalsIgnoreCase("_NUM_") || word.equalsIgnoreCase("<num>")) {//after smoothing
				rep = defaultNum;
			}
		}
		return rep;
	}
}
