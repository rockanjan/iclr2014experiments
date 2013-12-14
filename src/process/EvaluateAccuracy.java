package process;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class EvaluateAccuracy {
	
	public static void main(String[] args) throws IOException {
		Vocabulary v = new Vocabulary();
		v.debug = false;
		//v.readDictionary("/data/onco_pos/vocab.txt.thres0.no_lower.no_smooth");
		v.readDictionary("/data/onco_pos/vocab.txt.thres0.no_lower.no_smooth");
		boolean lower = false;
		boolean smooth = false; //smooth before checking the vocab
		
		String filename = "/data/onco_pos/fhmm/test.rep.basic.new.f2";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int total = 0;
		int correct = 0;
		int unkTotal = 0;
		int unkCorrect = 0;
		
		int colLength = -1;
		while( (line = br.readLine()) != null ) {
			if(! line.trim().isEmpty() ) {
				total++;
				String[] tokens = line.split("\\s+");
				if(colLength == -1) { //first row
					colLength = tokens.length;
				}
				if(tokens.length != colLength) {
					System.err.println("WARNING: Column length mismatch. First found : " + colLength + " new found " + tokens.length);
					System.err.println("Line = " + line);
				}
				String gold = tokens[tokens.length - 2];
				String pred = tokens[tokens.length - 1];
				String word = tokens[0];
				if(lower) {
					word = word.toLowerCase();
				}
				if(smooth) {
					word = TokenProcessor.getSmoothedWord(word);
				}
				if(v.getIndex(word) == 0) {
					//System.out.println(word);
					unkTotal++;
				}
				if(gold.equalsIgnoreCase(pred)) {
					correct ++;
					if(v.getIndex(word) == 0) {
						unkCorrect++;
					}
				}
			}
		}
		System.out.println("correct : " + correct);
		System.out.println("Total : " + total);
		
		System.out.println("unkCorrect : " + unkCorrect);
		System.out.println("unkTotal : " + unkTotal);
		double accuracy = (100.0 * correct / total);
		double unkAccuracy = (100.0 * unkCorrect / unkTotal);
		System.out.format("Accuracy = %.2f\n", accuracy);
		System.out.format("unkAcc = %.2f\n", unkAccuracy);
		System.out.format("Error = %.2f\n", (100-accuracy));
		System.out.format("Unkerror = %.2f\n", (100-unkAccuracy));
		
		br.close();
	}
}
