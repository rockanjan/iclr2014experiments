package process;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import representation.BrownClusterRepresentation;


/*
 * takes one line sentence and next line tags, creates format for crf to process (conll format with extracted features)
 */
public class ConvertToConllFormatUsingBrown {
	//to extract features as described by Ratnaparkhi
	public static void main(String[] args) throws IOException {
		String brownFilename = "/home/anjan/src/brown-cluster/wsj_biomed_all.notag.uniq-c1000-p1.out/paths";
		BrownClusterRepresentation.createBrownClusterMap(brownFilename);
		
		Vocabulary v = new Vocabulary();
		v.readDictionary("/data/onco_pos/vocab.txt.thres0");
		String filename = "/data/onco_pos/brown/onco_test.561";
		String outFilename = filename + ".conll.brown";
		
		PrintWriter pw = new PrintWriter(outFilename);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = "";
		int totalTokens = 0;
		int totalUnknowns = 0;
		int totalBrownUnknown = 0;
		while( (line = br.readLine()) != null) {
			String[] words = line.split("\\s+");
			line = br.readLine(); //tags
			String[] tags = line.split("\\s+");
			//assert they are the same length
			int length = words.length;
			if(length != tags.length) {
				pw.close();
				br.close();
				throw new RuntimeException("Word and Tag length do not match");				
			}
			//start extracting features and writing to the file
			for(int i=0; i<length; i++) {
				totalTokens++;
				String word = words[i];
				String tag = tags[i];
				String smoothedWord = TokenProcessor.getSmoothedWord(word);
				String lower = smoothedWord.toLowerCase();
				//prefix defaults
				String[] prefix = new String[4];
				for(int p=0; p<4; p++) {
					prefix[p] = "_NA_";
				}
				//suffix defaults
				String[] suffix = new String[4];
				for(int s=0; s<4; s++) {
					suffix[s] = "_NA_";
				}
				
				if(lower.equals("<num>")) {
					for(int p=0; p<4; p++) {
						suffix[p] = "<num>";
						prefix[p] = "<num>";
					}
				}
				else { 
					if(! lower.startsWith("<num>")) {
					prefix[0] = lower.substring(0, 1);
					if(lower.length() > 1) {
						prefix[0] = prefix[0] + "_"; //to indicate there is something after the prefix0
						prefix[1] = lower.substring(0,2);
						if(lower.length() > 2) {
							prefix[1] = prefix[1] + "_";
							prefix[2] = lower.substring(0,3);
							if(lower.length() > 3) {
								prefix[2] = prefix[2] + "_";
								prefix[3] = lower.substring(0,4);
								if(lower.length() > 4) {
									prefix[3] = prefix[3] + "_";
								}
							}
						}					
					}
					}
					if(!lower.endsWith("<num>")) {
						suffix[0] = lower.substring(lower.length()-1, lower.length());
						if(lower.length() > 1) {
							suffix[0] = "_" + suffix[0];
							suffix[1] = lower.substring(lower.length()-2, lower.length());
							if(lower.length() > 2) {
								suffix[1] = "_" + suffix[1];
								suffix[2] = lower.substring(lower.length()-3, lower.length());
								if(lower.length() > 3) {
									suffix[2] = "_" + suffix[2];
									suffix[3] = lower.substring(lower.length()-4, lower.length());
									if(lower.length() > 4) {
										suffix[3] = "_" + suffix[3];
									}
								}
							}
						}
					}
				}
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
				if(i == 0) {
					upper = upper + "0";
				}		
				
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
				
				//write
				pw.println(word + spaces(15 - word.length()) + 
						smoothedWord.toLowerCase() + spaces(15 - smoothedWord.length()) +
						prefix[0] + spaces(5-prefix[0].length()) +
						prefix[1] + spaces(5-prefix[1].length()) +
						prefix[2] + spaces(5-prefix[2].length()) +
						prefix[3] + spaces(5-prefix[3].length()) +
						
						suffix[0] + spaces(5-suffix[0].length()) +
						suffix[1] + spaces(5-suffix[1].length()) +
						suffix[2] + spaces(5-suffix[2].length()) +
						suffix[3] + spaces(5-suffix[3].length()) +
						
						containsNumber + " " +
						upper + " " +
						containsHyphen + " " +
						brownRep + " " +
						tag
						);
				
			}
			pw.println();
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
