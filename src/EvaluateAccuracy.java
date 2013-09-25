import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class EvaluateAccuracy {
	
	public static void main(String[] args) throws IOException {
		String filename = "/home/anjan/src/sgd/crf/onco_test.561.conll.gold.pred.new";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line;
		int total = 0;
		int correct = 0;
		while( (line = br.readLine()) != null ) {
			if(! line.trim().isEmpty() ) {
				String[] tokens = line.split("\\s+");
				String gold = tokens[tokens.length - 2];
				String pred = tokens[tokens.length - 1];
				if(gold.equalsIgnoreCase(pred)) {
					correct ++;
				}
				total++;
			}
		}
		System.out.println("correct : " + correct);
		System.out.println("Total : " + total);
		System.out.println("Accuracy = " + (1.0 * correct / total));
		
	}
}
