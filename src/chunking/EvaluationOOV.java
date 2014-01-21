package chunking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import process.TokenProcessor;
import process.Vocabulary;

/*
 * Evaluates the precision/recall and F1 score for 
 * NP-chunks for file given in conll format ending with gold and pred
 * For OOV which are not in a dictionary
 * 
 * 
 * tp = exact boundary match for the phrases that begin with OOV
 * fp = predicted phrases that begin with OOV, but are actually not phrases in the gold
 * tp + fn = total phrases in the gold
 */

public class EvaluationOOV {
	// stores arraylist of all the hash maps of arguments
	// hashmap stored in format <linenumber, argLength>
	static boolean debug = false;

	public static void main(String... args) throws IOException {
		Vocabulary v = new Vocabulary();
		
		v.readDictionary("/data/iclr/chunking/preprocess_files/vocab.txt.thres2"); //test has only lowercase characters
		String testFile = "/data/iclr/chunking/hmmnew/result/test.rep";
		
		v.debug = false;
		boolean lower = true;
		boolean smooth = true; //smooth before checking the vocab
		
		
		//String testFile = "/data/iclr/chunking/preprocess_files/sanitycheck/oanc_test.baseline.gold.gold";
		
		BufferedReader br = new BufferedReader(new FileReader(testFile));

		HashMap<Integer, Integer> goldChunks = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> predChunks = new HashMap<Integer, Integer>();

		int tp = 0, fn = 0, fp = 0;

		String line;
		int lineNumber = 0;
		int argLengthGold = 0;
		int argLengthPred = 0;

		String goldBio;
		String predBio;
		boolean goldCont = false; // is the phrase continuing?
		boolean predCont = false;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			line = line.trim();

			if (! line.equals("")) {
				String[] splitted = line.split("(\\s+|\\t+)");
				goldBio = splitted[splitted.length - 2];
				predBio = splitted[splitted.length - 1];

				String word = splitted[0]; //original word
				if(lower) {
					word = word.toLowerCase();
				}
				if(smooth) {
					word = TokenProcessor.getSmoothedWord(word);
				}
				int vocabIndex = v.getIndex(word);
				// Process gold: 3 cases, B, I or O
				if (goldBio.equals("B-NP") && vocabIndex == 0) {
					if (goldCont) {
						// either previous B or I
						int startLineGold = lineNumber - argLengthGold;
						goldChunks.put(startLineGold, argLengthGold);
					}
					argLengthGold = 1;
					goldCont = true;
				} else if (goldBio.equals("I-NP")) {
					if(goldCont) {
						argLengthGold++;
					}					
				} else if (goldBio.equals("O")) {
					if (goldCont) {
						// put the argument into hashmap
						int startLine = lineNumber - argLengthGold;
						goldChunks.put(startLine, argLengthGold);
						// reset
						argLengthGold = 0;
						goldCont = false;
					}
				}
				
				//Process pred: 3 cases, BIO
				if (predBio.equals("B-NP") && vocabIndex == 0) {
					if (predCont) {
						// either previous B or I
						int startLinePred = lineNumber - argLengthPred;
						predChunks.put(startLinePred, argLengthPred);
					}
					argLengthPred = 1;
					predCont = true;
				} else if (predBio.equals("I-NP")) {
					if(predCont) {
						argLengthPred++;
					}
				} else if (predBio.equals("O")) {
					if (predCont) {
						// put the argument into hashmap
						int startLine = lineNumber - argLengthPred;
						predChunks.put(startLine, argLengthPred);
						// reset
						argLengthPred = 0;
						predCont = false;
					}
				}
			} else { // empty line
				if (goldCont) {
					int startLine = lineNumber - argLengthGold;
					goldChunks.put(startLine, argLengthGold);
					goldCont = false;
					argLengthGold = 0;
				}
				
				if (predCont) {
					int startLine = lineNumber - argLengthPred;
					predChunks.put(startLine, argLengthPred);
					predCont = false;
					argLengthPred = 0;
				}
			}
		}
		// if reaches here, it might be because of B I I and termination
		if (goldCont) {
			int startLineGold = lineNumber - argLengthGold + 1;
			goldChunks.put(startLineGold, argLengthGold);
		}
		
		if (predCont) {
			int startLinePred = lineNumber - argLengthPred + 1;
			predChunks.put(startLinePred, argLengthPred);
		}

		/********* Evaluation ***************/
		Set<Map.Entry<Integer, Integer>> goldEntries = goldChunks.entrySet();
		for (Map.Entry<Integer, Integer> entry : goldEntries) {
			int key = entry.getKey();
			int value = entry.getValue();
			if (predChunks.containsKey(key)) {
				if (predChunks.get(key) == value) {
					// true positive
					tp++;
				} else {
					fn++;
				}
			} else {
				fn++;
			}
		}
		// for false positive
		Set<Map.Entry<Integer, Integer>> predEntries = predChunks.entrySet();
		for (Map.Entry<Integer, Integer> entry : predEntries) {
			int key = entry.getKey();
			int value = entry.getValue();
			if (!goldChunks.containsKey(key)) {
				fp++;
			} else {
				if (goldChunks.get(key) != value) {
					fp++;
				}
			}
			// else it's tp, already handled
		}

		// finally, F1 score
		System.out.println("True Positives: " + tp);
		System.out.println("False Positives: " + fp);
		System.out.println("False Negatives: " + fn);
		double precision = 1.0 * tp / (tp + fp);
		int total = tp + fn;
		System.out.println("Total : " + total);
		double recall = 1.0 * tp / (tp + fn);
		
		System.out.format("Precision: %.2f\n", precision);
		System.out.format("Recall: %.2f\n", recall);
		double f1 = 2 * precision * recall / (precision + recall);
		System.out.format("F1 score : %.2f\n", f1);

		br.close();
	}
}
