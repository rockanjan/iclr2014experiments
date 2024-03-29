package process;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


/*
 * takes one line sentence and next line tags, creates format for crf to process (conll format with extracted features)
 */
public class ConvertToConllFormatOrthographic {
	//to extract features as described by Huang and Yates
	public static void main(String[] args) throws IOException {
		int SPACESIZE = 10;
		Vocabulary v = new Vocabulary();
		v.readDictionary("/data/onco_pos/vocab.txt.thres0");
		String filename = "/data/onco_pos/new/train.40k";
		String outFilename = filename + ".conll.ortho";
		
		PrintWriter pw = new PrintWriter(outFilename);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = "";
		int totalTokens = 0;
		int totalUnknowns = 0;
		while( (line = br.readLine()) != null) {
			String[] words = line.split("\\s+");
			line = br.readLine(); //tags
			String[] tags = line.split("\\s+");
			//assert they are the same length
			int length = words.length;
			if(length != tags.length) {
				br.close();
				pw.close();
				throw new RuntimeException("Word and Tag length do not match");
			}
			//start extracting features and writing to the file
			for(int i=0; i<length; i++) {
				totalTokens++;
				String word = words[i];
				String tag = tags[i].toUpperCase();
				String smoothedWord = TokenProcessor.getSmoothedWord(word);
				String lower = smoothedWord.toLowerCase();
				
				String prefix = TokenProcessor.prefixesOrthographic(lower);
				String suffix = TokenProcessor.suffixesOrthographic(lower);
				
				String caps = TokenProcessor.getCapitalType(word);
				if(i==0) {
					caps = caps + "BOS"; //BOS
				}
				
				String alphaNum = TokenProcessor.getAlphaNumericType(smoothedWord);
				
				smoothedWord = smoothedWord.toLowerCase(); 
				if(v.getIndex(smoothedWord) == 0) {
					smoothedWord = "*unk*";
					totalUnknowns++;
				}
				
				//write
				pw.println(word + spaces(15 - word.length()) + 
						smoothedWord.toLowerCase() + spaces(15 - smoothedWord.length()) +
						prefix + spaces(SPACESIZE - prefix.length()) +
						suffix + spaces(SPACESIZE - suffix.length()) +
						caps + spaces(SPACESIZE - caps.length()) +
						alphaNum + spaces(SPACESIZE - alphaNum.length()) +
						tag
						);
				
			}
			pw.println();
			pw.flush();
		}
		br.close();
		pw.close();
		System.out.println("Total tokens = " + totalTokens + " Total Unknowns = " + totalUnknowns + " frac = " + (1.0 * totalUnknowns/totalTokens));
	}
	
	public static String spaces(int size) {
		String returnString = "";
		for (int i=0; i<size; i++) {
			returnString += " ";
		}
		return returnString + " ";
	}
}
