package chunking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Evaluates the precision/recall and F1 score for 
 * NP-chunks for file given in conll format ending with gold and pred
 * 
 */

public class Evaluation {
	// stores arraylist of all the hash maps of arguments
	// hashmap stored in format <linenumber, argLength>
	static boolean debug = false;

	public static void main(String... args) throws IOException {
		String testFile = "/data/iclr/chunking/hmmnew/result/test.rep";
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

				// Process gold: 3 cases, B, I or O
				if (goldBio.equals("B-NP")) {
					if (goldCont) {
						// either previous B or I
						int startLineGold = lineNumber - argLengthGold;
						goldChunks.put(startLineGold, argLengthGold);
					}
					argLengthGold = 1;
					goldCont = true;
				} else if (goldBio.equals("I-NP")) {
					argLengthGold++;
					if (!goldCont) {
						System.err.println("GOLD: I without preceding B at line " + lineNumber);
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
				} else {
					System.err.println("Processing Gold: Unexpected label in target at line " + lineNumber + " = " + goldBio);
					System.exit(-1);
				}
				
				//Process pred: 3 cases, BIO
				
				
				if (predBio.equals("B-NP")) {
					if (predCont) {
						// either previous B or I
						int startLinePred = lineNumber - argLengthPred;
						predChunks.put(startLinePred, argLengthPred);
					}
					argLengthPred = 1;
					predCont = true;
				} else if (predBio.equals("I-NP")) {
					argLengthPred++;
					if (!predCont) {
						System.err.println("Pred: I without preceding B at line "+ lineNumber + "=" + predBio);
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
				} else {
					System.err.println("Processing Pred: Unexpected label in target at line " + lineNumber);
					System.exit(-1);
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
