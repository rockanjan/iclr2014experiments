package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class BinPosteriors {
	public static void main(String[] args) throws IOException {
		String inFile = "/data/iclr/chunking/fhmm_3_10_new/preprocess/oanc_test.baseline.SPL.decoded.posterior";
		int DIM = 50;// M * K
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		PrintWriter pw = new PrintWriter(inFile + ".binned");
		String line;
		while( (line = br.readLine()) != null) {
			line = line.trim();
			if(line.isEmpty()) {
				pw.println();
				continue;
			}
			StringBuffer sb = new StringBuffer();
			String[] splitted = line.split("\\|");
			String word = splitted[0].trim();
			//sb.append(word);
			for(int i=1; i<splitted.length; i++) {
				double rep = Double.parseDouble(splitted[i]);
				String repBinned = String.format("%.1f", rep);
				//String repBinned = splitted[i];
				sb.append(" " + repBinned);
			}
			pw.println(sb.toString());
			pw.flush();
		}
		pw.close();
		br.close();
	}
}
