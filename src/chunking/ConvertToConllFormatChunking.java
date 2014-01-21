package chunking;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import process.TokenProcessor;
import process.Vocabulary;


/*
 * takes file in following format
 * word	pos	chunkTag	rep1	rep2 ...
 */
public class ConvertToConllFormatChunking {
	//to extract features as described by Ratnaparkhi
	public static void main(String[] args) throws IOException {
		boolean convertToNPChunking = true;
		
		Vocabulary	v = new Vocabulary();
		//with thres 1, we can allow few word in the training to also be OOV
		v.readDictionary("/data/iclr/chunking/preprocess_files/vocab.txt.thres0");
		
		String filename = "/data/iclr/chunking/hmmnew/preprocess/train.hmm";
		String outFilename = filename + ".conll";
		
		PrintWriter pw = new PrintWriter(outFilename);
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = "";
		int totalTokens = 0;
		int totalUnknowns = 0;
		while( (line = br.readLine()) != null) {
			line = line.trim();
			if(line.isEmpty()) {
				pw.println();
				continue;
			}
			totalTokens++;
			String[] splitted = line.split("\\s+");
			String word = splitted[0];
			String pos = splitted[1];
			String tag = splitted[2];
			
			String[] rep = new String[splitted.length - 3];
			for(int i=0; i<rep.length; i++) {
				rep[i] = splitted[3+i];
			}
			
			String smoothedWord = TokenProcessor.getSmoothedWord(word);
			String lower = smoothedWord.toLowerCase();
				
			String suffix = TokenProcessor.suffixesOrthographic(lower);
			String containsNumber = "N";
			//contains number?
			if(smoothedWord.contains("_NUM_") || smoothedWord.contains("<num>")) {
				containsNumber = "Y";
			}
			String containsHyphen = "N";
			if(word.contains("-")) {
				containsHyphen = "Y";
			}
			
			String smoothedLower = smoothedWord.toLowerCase(); 
			if(v.getIndex(smoothedLower) == 0) {
				smoothedLower = "*unk*";
			}
			StringBuffer sb = new StringBuffer();
			sb.append(word + spaces(15 - word.length()));
			sb.append(smoothedLower + spaces(15 - smoothedLower.length()));
			sb.append(pos + spaces(5 - pos.length()));
			sb.append(suffix + spaces(5 - suffix.length()));
			sb.append(containsNumber + spaces(5 - containsNumber.length()));
			//sb.append(upper + spaces(5 - upper.length())); //test data has all lowercase tokens
			sb.append(containsHyphen + spaces(5 - containsHyphen.length()));
			
			for(int i=0; i<rep.length; i++) {
				sb.append(rep[i] + spaces(5 - rep[i].length()));
			}
			
			if(convertToNPChunking) {
				if(! tag.contains("-NP")) {
					tag = "O";
				}
			}
			sb.append(tag);
			
			//write
			pw.println(sb.toString());
			pw.flush();
		}
		br.close();
		pw.close();
		System.out.println("Total tokens = " + totalTokens);
	}
	
	public static String spaces(int size) {
		String returnString = "";
		for (int i=0; i<size; i++) {
			returnString += " ";
		}
		return returnString + " ";
	}
}
