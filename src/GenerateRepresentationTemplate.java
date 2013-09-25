

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateRepresentationTemplate {
	/*
	word
	smoothedWord
	prefix1
	prefix2
	prefix3
	prefix4
	suffix1
	suffix2
	suffix3
	suffix4
	hasNumber
	hasUpper
	hasHyphen
	rep1
	...
	repn
	label;
	*/
	
	//extract features as described by Ratnaparkhi
	public static void main(String[] args) throws FileNotFoundException {
		int REP_LENGTH = 1;
		String templateFile = "/data/onco_pos/smaller/representation.template";
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
		
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			for(int i=0; i<0; i++) {
				content.append(String.format("U%d:%%x[%d,%d]\n", featureIndex, i, (13+d)));
				featureIndex++;
			}
		}
		//bigram
		int bigramFeatureIndex = 0;
		content.append(String.format("B%d\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		/*
		//my: bigram + word
		content.append(String.format("B%d:%%x[0,1]\n", bigramFeatureIndex));
		*/
		System.out.println(content.toString());
		PrintWriter pw = new PrintWriter(templateFile);
		pw.println(content.toString());
		pw.close();
		
	}
}
