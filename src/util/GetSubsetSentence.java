package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GetSubsetSentence {
	public static void main(String[] args) throws IOException {
		String inFile = "/data/onco_pos/fhmm20new/train.40k.fhmm.conll";
		int size = 10000; //how many subsets to select?
		String outFile = inFile + ".subset." + size;
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		PrintWriter pw = new PrintWriter(outFile);
		String line;
		int count = 0;
		while( (line = br.readLine()) != null) {
			line = line.trim();
			if(line.isEmpty()) {
				count++;
				if(count >= size) {
					pw.println();
					break;
				}
			}
			pw.println(line);
		}
		pw.close();
		br.close();
		
	}
}
