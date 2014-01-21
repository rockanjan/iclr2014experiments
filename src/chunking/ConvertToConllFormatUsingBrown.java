package chunking;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import process.TokenProcessor;
import process.Vocabulary;

import representation.BrownClusterRepresentation;


/*
 * takes one line sentence and next line tags, creates format for crf to process (conll format with extracted features)
 */
public class ConvertToConllFormatUsingBrown {
	//to extract features as described by Ratnaparkhi
	public static void main(String[] args) throws IOException {
		boolean convertToNPChunking = true;
		String brownFilename = "/home/anjan/src/brown-cluster/wsj_biomed_all.notag.uniq-c1000-p1.out/paths";
		BrownClusterRepresentation.createBrownClusterMap(brownFilename);
		
		Vocabulary v = new Vocabulary();
		v.readDictionary("/data/iclr/chunking/preprocess_files/vocab.txt.thres0");
		String filename = "/data/iclr/chunking/brown/oanc_test.baseline";
		String outFilename = filename + ".conll.brown";
		
		PrintWriter pw = new PrintWriter(outFilename);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = "";
		int totalTokens = 0;
		int totalUnknowns = 0;
		int totalBrownUnknown = 0;
		while( (line = br.readLine()) != null) {
			if(line.trim().isEmpty()) {
				pw.println();
				continue;
			}
			totalTokens++;
			String[] splitted = line.split("\\s+");
			String word = splitted[0];
			String pos = splitted[1];
			String tag = splitted[2];
			String smoothedWord = TokenProcessor.getSmoothedWord(word);
			String lower = smoothedWord.toLowerCase();
			String suffix = TokenProcessor.suffixesOrthographic(lower);
			String containsNumber = "N";
			//contains number?
			if(smoothedWord.contains("_NUM_") || smoothedWord.contains("<num>")) {
				containsNumber = "Y";
			}
			/*
			String containsUpper = "N"; //if not the beginning of the word
			//if(i != 0 && TokenProcessor.hasCaps(word)) {
			if(TokenProcessor.hasCaps(word)) {
				containsUpper = "Y";
			}
			*/
			/*
			String upper = TokenProcessor.getCapitalType(word);
			*/
			String upper = TokenProcessor.hasFirstCapFollowedByaToz(word) ? "Y" : "N";
			
			String containsHyphen = "N";
			if(word.contains("-")) {
				containsHyphen = "Y";
			}
			
			smoothedWord = smoothedWord.toLowerCase(); 
			if(v.getIndex(smoothedWord) == 0) {
				smoothedWord = "*unk*";
				totalUnknowns++;
			}
			String brownRep = BrownClusterRepresentation.getRepresentation(word);
			if(brownRep.equals("-1")) {
				totalBrownUnknown++;
			}
			
			if(convertToNPChunking) {
				if(! tag.contains("-NP")) {
					tag = "O";
				}
			}
			//write
			pw.println(
					word + spaces(15 - word.length()) + 
					smoothedWord.toLowerCase() + spaces(15 - smoothedWord.length()) +
					pos + spaces(5-pos.length()) +
					suffix + spaces(5-suffix.length()) +
					containsNumber + " " +
					//upper + " " +
					containsHyphen + " " +
					brownRep + " " +
					tag
					);
		}
		br.close();
		pw.close();
		System.out.println("Total tokens = " + totalTokens + " Total Unknowns = " + totalUnknowns + " frac = " + (1.0 * totalUnknowns/totalTokens));
		System.out.println("Total brown unk = " + totalBrownUnknown + " frac = " + (1.0 * totalBrownUnknown / totalTokens));
	}
	
	public static String spaces(int size) {
		String returnString = "";
		for (int i=0; i<size; i++) {
			returnString += " ";
		}
		return returnString + " ";
	}
}
