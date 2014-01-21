package chunking;


import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GenerateRepresentationTemplateChunkingCombined {
	/*
	word
	smoothedWord
	POSTag
	suffixOrtho
	hasNumber
	hasHyphen
	rep1
	...
	repn
	label;
	*/
	
	//extract features as described by Ratnaparkhi
	public static void main(String[] args) throws FileNotFoundException {
		int REP_LENGTH = 3;
		String templateFile = "/data/iclr/chunking/fhmm20/representation.template.combined";
		StringBuffer content = new StringBuffer();
		int featureIndex = 0;
		//smoothed word upto hasHyphen
		for(int i=1; i<=5; i++) {
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
			for(int i=0; i<=0; i++) {
				content.append(String.format("U%d:%%x[%d,%d]\n", featureIndex, i, (6+d)));
				featureIndex++;
			}
		}
		

		//representation combined (eg. n choose 2 for pairs)
		int totalCombinedPairs = 0;
		for(int d=0; d<REP_LENGTH; d++) { //rep dimension
			for(int e=0; e<REP_LENGTH; e++) {
				if(d < e) {
					totalCombinedPairs++;
					content.append(String.format("U%d:%%x[0,%d]/%%x[0,%d]\n", featureIndex, (13+d), (13+e)));
					featureIndex++;
				}
			}
		}
		
		
		int bigramFeatureIndex = 0;
		content.append(String.format("B%d\n", bigramFeatureIndex));
		bigramFeatureIndex++;
		
		System.out.println(content.toString());
		PrintWriter pw = new PrintWriter(templateFile);
		pw.println(content.toString());
		pw.close();
		
	}
}
