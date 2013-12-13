import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import process.TokenProcessor;
import process.Vocabulary;


public class ConvertForJahmm {
	public static void main(String[] args) throws IOException {
		Vocabulary v = new Vocabulary();
		v.debug = false;
		v.readDictionary("/data/onco_pos/smaller/vocab.txt.thres1");
		String inputFile = "/data/onco_pos/smaller/pos_ul.test.notag";
		PrintWriter pw = new PrintWriter(inputFile + ".jahmm");
		PrintWriter pwProcessed = new PrintWriter(inputFile + ".jahmm.processed");
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line = "";
		while( (line = br.readLine()) != null) {
			line = line.trim();
			String[] words = line.split("\\s+");
			for(int i=0; i<words.length; i++) {
				String word = words[i];
				String smoothedWordLower = TokenProcessor.getSmoothedWord(word.toLowerCase());
				pw.print(v.getIndex(smoothedWordLower) + ";");
				if(i == words.length - 1) {
					pw.println();
				}
			}
			if(words.length > 2) {
				for(int i=0; i<words.length; i++) {
					String word = words[i];
					String smoothedWordLower = TokenProcessor.getSmoothedWord(word.toLowerCase());
					pwProcessed.print(v.getIndex(smoothedWordLower) + ";");
					if(i == words.length - 1) {
						pwProcessed.println();
					}
				}
			}
		}
		br.close();
		pw.close();
		pwProcessed.close();
		System.out.println("Done");
	}
}
