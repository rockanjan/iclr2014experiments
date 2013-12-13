package process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ConvertToIntegerSequenceUsingVocabSPLForJahmm {
	public static void main(String[] args) throws IOException{

		if(args.length != 3) {
			System.err.println("Usage: <convert> vocabFile inputFile outputFile");
			System.exit(1);
		}
		String vocabFile = args[0];
		String inputFile = args[1];
		String outputFile = args[2];
		
		Vocabulary v = new Vocabulary();
		v.debug = false;
		v.readDictionary(vocabFile);
		
		System.out.println("Vocab size: " + v.vocabSize);
		//start processing
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		PrintWriter pw = new PrintWriter(outputFile);
		String line = "";
		while( (line = br.readLine()) != null ){
			line = line.trim().toLowerCase();
			if(! line.equals("")){
				String[] splitted = line.split("(\\s+|\\t+)");
				for(String word : splitted) {
					word = TokenProcessor.getSmoothedWord(word);
					Integer index = v.getIndex(word);
					if(index == null){
						System.err.println("WARNING: Vocab Index reader returned null index");
					}
					pw.print(index + ";");
				}
				pw.println();
				
			} else {
				//pw.println();
			}
			pw.flush();
		}
		br.close();
		pw.close();
	}
}
