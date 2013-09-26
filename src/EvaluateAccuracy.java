import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class EvaluateAccuracy {
	
	public static void main(String[] args) throws IOException {
		Vocabulary v = new Vocabulary();
		v.debug = false;
		v.readDictionary("/data/onco_pos/all/vocab.txt.thres0");
		
		boolean smooth = false; //smooth before checking the vocab
		boolean includeNum = true; //this does not matter if smooth=false
		String filename = "/home/anjan/src/sgd/crf/test.c5";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int total = 0;
		int correct = 0;
		int unkTotal = 0;
		int unkCorrect = 0;
		while( (line = br.readLine()) != null ) {
			if(! line.trim().isEmpty() ) {
				String[] tokens = line.split("\\s+");
				String gold = tokens[tokens.length - 2];
				String pred = tokens[tokens.length - 1];
				String word = tokens[0].toLowerCase();
				if(smooth) {
					word = TokenProcessor.getSmoothedWord(word);
				}
				if(v.getIndex(word) == 0) {
					//System.out.println(word);
					unkTotal++;
				} else if(includeNum && word.contains("<num>")) {
					unkTotal++;
				}
				if(gold.equalsIgnoreCase(pred)) {
					correct ++;
					if(v.getIndex(word) == 0) {
						unkCorrect++;
					} else if(includeNum && word.contains("<num>")) {
						unkCorrect++;
					}
				}
				total++;
			}
		}
		/*
		System.out.println("correct : " + correct);
		System.out.println("Total : " + total);
		
		System.out.println("unkCorrect : " + unkCorrect);
		System.out.println("unkTotal : " + unkTotal);
		*/
		System.out.format("Accuracy = %.2f\n", (100.0 * correct / total));
		System.out.format("unkAcc = %.2f\n", (100.0 * unkCorrect / unkTotal));
		br.close();
	}
}
