package process;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateRepresentationTemplateFHmm {
	/*
	0. word
	smoothedWord
	prefix1
	prefix2
	prefix3
	prefix4
	suffix1
	suffix2
	suffix3
	suffix4
	10. hasNumber
	11. hasUpper
	hasHyphen
	rep1
	...
	repn
	label;
	*/
	
	//extract features as described by Ratnaparkhi
	public static void main(String[] args) throws FileNotFoundException {
		int REP_LENGTH = 1;
		String templateFile = "/data/onco_pos/brown/representation.template.withcontext";
		StringBuffer content = new StringBuffer();
		int featureIndex = 0;
		//smoothed word upto hasHyphen
		for(int i=1; i<=12; i++) {
			content.append(String.format("U%d:%%x[0,%d]\n", featureIndex, i));
			featureIndex++;
		}
		//context words
		for(int i=-2; i<=2; i++) {
			if(i != 0) { //already included above
				content.append(String.format("U%d:%%x[%d,1]\n", featureIndex, i));
				featureIndex++;
			}
		}
		/*
		//prev + next words
		content.append(String.format("U%d:%%x[%d,1]/%%x[%d,1]\n", featureIndex, -1, 1));
		featureIndex++;
		*/
		
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			//unigram of rep
			for(int i=-1; i<=1; i++) {
			//for(int i=0; i<=0; i++) {
				content.append(String.format("U%d:%%x[%d,%d]\n", featureIndex, i, (13+d)));
				featureIndex++;
			}
		}
		
		/*
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			//bigrams
			for(int i=0; i<=0; i++) {
				content.append(String.format("U%d:%%x[%d,%d]/%%x[%d,%d]\n", featureIndex, i-1, (13+d), i, (13+d)));
				featureIndex++;
				content.append(String.format("U%d:%%x[%d,%d]/%%x[%d,%d]\n", featureIndex, i, (13+d), i+1, (13+d)));
				featureIndex++;
			}
		}
		*/
		
		/*
		//my : combine representation with suffixes
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			for(int i=0; i<4; i++) {
				content.append(String.format("U%d:%%x[%d,%d]/%%x[%d,%d]\n", featureIndex, 0, (13+d), 0, (6+i)));
				featureIndex++;
			}
		}
		
		
		//my : combine representation with capital
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			content.append(String.format("U%d:%%x[%d,%d]/%%x[%d,%d]\n", featureIndex, 0, (13+d), 0, (11)));
			featureIndex++;
		}
		
		
		//my : combine representation with capital and 1 and 2 suffix
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			content.append(String.format("U%d:%%x[%d,%d]/%%x[%d,%d]/%%x[0,6]\n", featureIndex, 0, (13+d), 0, (11)));
			featureIndex++;
			content.append(String.format("U%d:%%x[%d,%d]/%%x[%d,%d]/%%x[0,7]\n", featureIndex, 0, (13+d), 0, (11)));
			featureIndex++;
		}
		*/
		
		//bigram
		int bigramFeatureIndex = 0;
		content.append(String.format("B%d\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		
		
		System.out.println(content.toString());
		PrintWriter pw = new PrintWriter(templateFile);
		pw.println(content.toString());
		pw.close();
		
	}
}
